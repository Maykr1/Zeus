@Library('shared-jenkins-library@setup') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { 
        maven 'maven-3.9.16' 
        docker 'docker' 
    }

    environment {
        // --- APP META ---
        APP_NAME = 'Zeus'

        // --- HARBOR ---
        HARBOR_HOST = 'harbor.ethansclark.com'
        HARBOR_PROJECT = 'olympus-apps'
        HARBOR_CREDENTIALS_ID = 'harbor-registry-credentials'
    }

    stages {
        stage('Checkout Repo') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                testApp(buildTool: 'maven')
            }
        }

        stage('SonarQube') {
            steps {
                sonarApp(
                    buildTool: 'maven', 
                    appName: env.APP_NAME
                )
            }
        }

        stage('Build') {
            steps {
                buildApp(buildTool: 'maven')
            }
        }

        stage('Containerize') {
            steps {
                containerizeApp(
                    imageName: env.APP_NAME, 
                    harborHost: env.HARBOR_HOST,
                    harborProject: env.HARBOR_PROJECT,
                    harborCredentialsId: env.HARBOR_CREDENTIALS_ID
                )
            }
        }
    }

    post {
        success {
            echo 'Build complete ✅' 
        }
        failure {
            echo 'Build failed ❌' 
        }
    }
}