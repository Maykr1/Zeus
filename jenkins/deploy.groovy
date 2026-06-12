@Library('shared-jenkins-library@setup') _

pipeline {
    agent any
    options { timestamps(); disableConcurrentBuilds() }
    tools { maven 'maven-3.9.16' }

    parameters {
        string(
            name: 'COMMIT_ID',
            defaultValue: 'latest',
            description: 'Git commit ID to deploy (branch, tag, or sha)'
        )
        choice(name: 'ENVIRONMENT',
            choices: ['dev', 'prod'],
            description: 'Choose which environment to deploy the image to.'
        )
    }

    environment {
        // --- APP META ---
        APP_NAME = 'zeus'

        // --- HARBOR ---
        HARBOR_HOST = 'harbor.ethansclark.com'
        HARBOR_PROJECT = 'olympus-apps'
        HARBOR_CREDENTIALS_ID = 'harbor-registry-credentials'
    }

    stages {
        stage('Pull Image') {
            steps {
                pullImage(
                    imageName: env.APP_NAME,
                    imageTag: params.COMMIT_ID,
                    harborHost: env.HARBOR_HOST,
                    harborProject: env.HARBOR_PROJECT,
                    harborCredentialsId: env.HARBOR_CREDENTIALS_ID
                )
            }
        }

        stage('Deploy') {
            steps {
                deployApp(
                    imageName: env.APP_NAME,
                    imageTag: params.COMMIT_ID,
                    namespace: params.ENVIRONMENT,
                    harborHost: env.HARBOR_HOST,
                    harborProject: env.HARBOR_PROJECT
                )
            }
        }
    }

    post {
        success {
            echo 'Deployment complete ✅' 
        }
        failure {
            echo 'Deployment failed ❌' 
        }
    }
}