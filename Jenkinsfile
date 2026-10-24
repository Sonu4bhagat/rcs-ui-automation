// Set once the test run finishes. The test run itself sends the detailed report email; if the build
// stops before that (checkout/compile error, timeout, abort), post{} sends a short notification instead.
def testsCompleted = false

pipeline {
    agent any

    options {
        // Chrome/chromedriver processes are force-killed at start, so builds must not overlap
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '15'))
    }

    parameters {
        choice(name: 'TEST_TO_RUN', choices: ['testng.xml', 'enterprise.xml', 'reseller.xml', 'superadmin.xml', 'LoginTest'], description: 'testng.xml = all roles; enterprise.xml / reseller.xml / superadmin.xml = one role (tests/<role> folder); LoginTest = single class')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
        booleanParam(name: 'SEND_EMAIL', defaultValue: true, description: 'Send the execution summary email with the Extent report attached')
        string(name: 'EMAIL_RECIPIENTS', defaultValue: 'sonu.bhagat@altiquence.com,aryan.sonu7562@gmail.com', description: 'Comma-separated list of report recipients')
    }

    environment {
        MAVEN_OPTS = "-Xmx2048m"
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
                bat 'mvn -B clean compile -DskipTests'
            }
        }

        stage('Execute Automation Suite') {
            steps {
                timeout(time: 180, unit: 'MINUTES') {
                    script {
                        // Spaces removed and value quoted: cmd.exe treats unquoted commas as argument separators
                        def recipients = params.EMAIL_RECIPIENTS.replaceAll('\\s', '')
                        // maven.test.failure.ignore lets the run finish so the email/report is always produced;
                        // the junit step below then marks the build UNSTABLE when any test fails.
                        def commonArgs = "-B -Dbrowser.headless=${params.HEADLESS} -Dmaven.test.failure.ignore=true " +
                                "\"-Demail.enable=${params.SEND_EMAIL}\" \"-Demail.recipients=${recipients}\""

                        if (params.TEST_TO_RUN.endsWith('.xml')) {
                            echo "Executing TestNG Suite File: ${params.TEST_TO_RUN} (Headless: ${params.HEADLESS})..."
                            bat "mvn test -DsuiteXmlFile=${params.TEST_TO_RUN} ${commonArgs}"
                        } else {
                            echo "Executing Test Class: ${params.TEST_TO_RUN} (Headless: ${params.HEADLESS})..."
                            bat "mvn test -Dtest=${params.TEST_TO_RUN} ${commonArgs}"
                        }
                        testsCompleted = true
                    }
                }
            }
        }
    }

    post {
        always {
            // Archive test artifacts first to ensure they are available for viewing
            archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true

            // Parse JUnit results for Jenkins trend charts (TEST-*.xml only; testng-results.xml is not JUnit format)
            junit testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true

            script {
                if (params.SEND_EMAIL && !testsCompleted) {
                    // Uses Jenkins' own mail settings (Manage Jenkins > System > E-mail Notification).
                    try {
                        mail to: params.EMAIL_RECIPIENTS,
                             charset: 'UTF-8',
                             subject: "[BUILD ERROR] Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER} - automated tests did not complete",
                             body: """RCS Automation - Build Error

The automated test run did not complete, so no test report was produced for this build.
This usually means the build failed before or during the test run (code checkout, compilation,
timeout or a manual abort) - it is not a test result.

Job Name     : ${env.JOB_NAME}
Build Number : #${env.BUILD_NUMBER}
Build Status : ${currentBuild.currentResult}
Test Scope   : ${params.TEST_TO_RUN}
Duration     : ${currentBuild.durationString.replace(' and counting', '')}

Console Output (cause of the error):
${env.BUILD_URL}console

This is an automated email from Jenkins"""
                    } catch (err) {
                        echo "Build-error email not sent (configure Manage Jenkins > System > E-mail Notification): ${err.message}"
                    }
                }
            }
        }
    }
}
