@Library('shared-jenkins-library@refactor') _

pipeline {
    agent {
        kubernetes {
            yaml deployPod()
        }
    }

    options { 
        timestamps() 
        disableConcurrentBuilds() 
    }

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
        HARBOR_CREDENTIALS_ID = 'harbor-olympus-puller'
        CHART_VERSION = '0.1.0'
    }

    stages {
        stage('Deploy') {
            steps {
                deployApp(
                    imageName: env.APP_NAME,
                    imageTag: params.COMMIT_ID,
                    namespace: params.ENVIRONMENT,
                    harborHost: env.HARBOR_HOST,
                    harborProject: env.HARBOR_PROJECT,
                    harborCredentialsId: env.HARBOR_CREDENTIALS_ID,
                    chartVersion: env.CHART_VERSION
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