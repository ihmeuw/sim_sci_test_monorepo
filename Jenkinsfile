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
                    monorepo()
                }
            }
        }
    }
}