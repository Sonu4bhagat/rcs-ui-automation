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

        stage('Publish Reports') {
            steps {
                // Archive test reports
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Report'
                ])
                
                // Archive TestNG reports
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'TestNG Report'
                ])
            }
        }
    }

    post {
        always {
            echo 'Execution completed'
            
            // Send email notification
            emailext(
                subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <h2>RCS Automation Test Report</h2>
                    <p><b>Build Status:</b> ${currentBuild.currentResult}</p>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                    <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                    <p><b>Duration:</b> ${currentBuild.durationString}</p>
                    <hr>
                    <p>Check the attached report or visit the build URL for details.</p>
                """,
                to: 'aryan.sonu7562@gmail.com',
                mimeType: 'text/html',
                attachmentsPattern: 'test-output/ExtentReport.html'
            )
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
