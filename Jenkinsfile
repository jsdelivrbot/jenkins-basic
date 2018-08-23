pipeline {
    agent none
    options {
        disableResume()
    }
    stages {
        stage('Build') {
            agent { label 'build' }
            steps {
                script {
                    def hasChangesInPath = sh(script:"[ \"\$(git diff --name-only HEAD~1..HEAD | grep -v '^example/' | wc -l)\" -eq 0 ] && exit 999 || exit 0", returnStatus: true).trim() == 0
                    if (!hasChangesInPath){
                        error("No changes detected in the path (!'^example/')")
                    }
                }
                echo "Aborting all running jobs ..."
                script {
                    abortAllPreviousBuildInProgress(currentBuild)
                }
                echo "Building ..."
            }
        }
    }
}



