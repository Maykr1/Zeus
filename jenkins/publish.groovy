@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { maven 'maven-3.9.11' }

    environment {
        // --- DIRECTORIES ---
        BBACKEND_DIR        = "zeus-api" 
        FRONTEND_DIR        = "zeus-ui"

        // --- APP NAMES ---
        BACKEND_APP_NAME    = "zeus-api"
        FRONTEND_APP_NAME   = "zeus-ui"


        // --- NEXUS CONFIG ---
        NEXUS               = credentials('nexus-deploy')
        NEXUS_BASE          = "https://nexus.ethansclark.com"
        
        // Maven Release Repo
        RELEASE_REPO_ID     = "maven-releases"
        RELEASE_REPO        = "${NEXUS_BASE}/repository/${RELEASE_REPO_ID}/"
        
        // Flutter Release Repo
        FLUTTER_RELEASE_REPO_ID = "flutter-releases"
        FLUTTER_RELEASE_REPO    = "${NEXUS_BASE}/repository/${FLUTTER_RELEASE_REPO_ID}/"

        // --- DOCKER ---
        DOCKER_BASE         = "localhost:8003"
    }

    stages {
        stage('Checkout Repo') {
            steps {
                checkout scm
            }
        }

        stage('Run Pipelines') {
            parallel {
                // ==========================================
                // BACKEND PIPELINE
                // ==========================================
                stage('Backend') {
                    tools { jdk 'JDK25' }
                    when {
                        allOf {
                            expression { env.BACKEND_DIR != "" }
                            anyOf {
                                changeset "${env.BACKEND_DIR}/**"
                                changeset "pom.xml"
                            }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir(env.BACKEND_DIR) { testApp('maven') }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir(env.BACKEND_DIR) { buildApp('maven') }
                            }
                        }
                        stage('SonarQube') {
                            steps {
                                dir(env.BACKEND_DIR) { sonarApp('maven', env.BACKEND_APP_NAME) }
                            }
                        }
                        stage('Publish Release') {
                            steps {
                                script {
                                    env.BACKEND_RELEASE_VERSION = getReleaseVersion()
                                }
                                dir(env.BACKEND_DIR) {
                                    setVersion('maven', env.BACKEND_RELEASE_VERSION)
                                    containerizeApp('maven', env.BACKEND_APP_NAME, RELEASE_REPO, DOCKER_BASE, env.BACKEND_RELEASE_VERSION, 'both')
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // FRONTEND PIPELINE
                // ==========================================
                stage('Frontend') {
                    when {
                        allOf {
                            expression { env.FRONTEND_DIR != "" }
                            anyOf {
                                changeset "${env.FRONTEND_DIR}/**"
                                changeset "pom.xml"
                            }
                        }
                    }
                    stages {
                        stage('Test') {
                            steps {
                                dir(env.FRONTEND_DIR) { testApp('flutter') }
                            }
                        }
                        stage('Build') {
                            steps {
                                dir(env.FRONTEND_DIR) { buildApp('flutter') }
                            }
                        }
                        stage('SonarQube') {
                            steps {
                                dir(env.FRONTEND_DIR) { sonarApp('flutter', env.FRONTEND_APP_NAME) }
                            }
                        }
                        stage('Publish Release') {
                            steps {
                                script {
                                    env.FRONTEND_RELEASE_VERSION = getReleaseVersion()
                                }
                                dir(env.FRONTEND_DIR) {
                                    containerizeApp('flutter', env.FRONTEND_APP_NAME, FLUTTER_RELEASE_REPO, DOCKER_BASE, env.FRONTEND_RELEASE_VERSION, 'both')
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        success { echo 'Build complete ✅' }
        failure { echo 'Build failed ❌' }
    }
}
