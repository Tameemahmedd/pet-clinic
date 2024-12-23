pipeline {
    agent any

    environment {
        // Set the variables for the project and Docker image
        DOCKER_IMAGE_NAME = 'pet-clinic-app'  // Name for the Docker image
        DOCKER_TAG = 'latest'  // Tag for the Docker image
        EC2_USER = 'ec2-user'  // SSH username for EC2 instance (Amazon Linux)
        EC2_IP = '13.203.158.113'  // EC2 instance IP address
        PRIVATE_KEY_PATH = 'pet-clinic-keypair.pem'  // Path to your private SSH key
        MYSQL_PASSWORD = 'root123@'  // MySQL root password
        MYSQL_PORT = '3306'  // MySQL port inside the container
        MYSQL_HOST = 'mysqldb'  // Host name of the MySQL container
        PET_CLINIC_PORT = '9091'  // Port for pet-clinic application
    }


    stages {
        stage('Clone Repository') {
            steps {
                // Clone the repository from GitHub
                echo 'Cloning the Git repository'
                git branch: 'main', credentialsId: 'git-credentials', url: 'https://github.com/Tameemahmedd/pet-clinic.git'  // Replace with actual repo URL
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./mvnw'

                // Run Maven to clean and compile the project
                echo 'Building the project with Maven'
                sh './mvnw clean compile'  // Using Maven Wrapper to compile
            }
        }

        stage('Test') {
            steps {
                // Run Maven tests
                echo 'Running tests with Maven'
                sh './mvnw test'  // Run unit tests with Maven
            }
        }

        stage('Package') {
            steps {
                // Package the Spring Boot application into a JAR file
                echo 'Packaging the project with Maven'
                sh './mvnw package -DskipTests'  // Create JAR without running tests
            }
        }

stage('Containerize') {
    steps {
        echo 'Building Docker images and running containers'

        script {
            // Build the MySQL container
            sh '''
                docker network create bootApp || true  # Create a Docker network (ignore if it already exists)
                docker pull mysql:9.0.1  # Pull the MySQL image
                docker run -d --name mysqldb -p 3308:3306 --network=bootApp \
                    -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} \
                    -e MYSQL_DATABASE=pet_clinic \
                    mysql:9.0.1  # Run MySQL container
            '''

            // Build the Pet Clinic application container
            sh '''
                docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .  # Build the Docker image for the pet-clinic app
                docker run -d --name pet-clinic -p ${PET_CLINIC_PORT}:9091 --network=bootApp \
                    -e MYSQL_HOST=mysqldb \
                    -e MYSQL_PORT=${MYSQL_PORT} \
                    ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}  # Run Pet Clinic application container
            '''
        }
    }
}

        stage('Deploy') {
            steps {
                script {
                                   // Change the permissions of the private key file
                                   sh 'chmod 600 ${PRIVATE_KEY_PATH}'

                                   // Optional: Deploy the containers on EC2 instance using SSH
                                   echo 'Deploying the Docker containers on EC2'

                                   // Copy the JAR file to EC2 instance (if necessary for your app)
                                   // sh """
                                   //     scp -i ${PRIVATE_KEY_PATH} target/pet-clinic-1.0.0.jar ${EC2_USER}@${EC2_IP}:/home/${EC2_USER}/pet-clinic/
                                   // """

                                   // SSH into the EC2 instance to run the MySQL and Pet Clinic Docker containers (if not using local Docker)
                                   sh """
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo yum install docker -y'
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo systemctl start docker'
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo docker network create bootApp || true'  // Create network if not exists
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo docker pull mysql:latest'  // Pull the MySQL image
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo docker run -d --name mysqldb -p 3308:3306 --network=bootApp \
                                           -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} -e MYSQL_DATABASE=pet_clinic mysql'  // Run MySQL container on EC2
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo docker pull tameemahmed/pet-clinic-1.0.1:latest'
                                       ssh -i ${PRIVATE_KEY_PATH} ${EC2_USER}@${EC2_IP} 'sudo docker run -d --name pet-clinic -p ${PET_CLINIC_PORT}:9091 --network=bootApp \
                                           -e MYSQL_HOST=mysqldb -e MYSQL_PORT=${MYSQL_PORT} ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}'  // Run Pet Clinic container on EC2
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