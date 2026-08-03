pipeline {
    agent any

    parameters {
        choice(name: 'TEST_TO_RUN', choices: ['testng.xml', 'enterprise.xml', 'superadmin.xml', 'LoginTest'], description: 'Select specific test class (e.g. LoginTest) or TestNG XML suite file')
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
            // Archive test artifacts first to ensure they are available for viewing
            archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
            
            // Parse JUnit results for Jenkins trend charts
            junit testResults: 'target/surefire-reports/**/*.xml', allowEmptyResults: true
        }
    }
}
