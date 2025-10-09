@Library('vivarium_build_utils') _

pipeline {
    agent any
    
    options {
        timestamps()
        ansiColor('xterm')
    }
    
    stages {
        stage('Multi-Multibranch Pipeline') {
            steps {
                script {
                    // Define a logging function that will appear in console
                    def log = { message ->
                        sh "echo '[MONOREPO] ${message}'"
                    }
                    
                    // Pass the logging function to the shared library
                    monorepo([logger: log])
                }
            }
        }
    }
}