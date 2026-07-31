pipeline {
    agent any

    parameters {
        choice(name: 'TEST_TO_RUN', choices: ['LoginTest', 'testng.xml', 'enterprise.xml', 'superadmin.xml'], description: 'Select specific test class (e.g. LoginTest) or TestNG XML suite file')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
    }

    environment {
        MAVEN_OPTS = "-Xmx2048m --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out source code...'
                git branch: 'main', url: 'https://github.com/Sonu4bhagat/rcs-ui-automation.git'
            }
        }

        stage('Initialize Environment') {
            steps {
                echo 'Cleaning up leftover processes...'
                bat 'TASKKILL /F /IM chromedriver.exe /T /FI "status eq running" 2>NUL || echo No running chromedriver processes found.'
                bat 'TASKKILL /F /IM chrome.exe /T /FI "status eq running" 2>NUL || echo No running chrome processes found.'
                echo 'Checking Java and Maven versions...'
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Compile Project') {
            steps {
                echo 'Cleaning and Compiling project...'
                bat 'mvn clean compile -DskipTests'
            }
        }

        stage('Execute Automation Suite') {
            steps {
                timeout(time: 180, unit: 'MINUTES') {
                    script {
                        if (params.TEST_TO_RUN.endsWith('.xml')) {
                            echo "Executing TestNG Suite File: ${params.TEST_TO_RUN} (Headless: ${params.HEADLESS})..."
                            bat "mvn test -DsuiteXmlFile=${params.TEST_TO_RUN} -Dbrowser.headless=${params.HEADLESS}"
                        } else {
                            echo "Executing Test Class: ${params.TEST_TO_RUN} (Headless: ${params.HEADLESS})..."
                            bat "mvn test -Dtest=${params.TEST_TO_RUN} -Dbrowser.headless=${params.HEADLESS}"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            // Archive test artifacts first to ensure they are available for the email
            archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
            
            // Parse JUnit results for Jenkins trend charts
            junit testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true

            script {
                echo "DEBUG: Starting post-build script..."
                def totalTests = 0
                def passedTests = 0
                def failedTests = 0
                def skippedTests = 0
                def failedTestNames = []
                
                try {
                    // Check multiple paths for safety, but focus on the main sequential path
                    def paths = [
                        'target/surefire-reports/testng-results.xml'
                    ]
                    
                    for (path in paths) {
                        if (fileExists(path)) {
                            echo "DEBUG: Parsing results at ${path}"
                            def resultsFile = readFile(path)
                            
                            def totalMatch = (resultsFile =~ /total="(\d+)"/)
                            def passedMatch = (resultsFile =~ /passed="(\d+)"/)
                            def failedMatch = (resultsFile =~ /failed="(\d+)"/)
                            def skippedMatch = (resultsFile =~ /skipped="(\d+)"/)
                            
                            if (totalMatch.find()) totalTests += totalMatch.group(1).toInteger()
                            if (passedMatch.find()) passedTests += passedMatch.group(1).toInteger()
                            if (failedMatch.find()) failedTests += failedMatch.group(1).toInteger()
                            if (skippedMatch.find()) skippedTests += skippedMatch.group(1).toInteger()
                            
                            // Extract failed test names
                            def failureMatches = (resultsFile =~ /test-method status="FAIL" name="(\w+)"/)
                            while (failureMatches.find()) {
                                failedTestNames << failureMatches.group(1)
                            }
                        }
                    }
                } catch (Exception e) {
                    echo "ERROR: Could not parse test results: ${e.message}"
                }
                
                echo "DEBUG: Results -> Total: ${totalTests}, Passed: ${passedTests}, Failed: ${failedTests}"
                
                def calculatedStatus = failedTests > 0 ? "FAILURE" : (totalTests > 0 ? "SUCCESS" : currentBuild.currentResult)
                def statusEmoji = failedTests > 0 ? '❌' : '✅'
                def failureSummary = failedTestNames ? "\nFAILED TESTS:\n------------------------------------------\n" + failedTestNames.unique().take(30).join("\n") + (failedTestNames.size() > 30 ? "\n... and ${failedTestNames.size() - 30} more" : "") : ""
                
                // Pass rate calculation
                def passRateStr = "0%"
                if (totalTests > 0) {
                    def pr = (passedTests * 100.0) / totalTests
                    passRateStr = String.format("%.2f", pr) + "%"
                }

                def reportBaseUrl = env.BUILD_URL + "artifact/"
                echo "DEBUG: Report Base URL is ${reportBaseUrl}"
                
                try {
                    echo "DEBUG: Attempting to send email via emailext to aryan.sonu7562@gmail.com, sonu.bhagat@altiquence.com"
                    emailext to: 'aryan.sonu7562@gmail.com, sonu.bhagat@altiquence.com',
                             subject: "${statusEmoji} Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${calculatedStatus}",
                             mimeType: 'text/plain; charset=UTF-8',
                             body: """
==========================================
   RCS Automation Test Report ${statusEmoji}
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
Pass Rate    : ${passRateStr}
${failureSummary}

==========================================
             VIEW REPORTS
==========================================
Extent Report (Detailed):
${reportBaseUrl}test-output/ExtentReport.html

TestNG Index Report:
${reportBaseUrl}target/surefire-reports/index.html

Console Output:
${env.BUILD_URL}console

==========================================
This is an automated email from Jenkins
------------------------------------------
"""
                } catch (Exception e) {
                    echo "ERROR: Failed to send email via emailext: ${e.message}"
                    mail to: 'aryan.sonu7562@gmail.com, sonu.bhagat@altiquence.com',
                         subject: "Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${calculatedStatus}",
                         body: "Test Results - Total: ${totalTests}, Passed: ${passedTests}, Failed: ${failedTests}, Skipped: ${skippedTests}. View: ${env.BUILD_URL}"
                }
            }
        }
    }
}
