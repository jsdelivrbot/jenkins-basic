pipeline {
    agent none
    options {
        disableResume()
    }
    stages {
        stage('Build') {
            agent { label 'build' }
            steps {
                sh "[ \"\$(git diff --name-only HEAD~1..HEAD | grep -v '^example/' | wc -l)\" -eq 0 ] && false || true"
                echo "Aborting all running jobs ..."
                script {
                    abortAllPreviousBuildInProgress(currentBuild)
                }
                echo "Building ..."
            }
        }
    }
}
