@Library('shared-jenkins-library@setup') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { maven 'maven-3.9.16' }

    stages {
        stage('Checkout Repo') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                testApp('maven')
            }
        }

        stage('Build') {
            steps {
                buildApp('maven')
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