@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps() }

    environment {
        // --- APP NAMES ---
        BACKEND_APP_NAME    = "task-aggregator-api"
        FRONTEND_APP_NAME   = "" // Leave empty if not using

        ACTIVE_PROFILE      = "docker"

        // --- DOCKER ---
        COMPOSE_DIR         = '/deploy'
        DOCKER_REG          = 'localhost:8003'
        REG_CRED_ID         = 'nexus-deploy'
        
        // Tags
        IMAGE_TAG           = "${params.COMMIT_ID ?: 'latest'}"
        
        PRUNE_MODE          = "${params.PRUNE_MODE ?: 'none'}"
    }

    parameters {
        string(
            name: 'COMMIT_ID',
            defaultValue: 'latest',
            description: 'Git commit ID to deploy (branch, tag, or sha)'
        )
        choice(name: 'PRUNE_MODE',
            choices: ['none', 'dangling', 'all'],
            description: 'Choose how aggressively to prune docker containers, volumes, etc.'
        )
    }

    stages {
        stage("Pull Secrets") {
            steps {
                script {
                    if (env.BACKEND_APP_NAME) { pullSecrets(env.BACKEND_APP_NAME) }
                    if (env.FRONTEND_APP_NAME) { pullSecrets(env.FRONTEND_APP_NAME) }
                }
            }
        }

        stage('Login to Registry') {
            steps {
                login(env.REG_CRED_ID, env.DOCKER_REG)
            }
        }

        stage('Deploy Applications') {
            parallel {
                stage('Backend') {
                    when { expression { env.BACKEND_APP_NAME != "" } }
                    steps {
                        deployApp("${env.DOCKER_REG}/${env.BACKEND_APP_NAME}:${env.IMAGE_TAG}", env.COMPOSE_DIR, env.IMAGE_TAG, env.BACKEND_APP_NAME)
                    }
                }
                stage('Frontend') {
                    when { expression { env.FRONTEND_APP_NAME != "" } }
                    steps {
                        deployApp("${env.DOCKER_REG}/${env.FRONTEND_APP_NAME}:${env.IMAGE_TAG}", env.COMPOSE_DIR, env.IMAGE_TAG, env.FRONTEND_APP_NAME)
                    }
                }
            }
        }

        stage('Cleanup') {
            when { expression { env.PRUNE_MODE != 'none' } }
            steps {
                cleanupServer(env.PRUNE_MODE)
            }
        }

        stage('Logout') {
            steps {
                logout(env.DOCKER_REG)
            }
        }
        
    }

    post {
        success { echo "✅ Successfully deployed applications" }
        failure { echo "❌ Deployment failed" }
    }
}
