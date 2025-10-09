// Job DSL script for multi-multibranch pipeline execution
// This should be used in a Freestyle job with "Process Job DSLs" build step

println "=== Multi-Multibranch Pipeline DSL Script Starting ==="

// Get environment variables (available in Job DSL context)
def jobName = binding.variables.get('JOB_NAME') ?: 'unknown'
def gitUrl = binding.variables.get('GIT_URL') ?: 'unknown'

println "Job Name: ${jobName}"
println "Repository URL: ${gitUrl}"

// Extract repository name from job name or URL
def repositoryName = jobName.split('/').size() > 1 ? jobName.split('/')[1] : 'sim_sci_test_monorepo'
println "Repository Name: ${repositoryName}"

def rootFolderPath = "Generated/${repositoryName}"
println "Root Folder Path: ${rootFolderPath}"

println "=== Step 1: Discovering Jenkinsfiles ==="

// In Job DSL context, we need to use different methods to find files
// We'll create the job structure based on known paths
def jenkinsfilePaths = [
    'libs/core/Jenkinsfile',
    'libs/public_health/Jenkinsfile'
]

println "Found ${jenkinsfilePaths.size()} Jenkinsfile(s):"
jenkinsfilePaths.each { path ->
    println "  - ${path}"
}

println "=== Step 2: Creating Folder Structure ==="

// Create the root folder
folder(rootFolderPath) {
    displayName("Generated Jobs for ${repositoryName}")
    description("Auto-generated folder for ${repositoryName} multibranch pipelines")
}

println "Created root folder: ${rootFolderPath}"

println "=== Step 3: Creating Multibranch Pipelines ==="

jenkinsfilePaths.each { jenkinsfilePath ->
    def pipelineName = jenkinsfilePath.replaceAll('/Jenkinsfile$', '')
    def fullJobName = "${rootFolderPath}/${pipelineName}"
    
    println "Creating multibranch pipeline: ${fullJobName}"
    
    multibranchPipelineJob(fullJobName) {
        displayName(pipelineName)
        description("Auto-generated multibranch pipeline for ${pipelineName}")
        
        branchSources {
            github {
                id('github-source')
                repoOwner('ihmeuw')
                repository(repositoryName)
                
                buildOriginBranch(true)
                buildOriginBranchWithPR(false)
                buildOriginPRMerge(false)
                buildOriginPRHead(true)
                buildForkPRMerge(false)
                buildForkPRHead(false)
            }
        }
        
        factory {
            workflowBranchProjectFactory {
                scriptPath(jenkinsfilePath)
            }
        }
        
        configure { node ->
            def traits = node / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
            
            // Add branch discovery trait
            traits << 'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
                strategyId(1) // Exclude branches that are also filed as PRs
            }
            
            // Add pull request discovery trait  
            traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
                strategyId(1) // Merging the pull request with the current target branch revision
            }
        }
    }
}

println "=== Step 4: Job Creation Complete ==="
println "Created ${jenkinsfilePaths.size()} multibranch pipeline jobs"

println "=== Multi-Multibranch Pipeline DSL Script Complete ==="