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
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    bat 'mvn clean test -Dbrowser.headless=true'
                }
            }
        }

        stage('Archive Reports') {
            steps {
                // Archive test artifacts
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                
                // Parse JUnit results for email
                junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            }
        }
    }

    post {
        always {
            echo 'Execution completed'
            
            // Send email notification with test results
            script {
                def testResult = currentBuild.currentResult
                def totalTests = 0
                def passedTests = 0
                def failedTests = 0
                def skippedTests = 0
                
                // Try to get test results
                try {
                    def testResultAction = currentBuild.rawBuild.getAction(hudson.tasks.junit.TestResultAction.class)
                    if (testResultAction != null) {
                        totalTests = testResultAction.getTotalCount()
                        failedTests = testResultAction.getFailCount()
                        skippedTests = testResultAction.getSkipCount()
                        passedTests = totalTests - failedTests - skippedTests
                    }
                } catch (Exception e) {
                    echo "Could not get test results: ${e.message}"
                }
                
                mail to: 'aryan.sonu7562@gmail.com',
                     subject: "Jenkins Build ${testResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     body: """
======================================
   RCS Automation Test Report
======================================

Build Status: ${testResult}
Job: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Build URL: ${env.BUILD_URL}
Duration: ${currentBuild.durationString}

--------------------------------------
        TEST RESULTS
--------------------------------------
Total Tests: ${totalTests}
Passed: ${passedTests}
Failed: ${failedTests}
Skipped: ${skippedTests}
--------------------------------------

View full report at: ${env.BUILD_URL}

This is an automated email from Jenkins CI/CD.
                     """
            }
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
