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
                
                // Parse JUnit results
                junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            }
        }
    }

    post {
        always {
            echo 'Execution completed'
            
            // Parse test results from testng-results.xml
            script {
                def testClass = 'LoginTest'
                def totalTests = 0
                def passedTests = 0
                def failedTests = 0
                def skippedTests = 0
                
                try {
                    def resultsFile = readFile('target/surefire-reports/testng-results.xml')
                    
                    // Parse total, passed, failed, skipped
                    def totalMatch = (resultsFile =~ /total="(\d+)"/)
                    def passedMatch = (resultsFile =~ /passed="(\d+)"/)
                    def failedMatch = (resultsFile =~ /failed="(\d+)"/)
                    def skippedMatch = (resultsFile =~ /skipped="(\d+)"/)
                    
                    if (totalMatch) totalTests = totalMatch[0][1].toInteger()
                    if (passedMatch) passedTests = passedMatch[0][1].toInteger()
                    if (failedMatch) failedTests = failedMatch[0][1].toInteger()
                    if (skippedMatch) skippedTests = skippedMatch[0][1].toInteger()
                    
                    // Get test class name
                    def classMatch = (resultsFile =~ /class name="tests\.(\w+)"/)
                    if (classMatch) testClass = classMatch[0][1]
                    
                } catch (Exception e) {
                    echo "Could not parse test results: ${e.message}"
                }
                
                // Determine pass/fail status
                def status = failedTests > 0 ? 'FAILED' : 'PASSED'
                def statusEmoji = failedTests > 0 ? '❌' : '✅'
                
                // Send email with test summary
                mail to: 'aryan.sonu7562@gmail.com',
                     subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     body: """
==========================================
   RCS Automation Test Report  ${statusEmoji}
==========================================

BUILD INFORMATION
------------------------------------------
Build Status : ${currentBuild.currentResult}
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Build URL    : ${env.BUILD_URL}

==========================================
         TEST EXECUTION SUMMARY
==========================================

Test Class   : ${testClass}
------------------------------------------
Total Tests  : ${totalTests}
Passed       : ${passedTests} ✅
Failed       : ${failedTests} ❌
Skipped      : ${skippedTests} ⏭️
------------------------------------------
Result       : ${status}

==========================================
            VIEW REPORTS
==========================================

Extent Report (Detailed):
${env.BUILD_URL}artifact/test-output/ExtentReport.html

TestNG Report:
${env.BUILD_URL}artifact/target/surefire-reports/index.html

Build Console Output:
${env.BUILD_URL}console

==========================================
This is an automated email from Jenkins CI/CD
==========================================
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
