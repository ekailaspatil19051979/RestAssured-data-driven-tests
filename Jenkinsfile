pipeline {
    agent any

    // Parameters for environment selection
    parameters {
        choice(name: 'ENV', choices: ['qa', 'uat', 'staging'], description: 'Target environment')
    }

    // Credentials binding for secrets (e.g., API keys, tokens)
    environment {
        // Example: API_TOKEN = credentials('jenkins-api-token-id')
        // Add your credential IDs in Jenkins and reference here
    }

    options {
        // Fail fast on first failure in parallel
        parallelsAlwaysFailFast()
        // Keep build logs clean
        ansiColor('xterm')
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout source code
                checkout scm
            }
        }

        stage('Setup Java & Maven') {
            steps {
                // Use Jenkins tool installers for Java and Maven
                tool name: 'jdk11', type: 'jdk'
                tool name: 'maven3', type: 'maven'
                // Set JAVA_HOME and MAVEN_HOME for later steps
                withEnv(["JAVA_HOME=${tool 'jdk11'}", "PATH+JAVA=${tool 'jdk11'}/bin", "MAVEN_HOME=${tool 'maven3'}", "PATH+MAVEN=${tool 'maven3'}/bin"]) {
                    sh 'java -version'
                    sh 'mvn -version'
                }
            }
        }

        stage('API Health Check') {
            steps {
                // Fail-fast health check (e.g., ping a health endpoint)
                script {
                    def healthUrl = getHealthUrl(params.ENV) // Implement this function in shared library or inline
                    def response = sh(script: "curl -s -o /dev/null -w \"%{http_code}\" ${healthUrl}", returnStdout: true).trim()
                    if (response != '200') {
                        error "API health check failed for ${params.ENV} (HTTP $response)"
                    }
                }
            }
        }

        stage('Run API Tests') {
            parallel {
                stage('Smoke Tests') {
                    steps {
                        withEnv(["JAVA_HOME=${tool 'jdk11'}", "PATH+JAVA=${tool 'jdk11'}/bin", "MAVEN_HOME=${tool 'maven3'}", "PATH+MAVEN=${tool 'maven3'}/bin"]) {
                            // Pass environment and secrets as system properties
                            withCredentials([
                                // Example: string(credentialsId: 'jenkins-api-token-id', variable: 'API_TOKEN')
                            ]) {
                                sh 'mvn test -Dgroups=smoke -Denv=${ENV} -DsuiteXmlFile=testng.xml'
                            }
                        }
                    }
                }
                stage('Regression Tests') {
                    steps {
                        withEnv(["JAVA_HOME=${tool 'jdk11'}", "PATH+JAVA=${tool 'jdk11'}/bin", "MAVEN_HOME=${tool 'maven3'}", "PATH+MAVEN=${tool 'maven3'}/bin"]) {
                            withCredentials([
                                // Example: string(credentialsId: 'jenkins-api-token-id', variable: 'API_TOKEN')
                            ]) {
                                sh 'mvn test -Dgroups=regression -Denv=${ENV} -DsuiteXmlFile=testng.xml'
                            }
                        }
                    }
                }
                // Add more groups as needed
            }
        }

        stage('Publish Reports') {
            steps {
                // Publish TestNG and Allure reports
                junit '**/target/surefire-reports/*.xml'
                allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
            }
        }
    }

    post {
        always {
            // Archive test results and logs
            archiveArtifacts artifacts: '**/target/surefire-reports/*.xml', allowEmptyArchive: true
        }
        failure {
            // Send notification on failure (e.g., email, Slack)
            mail to: 'team@example.com',
                 subject: "API Test Failure: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "Build failed. Check Jenkins for details: ${env.BUILD_URL}"
            // Or use Slack plugin, etc.
        }
    }
}

// Helper function for health check URL (replace with your logic or shared library)
def getHealthUrl(env) {
    switch(env) {
        case 'qa': return 'https://qa.example.com/health'
        case 'uat': return 'https://uat.example.com/health'
        case 'staging': return 'https://staging.example.com/health'
        default: error("Unknown environment: $env")
    }
}
