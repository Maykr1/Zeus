@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { 
        custom 'flutter'
        maven 'maven-3.9.11' 
    }

    environment {
        // --- DIRECTORIES ---
        BACKEND_DIR         = "../zeus-api" 
        FRONTEND_DIR        = "../zeus-ui"

        // --- APP NAMES ---
        BACKEND_APP_NAME    = "zeus-api"
        FRONTEND_APP_NAME   = "zeus-ui"

        // --- NEXUS CONFIG ---
        NEXUS               = credentials('nexus-deploy')
        NEXUS_BASE          = "https://nexus.ethansclark.com"
        
        // Maven Snapshot Repo
        SNAPSHOT_REPO_ID    = "maven-snapshots"
        SNAPSHOT_REPO       = "${NEXUS_BASE}/repository/${SNAPSHOT_REPO_ID}/"
        
        // Flutter Snapshot Repo
        FLUTTER_SNAPSHOT_REPO_ID = "flutter-snapshots"
        FLUTTER_SNAPSHOT_REPO    = "${NEXUS_BASE}/repository/${FLUTTER_SNAPSHOT_REPO_ID}/"

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
                    when {
                        allOf {
                            expression { env.BACKEND_DIR != "" }
                            anyOf {
                                changeset "${env.BACKEND_DIR}/**" // Simplified since BACKEND_DIR is zeus-api
                                changeset "pom.xml"              // Listen to the ROOT pom
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
                        stage('Publish Snapshot') {
                            steps {
                                script {
                                    env.BACKEND_SNAPSHOT_VERSION = getSnapshotVersion('maven')
                                }
                                dir(env.BACKEND_DIR) {
                                    setVersion('maven', env.BACKEND_SNAPSHOT_VERSION)
                                    containerizeApp('maven', env.BACKEND_APP_NAME, SNAPSHOT_REPO, DOCKER_BASE, env.COMMIT_ID, 'both')
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
                        stage('Publish Snapshot') {
                            steps {
                                script {
                                    env.FRONTEND_SNAPSHOT_VERSION = getSnapshotVersion('flutter')
                                }
                                dir(env.FRONTEND_DIR) {
                                    containerizeApp('flutter', env.FRONTEND_APP_NAME, FLUTTER_SNAPSHOT_REPO, DOCKER_BASE, env.COMMIT_ID, 'both')
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
