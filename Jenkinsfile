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
            agent any

            options {
                buildDiscarder(logRotator(numToKeepStr: '10'))
                timestamps()
                ansiColor('xterm')
                disableConcurrentBuilds()
                timeout(time: 60, unit: 'MINUTES')
            }

            parameters {
                choice(name: 'ENV', choices: ['dev', 'qa', 'stage'], description: 'Target environment')
                string(name: 'TAGS', defaultValue: '', description: 'TestNG groups or Cucumber tags (comma-separated)')
                string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to build')
            }

            environment {
                MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
                ALLURE_RESULTS = 'allure-results'
                ALLURE_REPORT = 'allure-report'
                // Example: CREDENTIALS_ID = credentials('my-jenkins-secret')
            }

            stages {
                stage('Checkout') {
                    steps {
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: "*/${params.BRANCH}"]],
                            userRemoteConfigs: [[url: 'https://github.com/ekailaspatil19051979/RestAssured-data-driven-tests.git']]
                        ])
                    }
                }

                stage('Build') {
                    steps {
                        sh 'mvn clean compile -B'
                    }
                }

                stage('Test Execution') {
                    steps {
                        script {
                            def testCmd = "mvn test -B -Denv=${params.ENV}"
                            if (params.TAGS?.trim()) {
                                // For TestNG groups
                                testCmd += " -Dgroups=${params.TAGS}"
                                // For Cucumber tags (if using cucumber)
                                // testCmd += " -Dcucumber.options=\"--tags ${params.TAGS}\""
                            }
                            // Enable parallel execution via surefire config
                            sh testCmd
                        }
                    }
                    post {
                        always {
                            junit '**/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: '**/target/*.log, **/target/screenshots/**, **/allure-results/**', allowEmptyArchive: true
                        }
                    }
                }

                stage('Report Generation') {
                    steps {
                        // Allure report generation
                        sh 'mvn allure:report'
                        publishHTML(target: [
                            reportName: 'Allure Report',
                            reportDir: "${env.ALLURE_REPORT}",
                            reportFiles: 'index.html',
                            keepAll: true
                        ])
                    }
                }

                stage('Archive Artifacts') {
                    steps {
                        archiveArtifacts artifacts: '**/target/*.log, **/target/screenshots/**, **/allure-results/**, **/allure-report/**', allowEmptyArchive: true
                    }
                }
            }

            post {
                always {
                    cleanWs()
                }
                success {
                    script {
                        currentBuild.result = 'SUCCESS'
                    }
                    // notifySlack('SUCCESS')
                }
                unstable {
                    script {
                        currentBuild.result = 'UNSTABLE'
                    }
                    // notifySlack('UNSTABLE')
                }
                failure {
                    script {
                        currentBuild.result = 'FAILURE'
                    }
                    // notifySlack('FAILURE')
                }
            }
        }
