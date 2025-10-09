// Job DSL script for multi-multibranch pipeline execution
// Use this file in Jenkins Freestyle job "Process Job DSLs" build step

println "=== Multi-Multibranch Pipeline DSL Script Starting ==="

// Get build variables
def build = Thread.currentThread().executable
def workspace = build.workspace
def env = build.environment

println "Build Number: ${build.number}"
println "Workspace: ${workspace}"

// Get repository information
def gitUrl = env.get('GIT_URL') ?: 'https://github.com/ihmeuw/sim_sci_test_monorepo'
def jobName = env.get('JOB_NAME') ?: 'seed'

println "Job Name: ${jobName}"  
println "Repository URL: ${gitUrl}"

// Extract repository name
def repositoryName = gitUrl.split('/').last().replaceAll('\\.git$', '')
println "Repository Name: ${repositoryName}"

def rootFolderPath = "Generated/${repositoryName}"
println "Root Folder Path: ${rootFolderPath}"

println "=== Step 1: Discovering Jenkinsfiles ==="

// Define the Jenkinsfiles we expect to find
def jenkinsfilePaths = [
    'libs/core/Jenkinsfile',
    'libs/public_health/Jenkinsfile'
]

println "Processing ${jenkinsfilePaths.size()} Jenkinsfile(s):"
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

// Create subfolders for each library if needed
def subfolders = jenkinsfilePaths.collect { it.split('/')[0] }.unique()
subfolders.each { subfolder ->
    def folderPath = "${rootFolderPath}/${subfolder}"
    folder(folderPath) {
        displayName(subfolder)
        description("Jobs for ${subfolder} library")
    }
    println "Created subfolder: ${folderPath}"
}

println "=== Step 3: Creating Multibranch Pipelines ==="

jenkinsfilePaths.each { jenkinsfilePath ->
    def pipelineName = jenkinsfilePath.replaceAll('/Jenkinsfile$', '')
    def fullJobName = "${rootFolderPath}/${pipelineName}"
    
    println "Creating multibranch pipeline: ${fullJobName}"
    
    multibranchPipelineJob(fullJobName) {
        displayName(pipelineName.split('/').last())
        description("Auto-generated multibranch pipeline for ${pipelineName}")
        
        branchSources {
            github {
                id('github-source')
                repoOwner('ihmeuw') 
                repository(repositoryName)
                credentialsId('fad62062-b1f4-447b-997f-005d6b1ea41e') // Your GitHub credentials
                
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
        
        // Configure branch and PR discovery
        configure { node ->
            def traits = node / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
            
            // Branch discovery - exclude branches that are also PRs
            traits << 'org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait' {
                strategyId(1)
            }
            
            // Pull request discovery - merge with target
            traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
                strategyId(1)
            }
            
            // Clean before checkout
            traits << 'jenkins.plugins.git.traits.CleanBeforeCheckoutTrait'()
            
            // Checkout over SSH
            traits << 'jenkins.plugins.git.traits.CloneOptionTrait' {
                extension(class: 'hudson.plugins.git.extensions.impl.CloneOption') {
                    shallow(false)
                    noTags(false)
                    reference('')
                    timeout(10)
                }
            }
        }
        
        // Scan triggers
        triggers {
            periodicFolderTrigger {
                interval('1h')
            }
        }
    }
    
    println "✓ Created: ${fullJobName}"
}

println "=== Step 4: Summary ==="
println "Successfully created:"
println "- 1 root folder: ${rootFolderPath}"
println "- ${subfolders.size()} subfolders: ${subfolders.join(', ')}"
println "- ${jenkinsfilePaths.size()} multibranch pipelines"

println "=== Multi-Multibranch Pipeline DSL Script Complete ==="