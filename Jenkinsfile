@Library('vivarium_build_utils') _

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
                    echo "Branch: ${env.GIT_BRANCH}"
                    
                    // Call your shared library step
                    monorepo()
                    
                    echo "=== MULTI-MULTIBRANCH PIPELINE COMPLETE ==="
                }
            }
        }
    }
}