@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { maven 'maven-3.9.11' }

    environment {
        // --- DIRECTORIES ---
        BACKEND_DIR         = "zeus-api" 

        // --- APP NAMES ---
        BACKEND_APP_NAME    = "zeus-api"

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

        stage('Test') {
            steps {
                script {
                    testApp('maven', env.BACKEND_DIR)
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    buildSnapshot('maven', env.BACKEND_DIR)
                }
            }
        }

        stage('SonarQube') {
            steps {
                script {
                    sonarApp('maven', env.BACKEND_APP_NAME, env.BACKEND_DIR)
                }
            }
        }

        stage('Publish Snapshot') {
            steps {
                script {
                    env.BACKEND_SNAPSHOT_VERSION = getSnapshotVersion('maven', env.BACKEND_DIR)
                    setVersion('maven', env.BACKEND_SNAPSHOT_VERSION, env.BACKEND_DIR)
                    containerizeApp('maven', env.BACKEND_APP_NAME, SNAPSHOT_REPO, DOCKER_BASE, env.COMMIT_ID, 'both', env.BACKEND_DIR)
                }
            }
        }
    }

    post {
        success { echo 'Build complete ✅' }
        failure { echo 'Build failed ❌' }
    }
}
