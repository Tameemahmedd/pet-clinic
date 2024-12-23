pipeline {
    agent {
        docker {
            image 'docker:latest'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        // Set the variables for the project and Docker image
        DOCKER_IMAGE_NAME = 'pet-clinic-app'
        DOCKER_TAG = 'latest'
        EC2_USER = 'ec2-user'
        EC2_IP = '3.83.179.11'
        PRIVATE_KEY_PATH = 'pet-clinic-keypair.pem'
        MYSQL_PASSWORD = 'root123@'
        MYSQL_PORT = '3306'
        MYSQL_HOST = 'mysqldb'
        PET_CLINIC_PORT = '9091'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning the Git repository'
                git branch: 'main', credentialsId: 'git-credentials', url: 'https://github.com/Tameemahmedd/pet-clinic.git'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./mvnw'
                echo 'Building the project with Maven'
                sh './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests with Maven'
                sh './mvnw test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the project with Maven'
                sh './mvnw package -DskipTests'
            }
        }

        stage('Containerize') {
            steps {
                echo 'Building Docker images and running containers'

                script {
                    sh '''
                        docker network create bootApp || true
                        docker pull mysql:9.0.1
                        docker run -d --name mysqldb -p 3308:3306 --network=bootApp \
                            -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} \
                            -e MYSQL_DATABASE=pet_clinic \
                            mysql:9.0.1
                    '''

                    sh '''
                        docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .
                        docker run -d --name pet-clinic -p ${PET_CLINIC_PORT}:9091 --network=bootApp \
                            -e MYSQL_HOST=mysqldb \
                            -e MYSQL_PORT=${MYSQL_PORT} \
                            ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo 'Deploying the Docker containers on EC2'
                    sh """
                        scp -i ${PRIVATE_KEY_PATH} target/pet-clinic-1.0.0.jar ${EC2_USER}@${EC2_IP}:/home/${EC2_USER}/pet-clinic/
                    """
                    sh """
                        ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'docker network create bootApp || true'
                        ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'docker pull mysql:9.0.1'
                        ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'docker run -d --name mysqldb -p 3308:3306 --network=bootApp \
                            -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} -e MYSQL_DATABASE=pet_clinic mysql:9.0.1'
                        ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .'
                        ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'docker run -d --name pet-clinic -p ${PET_CLINIC_PORT}:9091 --network=bootApp \
                            -e MYSQL_HOST=mysqldb -e MYSQL_PORT=${MYSQL_PORT} ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}'
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}