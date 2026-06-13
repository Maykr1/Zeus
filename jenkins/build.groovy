@Library('shared-jenkins-library@refactor') _

pipeline {
    agent {
        kubernetes {
            yaml buildPod()
        }
    }
    
    options { 
        timestamps() 
        disableConcurrentBuilds() 
    }
    
    environment {
        // --- APP META ---
        APP_NAME        = 'zeus'

        // --- HARBOR ---
        HARBOR_HOST     = 'harbor.ethansclark.com'
        HARBOR_PROJECT  = 'olympus-apps'
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
                    imageName:      env.APP_NAME, 
                    harborHost:     env.HARBOR_HOST,
                    harborProject:  env.HARBOR_PROJECT
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