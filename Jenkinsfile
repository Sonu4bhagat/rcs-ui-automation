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
            
            // Send email with ExtentReport attached
            emailext(
                subject: "Jenkins Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: '''
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; }
        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
        .header.unstable { background-color: #FF9800; }
        .header.failure { background-color: #f44336; }
        .content { padding: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        .footer { background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>RCS Automation Test Report</h1>
    </div>
    <div class="content">
        <h2>Build Information</h2>
        <table>
            <tr><th>Field</th><th>Value</th></tr>
            <tr><td>Build Status</td><td>${BUILD_STATUS}</td></tr>
            <tr><td>Job Name</td><td>${JOB_NAME}</td></tr>
            <tr><td>Build Number</td><td>#${BUILD_NUMBER}</td></tr>
            <tr><td>Build URL</td><td><a href="${BUILD_URL}">${BUILD_URL}</a></td></tr>
        </table>
        
        <h2>Test Results</h2>
        <p>Please find the detailed <b>Extent Report</b> attached to this email.</p>
        <p>You can also view the full report online at: <a href="${BUILD_URL}artifact/test-output/ExtentReport.html">View Extent Report</a></p>
    </div>
    <div class="footer">
        <p>This is an automated email from Jenkins CI/CD.</p>
    </div>
</body>
</html>
                ''',
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
