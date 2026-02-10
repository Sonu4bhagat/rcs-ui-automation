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
                // Kill any orphaned chromedriver processes to prevent resource leaks
                bat 'TASKKILL /F /IM chromedriver.exe /T /FI "status eq running" || echo "No chromedriver found."'
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sonu4bhagat/rcs-ui-automation.git'
            }
        }

        stage('Parallel Test Execution') {
            parallel {
                stage('Enterprise Tests') {
                    steps {
                        timeout(time: 20, unit: 'MINUTES') {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                echo 'Running Enterprise Suite...'
                                bat 'mvn clean test -DsuiteXmlFile=enterprise.xml -Dbrowser.headless=true -Ddataproviderthreadcount=2 -Dtestng.threadcount=2'
                            }
                        }
                    }
                }

                stage('SuperAdmin Tests') {
                    steps {
                        timeout(time: 20, unit: 'MINUTES') {
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                echo 'Running SuperAdmin Suite...'
                                bat 'mvn clean test -DsuiteXmlFile=superadmin.xml -Dbrowser.headless=true -Ddataproviderthreadcount=2 -Dtestng.threadcount=2'
                            }
                        }
                    }
                }
            }
        }

        stage('Archive Results') {
            steps {
                // Archive test artifacts from all forks
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                
                // Parse JUnit results for Jenkins trend charts
                junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            }
        }
    }

    post {
        always {
            script {
                def totalTests = 0
                def passedTests = 0
                def failedTests = 0
                def skippedTests = 0
                def failedTestNames = []
                
                try {
                    // Collect results from all testng-results.xml (handles multiple forks if they exist)
                    def files = findFiles(glob: 'target/surefire-reports/testng-results.xml')
                    for (file in files) {
                        def resultsFile = readFile(file.path)
                        
                        def totalMatch = (resultsFile =~ /total="(\d+)"/)
                        def passedMatch = (resultsFile =~ /passed="(\d+)"/)
                        def failedMatch = (resultsFile =~ /failed="(\d+)"/)
                        def skippedMatch = (resultsFile =~ /skipped="(\d+)"/)
                        
                        if (totalMatch) totalTests += totalMatch[0][1].toInteger()
                        if (passedMatch) passedTests += passedMatch[0][1].toInteger()
                        if (failedMatch) failedTests += failedMatch[0][1].toInteger()
                        if (skippedMatch) skippedTests += skippedMatch[0][1].toInteger()
                        
                        // Extract failed test names for the email summary
                        def failureMatches = (resultsFile =~ /test-method status="FAIL" name="(\w+)"/)
                        for (int i = 0; i < failureMatches.count; i++) {
                            failedTestNames << failureMatches[i][1]
                        }
                    }
                } catch (Exception e) {
                    echo "Could not parse test results: ${e.message}"
                }
                
                def statusEmoji = failedTests > 0 ? '❌' : '✅'
                def failureSummary = failedTestNames ? "\nFAILED TESTS:\n------------------------------------------\n" + failedTestNames.unique().join("\n") : ""
                
                mail to: 'aryan.sonu7562@gmail.com, sonu.bhagat@altiquence.com',
                     subject: "${statusEmoji} Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                     body: """
==========================================
   RCS Automation Test Report  ${statusEmoji}
==========================================

BUILD INFORMATION
------------------------------------------
Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Status       : ${currentBuild.currentResult}
Duration     : ${currentBuild.durationString}
Build URL    : ${env.BUILD_URL}

==========================================
         TEST EXECUTION SUMMARY
==========================================
Total Tests  : ${totalTests}
Passed       : ${passedTests} ✅
Failed       : ${failedTests} ❌
Skipped      : ${skippedTests} ⏭️
Pass Rate    : ${totalTests > 0 ? (passedTests * 100 / totalTests).setScale(1, BigDecimal.ROUND_HALF_UP) : 0}%
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
            }
        }
    }
}
