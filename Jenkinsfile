pipeline {
    agent any

    options {
        // Chrome/chromedriver processes are force-killed at start, so builds must not overlap
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '15'))
    }

    parameters {
        choice(name: 'TEST_TO_RUN', choices: ['testng.xml', 'enterprise.xml', 'superadmin.xml', 'LoginTest'], description: 'Select specific test class (e.g. LoginTest) or TestNG XML suite file')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
        booleanParam(name: 'SEND_EMAIL', defaultValue: true, description: 'Send the execution summary email with the Extent report attached')
        string(name: 'EMAIL_RECIPIENTS', defaultValue: 'sonu.bhagat@altiquence.com,aryan.sonu7562@gmail.com', description: 'Comma-separated list of report recipients')
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
        }
    }
}
