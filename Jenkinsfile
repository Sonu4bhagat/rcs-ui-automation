// Set once the test run finishes. The test run itself sends the detailed report email; if the build
// stops before that (checkout/compile error, timeout, abort), post{} sends a short notification instead.
def testsCompleted = false

// Description of what this build runs, e.g. "Enterprise + Reseller" (shown in build history and emails)
def testScope = ''

// Tick-box parameter -> TestNG suite file (one folder per user role under src/test/java/tests)
def TEST_GROUPS = [
    [param: 'COMMON',      name: 'Common',      file: 'common.xml'],
    [param: 'ENTERPRISE',  name: 'Enterprise',  file: 'enterprise.xml'],
    [param: 'RESELLER',    name: 'Reseller',    file: 'reseller.xml'],
    [param: 'SUPER_ADMIN', name: 'Super Admin', file: 'superadmin.xml']
]

pipeline {
    agent any

    options {
        // Chrome/chromedriver processes are force-killed at start, so builds must not overlap
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '15'))
    }

    parameters {
        // Tick one or more groups - all ticked groups run together in this one build (one report, one email)
        booleanParam(name: 'ALL_TESTS',   defaultValue: false, description: 'Run ALL test cases (every portal). Overrides the boxes below.')
        booleanParam(name: 'COMMON',      defaultValue: false, description: 'Common tests - covering all roles, e.g. login (tests/common)')
        booleanParam(name: 'ENTERPRISE',  defaultValue: false, description: 'Enterprise portal tests (tests/enterprise)')
        booleanParam(name: 'RESELLER',    defaultValue: false, description: 'Reseller portal tests (tests/reseller)')
        booleanParam(name: 'SUPER_ADMIN', defaultValue: false, description: 'Super Admin portal tests (tests/superadmin)')
        string(name: 'TEST_CLASS', defaultValue: '',
               description: 'Optional - run only specific class(es) instead of the boxes above. e.g. LoginTest  |  LoginTest,SuperAdminDashboardTest  |  LoginTest#testSuperAdminLogin. Leave empty to use the boxes. If nothing is ticked and this is empty, ALL tests run.')
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

                        def testClass = (params.TEST_CLASS ?: '').trim()
                        def mvnTarget

                        if (testClass) {
                            // Only class/method names - the value is passed to cmd.exe
                            if (!(testClass ==~ /[A-Za-z0-9_.#,*+]+/)) {
                                error "TEST_CLASS is invalid: '${testClass}'. Example: LoginTest or LoginTest,SuperAdminDashboardTest"
                            }
                            testScope = "Test class(es): ${testClass}"
                            mvnTarget = "-Dtest=${testClass}"
                        } else {
                            def selected = []
                            for (group in TEST_GROUPS) {
                                if (params[group.param]) {
                                    selected << group
                                }
                            }

                            def allTests = params.ALL_TESTS || selected.isEmpty()
                            if (allTests) {
                                // Same group files as ticking every box, so "all" runs the roles in parallel too
                                selected = TEST_GROUPS
                            }

                            if (selected.size() == 1) {
                                testScope = selected[0].name
                                mvnTarget = "-DsuiteXmlFile=${selected[0].file}"
                            } else {
                                // Several groups: one parent suite over the groups' suite files, so the build
                                // produces a single report and a single email. utils.RoleSuitesInParallel runs
                                // the role suites (one account each) at the same time and Common on its own.
                                def names = []
                                def suiteFiles = ''
                                for (group in selected) {
                                    names << group.name
                                    suiteFiles += "        <suite-file path=\"${group.file}\"/>\n"
                                }
                                testScope = allTests ? 'All Tests' : names.join(' + ')
                                writeFile file: 'selected-suites.xml', text: """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<!-- Generated by the Jenkins build for: ${testScope} -->
<suite name="Selected: ${testScope}">
    <listeners>
        <listener class-name="utils.RoleSuitesInParallel"/>
    </listeners>
    <suite-files>
${suiteFiles}    </suite-files>
</suite>
"""
                                mvnTarget = '-DsuiteXmlFile=selected-suites.xml'
                            }
                        }

                        currentBuild.description = testScope
                        echo "Running: ${testScope} (Headless: ${params.HEADLESS})"
                        // TEST_SCOPE is read by the report email ("Test Scope" line)
                        withEnv(["TEST_SCOPE=${testScope}"]) {
                            bat "mvn test ${mvnTarget} ${commonArgs}"
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
Test Scope   : ${testScope ?: 'not determined - the build stopped before the test stage'}
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
