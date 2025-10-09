import hudson.model.*

/**
 * Provision items on Jenkins.
 * @param rootFolderPath A root folder path.
 * @param repositoryURL The repository URL.
 * @return The list of Jenkinsfile paths for which corresponding items have been provisioned.
 */
List<String> provisionItems(String rootFolderPath, String repositoryURL) {
    println "Discovering Jenkinsfiles with pattern '**/*/Jenkinsfile'..."
    
    // Find all Jenkinsfiles.
    List<String> jenkinsfilePaths = findFiles(glob: '**/*/Jenkinsfile').collect { it.path }
    println "Discovered ${jenkinsfilePaths.size()} Jenkinsfile(s)"

    println "Executing Job DSL to provision Jenkins items..."
    // Provision folder and Multibranch Pipelines.
    jobDsl(
            scriptText: libraryResource('multiPipelines.groovy'),
            additionalParameters: [
                    jenkinsfilePathsStr: jenkinsfilePaths,
                    rootFolderStr      : rootFolderPath,
                    repositoryURL      : repositoryURL
            ],
            // The following may be set to 'DELETE'. Note that branches will compete to delete and recreate items
            // unless you only provision items from the default branch.
            removedJobAction: 'IGNORE'
    )
    println "Job DSL execution completed"

    return jenkinsfilePaths
}

/**
 * Get the most relevant baseline revision.
 * @return A revision.
 */
String getBaselineRevision() {
    println "Determining baseline revision for change detection..."
    
    // Depending on your seed pipeline configuration and preferences, you can set the baseline revision to a target
    // branch, e.g. the repository's default branch or even `env.CHANGE_TARGET` if Jenkins is configured to discover
    // pull requests.
    def baseline = [env.GIT_PREVIOUS_SUCCESSFUL_COMMIT, env.GIT_PREVIOUS_COMMIT]
    // Look for the first existing existing revision. Commits can be removed (e.g. with a `git push --force`), so a
    // previous build revision may not exist anymore.
            .find { revision ->
                revision != null && sh(script: "git rev-parse --quiet --verify $revision", returnStatus: true) == 0
            } ?: 'HEAD^'
    
    println "Using baseline revision: ${baseline}"
    return baseline
}

/**
 * Get the list of changed directories.
 * @param baselineRevision A revision to compare to the current revision.
 * @return The list of directories which include changes.
 */
List<String> getChangedDirectories(String baselineRevision) {
    println "Getting changed directories compared to: ${baselineRevision}"
    
    // Jenkins native interface to retrieve changes, i.e. `currentBuild.changeSets`, returns an empty list for newly
    // created branches (see https://issues.jenkins.io/browse/JENKINS-14138), so let's use `git` instead.
    def result = sh(
            label: 'List changed directories',
            script: "git diff --name-only ${baselineRevision} | xargs -L1 dirname | uniq || echo ''",
            returnStdout: true,
    ).trim()
    
    return result ? result.split().toList() : []
}

/**
 * Find Multibranch Pipelines which Jenkinsfile is located in a directory that includes changes.
 * @param changedFilesPathStr List of changed files paths.
 * @param jenkinsfilePathsStr List of Jenkinsfile paths.
 * @return A list of Pipeline names, relative to the repository root.
 */
List<String> findRelevantMultibranchPipelines(List<String> changedFilesPathStr, List<String> jenkinsfilePathsStr) {
    def result = []
    
    for (String changedFilePath : changedFilesPathStr) {
        for (String jenkinsfilePath : jenkinsfilePathsStr) {
            // Extract the directory containing the Jenkinsfile
            def jenkinsfileDir = jenkinsfilePath.replaceAll('/Jenkinsfile$', '')
            
            // Check if the changed file is in or under the Jenkinsfile directory
            if (changedFilePath.startsWith(jenkinsfileDir + '/') || changedFilePath == jenkinsfileDir) {
                if (!result.contains(jenkinsfileDir)) {
                    result.add(jenkinsfileDir)
                }
            }
        }
    }
    
    return result
}

/**
 * Get the list of Multibranch Pipelines that should be run according to the changeset.
 * @param jenkinsfilePaths The list of Jenkinsfiles paths.
 * @return The list of Multibranch Pipelines to run relative to the repository root.
 */
List<String> findMultibranchPipelinesToRun(List<String> jenkinsfilePaths) {
    String baselineRevision = getBaselineRevision()
    List<String> changedDirectories = getChangedDirectories(baselineRevision)
    return findRelevantMultibranchPipelines(changedDirectories, jenkinsfilePaths)
}

/**
 * Run pipelines.
 * @param rootFolderPath The common root folder of Multibranch Pipelines.
 * @param multibranchPipelinesToRun The list of Multibranch Pipelines for which a Pipeline is run.
 */
def runPipelines(String rootFolderPath, List<String> multibranchPipelinesToRun) {
    if (multibranchPipelinesToRun.isEmpty()) {
        println "No pipelines to run - exiting"
        return
    }
    
    println "Preparing to run ${multibranchPipelinesToRun.size()} pipeline(s) in parallel"
    
    parallel(multibranchPipelinesToRun.inject([:]) { stages, multibranchPipelineToRun ->
        stages + [("Build ${multibranchPipelineToRun}"): {
            def branchName = env.CHANGE_BRANCH ?: env.GIT_BRANCH
            def encodedBranch = URLEncoder.encode(branchName, 'UTF-8')
            def pipelineName = "${rootFolderPath}/${multibranchPipelineToRun}/${encodedBranch}"
            
            println "Triggering pipeline: ${pipelineName}"
            
            // For new branches, Jenkins will receive an event from the version control system to provision the
            // corresponding Pipeline under the Multibranch Pipeline item. We have to wait for Jenkins to process the
            // event so a build can be triggered.
            println "Waiting for pipeline to become available..."
            timeout(time: 5, unit: 'MINUTES') {
                waitUntil(initialRecurrencePeriod: 1e3) {
                    def pipeline = Jenkins.instance.getItemByFullName(pipelineName)
                    if (pipeline && !pipeline.isDisabled()) {
                        println "Pipeline ${pipelineName} is ready"
                        return true
                    }
                    return false
                }
            }

            println "Starting build for: ${pipelineName}"
            // Trigger downstream builds.
            build(job: pipelineName, propagate: true, wait: true)
            println "Completed build for: ${pipelineName}"
        }]
    })
    
    println "All downstream builds completed"
}

/**
 * The step entry point.
 */
def call(Map config = [:]){
    println "=== Multi-Multibranch Pipeline Execution ==="
    
    String repositoryName = env.JOB_NAME.split('/')[1]
    println "Job Name: ${env.JOB_NAME}"
    println "Repository Name: ${repositoryName}"
    
    String rootFolderPath = "Generated/$repositoryName"
    println "Root Folder Path: ${rootFolderPath}"
    println "Repository URL: ${env.GIT_URL}"

    println ""
    println "=== Step 1: Provisioning Jenkins Items ==="
    List<String> jenkinsfilePaths = provisionItems(rootFolderPath, env.GIT_URL)
    println "Found ${jenkinsfilePaths.size()} Jenkinsfile(s):"
    jenkinsfilePaths.each { path ->
        println "  - ${path}"
    }

    println ""
    println "=== Step 2: Detecting Changes ==="
    List<String> multibranchPipelinesToRun = findMultibranchPipelinesToRun(jenkinsfilePaths)
    
    if (multibranchPipelinesToRun.isEmpty()) {
        println "No relevant changes detected - skipping downstream builds"
        return
    }
    
    println "Changes detected affecting ${multibranchPipelinesToRun.size()} pipeline(s):"
    multibranchPipelinesToRun.each { pipeline ->
        println "  - ${pipeline}"
    }

    println ""
    println "=== Step 3: Triggering Downstream Builds ==="
    runPipelines(rootFolderPath, multibranchPipelinesToRun)
    
    println ""
    println "=== Multi-Multibranch Pipeline Complete ==="
}
