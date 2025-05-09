pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
        jdk 'JDK 23'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t driver-fleet-app:${BUILD_NUMBER} .'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose stop app || true'
                sh 'docker-compose rm -f app || true'
                sh 'docker-compose up -d app'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}