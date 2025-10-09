// Load shared library from the same repository
library identifier: 'local-monorepo-lib@main', 
        retriever: modernSCM([
            $class: 'GitSCMSource',
            remote: env.GIT_URL,
            credentialsId: 'fad62062-b1f4-447b-997f-005d6b1ea41e'
        ])

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
                    
                    // Call the local shared library step
                    monorepo()
                    
                    echo "=== MULTI-MULTIBRANCH PIPELINE COMPLETE ==="
                }
            }
        }
    }
}