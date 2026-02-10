pipeline {
    agent any

    tools {
        jdk 'JDK_17'
        maven 'Maven_3'
    }

    environment {
        MAVEN_OPTS = "-Xmx2048m -Xms512m"
    }

    stages {
        stage('Initialize') {
            steps {
                echo 'Cleaning up environment...'
                bat 'TASKKILL /F /IM chromedriver.exe /T /FI "status eq running" || echo "No chromedriver found."'
                echo 'Cleaning and Compiling project...'
                bat 'mvn clean compile -DskipTests'
                echo 'Checking connectivity to target URL...'
                bat 'curl -I https://stagingvault.smartping.io/login || echo "Warning: Could not reach target URL"'
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sonu4bhagat/rcs-ui-automation.git'
            }
        }

        stage('Debug Test Run') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    echo 'Running LoginTest only for debugging...'
                    bat 'mvn test -Dtest=tests.LoginTest -Dbrowser.headless=true'
                }
            }
        }

        stage('Archive Results') {
            steps {
                // Archive test artifacts from all forks
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                
                // Parse JUnit results for Jenkins trend charts
                junit testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true
            }
        }
    }

    post {
        always {
            script {
                echo "DEBUG: Starting post-build script..."
                def totalTests = 0
                def passedTests = 0
                def failedTests = 0
                def skippedTests = 0
                def failedTestNames = []
                
                try {
                    // Use fileExists instead of findFiles to avoid plugin dependency
                    def resultsPath = 'target/surefire-reports/testng-results.xml'
                    
                    if (fileExists(resultsPath)) {
                        echo "DEBUG: Found results file at ${resultsPath}"
                        def resultsFile = readFile(resultsPath)
                        
                        def totalMatch = (resultsFile =~ /total="(\d+)"/)
                        def passedMatch = (resultsFile =~ /passed="(\d+)"/)
                        def failedMatch = (resultsFile =~ /failed="(\d+)"/)
                        def skippedMatch = (resultsFile =~ /skipped="(\d+)"/)
                        
                        if (totalMatch.find()) totalTests = totalMatch.group(1).toInteger()
                        if (passedMatch.find()) passedTests = passedMatch.group(1).toInteger()
                        if (failedMatch.find()) failedTests = failedMatch.group(1).toInteger()
                        if (skippedMatch.find()) skippedTests = skippedMatch.group(1).toInteger()
                        
                        // Extract failed test names
                        def failureMatches = (resultsFile =~ /test-method status="FAIL" name="(\w+)"/)
                        while (failureMatches.find()) {
                            failedTestNames << failureMatches.group(1)
                        }
                    } else {
                        echo "WARNING: testng-results.xml not found in target/surefire-reports/"
                    }
                } catch (Exception e) {
                    echo "ERROR: Could not parse test results: ${e.message}"
                }
                
                echo "DEBUG: Results -> Total: ${totalTests}, Passed: ${passedTests}, Failed: ${failedTests}"
                
                def calculatedStatus = failedTests > 0 ? "FAILURE" : (totalTests > 0 ? "SUCCESS" : currentBuild.currentResult)
                def statusEmoji = failedTests > 0 ? '❌' : '✅'
                def failureSummary = failedTestNames ? "\nFAILED TESTS:\n------------------------------------------\n" + failedTestNames.unique().take(30).join("\n") + (failedTestNames.size() > 30 ? "\n... and ${failedTestNames.size() - 30} more" : "") : ""
                
                // Simple pass rate calculation for sandbox compatibility
                def passRate = 0
                if (totalTests > 0) {
                    passRate = (passedTests * 100) / totalTests
                }

                try {
                    echo "DEBUG: Attempting to send email to aryan.sonu7562@gmail.com, sonu.bhagat@altiquence.com"
                    mail to: 'aryan.sonu7562@gmail.com, sonu.bhagat@altiquence.com',
                         subject: "${statusEmoji} Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${calculatedStatus}",
                         body: """
==========================================
   RCS Automation Test Report  ${statusEmoji}
==========================================

BUILD INFORMATION
------------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : ${calculatedStatus}
Duration     : ${currentBuild.durationString}
Build URL    : ${env.BUILD_URL}

==========================================
         TEST EXECUTION SUMMARY
==========================================
Total Tests  : ${totalTests}
Passed       : ${passedTests} ✅
Failed       : ${failedTests} ❌
Skipped      : ${skippedTests} ⏭️
Pass Rate    : ${passRate}%
${failureSummary}

==========================================
             VIEW REPORTS
==========================================
Extent Report (Detailed):
${env.BUILD_URL}artifact/test-output/ExtentReport.html

TestNG Index Report:
${env.BUILD_URL}artifact/target/surefire-reports/index.html

Console Output:
${env.BUILD_URL}console

==========================================
This is an automated email from Jenkins
==========================================
"""
                    echo "DEBUG: Email sent successfully."
                } catch (Exception e) {
                    echo "ERROR: Failed to send email: ${e.message}"
                }
            }
        }
    }
}
