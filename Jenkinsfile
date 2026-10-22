pipeline {
    agent any

    environment {
        IMAGE = "jitin1975/calculator-devops"
        REGISTRY_CREDS = "dockerhub"
        NOTIFY_EMAIL = "ghost050906@gmail.com"
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE}:${BUILD_NUMBER} -t ${IMAGE}:latest ."
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDS}",
                        usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh "docker push ${IMAGE}:${BUILD_NUMBER}"
                    sh "docker push ${IMAGE}:latest"
                    sh 'docker logout'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose pull || true'
                sh 'docker-compose up -d'
                sh '''
                    echo "Waiting for the API to report healthy..."
                    for i in $(seq 1 15); do
                        if curl -fs http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
                            echo "Service is UP"
                            exit 0
                        fi
                        sleep 4
                    done
                    echo "Service did not become healthy in time"
                    docker compose logs calculator
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "SUCCESS: ${JOB_NAME} #${BUILD_NUMBER}",
                body: "Build and deploy succeeded.\n\nImage: ${IMAGE}:${BUILD_NUMBER}\nURL: ${BUILD_URL}"
            )
        }
        failure {
            emailext(
                to: "${NOTIFY_EMAIL}",
                subject: "FAILURE: ${JOB_NAME} #${BUILD_NUMBER}",
                body: "Build failed. Check the console: ${BUILD_URL}"
            )
        }
    }
}
