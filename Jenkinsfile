/* This Jenkinsfile simply loads the `reusable_pipeline` pipeline from the
vivariu_build_utils repository (https://github.com/ihmeuw/vivarium_build_utils).

vivarium_build_utils is loaded as a Jenkins shared library 
(https://www.jenkins.io/doc/book/pipeline/shared-libraries/).
Jenkins shared library convention dictates that importable modules must be stored
in the 'vars' folder.

Jenkins shared libraries can be configured in the Jenkins UI:
* Manage Jenkins
  * Configure System
    * Global Pipeline Libraries section
      * Library subsection
        * Name: The Name for the lib
        * Version: The branch you want to use. Throws an error
                   for nonexistent branches.
        * Project Repository: Url to the shared lib
        * Credentials: SSH key to access the repo

Note that updating the shared repo will take affect on the next pipeline invocation.
*/ 

// Load the get_vbu_version function from vivarium_build_utils/bootstrap/
// (the directory to load from is defined in the Jenkins shared library configuration)
@Library("get_vbu_version@main") _

// Load the full vivarium_build_utils library at the expected version
def vbu_version = get_vbu_version()
echo "Loading vivarium_build_utils version: ${vbu_version}"
library("vivarium_build_utils@07ed5760ad0a20bad2594ec5fba8137f0356a27f")

pipeline {
    agent any
    
    options {
        timestamps()
    }
    
    stages {
        stage('Multi-Multibranch Pipeline') {
            steps {
                script {
                    echo "=== STARTING MULTI-MULTIBRANCH PIPELINE ==="
                    echo "Job Name: ${env.JOB_NAME}"
                    echo "Repository URL: ${env.GIT_URL}"
                    echo "Branch: ${env.GIT_BRANCH ?: env.BRANCH_NAME}"
                    echo "VBU Version: ${vbu_version}"
                    
                    // Call the shared library step
                    monorepo()
                    
                    echo "=== MULTI-MULTIBRANCH PIPELINE COMPLETE ==="
                }
            }
        }
    }
}