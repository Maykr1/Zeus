@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { 
        maven 'maven-3.9.11' 
        jdk 'JDK25' 
    }

    environment {
        // --- DIRECTORIES ---
        BBACKEND_DIR        = "zeus-api" 

        // --- APP NAMES ---
        BACKEND_APP_NAME    = "zeus-api"


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
                    buildRelease('maven', env.BACKEND_DIR)
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

        stage('Publish Release') {
            steps {
                script {
                    env.BACKEND_RELEASE_VERSION = getReleaseVersion('maven', env.BACKEND_DIR)
                    setVersion('maven', env.BACKEND_RELEASE_VERSION, env.BACKEND_DIR)
                    containerizeApp('maven', env.BACKEND_APP_NAME, RELEASE_REPO, DOCKER_BASE, env.BACKEND_RELEASE_VERSION, 'both', env.BACKEND_DIR)
                }
            }
        }
    }

    post {
        success { echo 'Build complete ✅' }
        failure { echo 'Build failed ❌' }
    }
}
