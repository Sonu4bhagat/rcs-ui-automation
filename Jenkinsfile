pipeline {
    agent any

    tools {
        jdk 'JDK_17'
        maven 'Maven_3'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sonu4bhagat/rcs-ui-automation.git'
            }
        }

        stage('Build & Run Tests') {
            steps {
                // Enable headless mode for CI/CD execution
                // Using catchError to continue even if tests fail
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    bat 'mvn clean test -Dbrowser.headless=true'
                }
            }
        }

        stage('Archive Reports') {
            steps {
                // Archive test artifacts
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            echo 'Execution completed'
            
            // Send email notification using basic mail
            mail to: 'aryan.sonu7562@gmail.com',
                 subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """
RCS Automation Test Report
===========================
Build Status: ${currentBuild.currentResult}
Job: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Build URL: ${env.BUILD_URL}

Check the build URL for detailed reports.
                 """
        }
        success {
            echo 'Automation Passed ✅'
        }
        failure {
            echo 'Automation Failed ❌'
        }
        unstable {
            echo 'Automation Unstable ⚠️ (Some tests failed)'
        }
    }
}
