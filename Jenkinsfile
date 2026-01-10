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
                    url: 'YOUR_REPO_URL_HERE'
            }
        }

        stage('Build & Run Tests') {
            steps {
                // Enable headless mode for CI/CD execution
                bat 'mvn clean test -Dbrowser.headless=true'
            }
        }

        stage('Publish Reports') {
            steps {
                // Archive test reports
                publishHTML(target: [
                    allowMissing: false,
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
            // Clean workspace after build (optional)
            // cleanWs()
        }
        success {
            echo 'Automation Passed ✅'
        }
        failure {
            echo 'Automation Failed ❌'
        }
    }
}
