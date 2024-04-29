pipeline {
    agent any
    environment {
        DB_CREDENTIALS = credentials('autonomous_database_credentials')
        OCI_CREDENTIALS = credentials('oci-user-authtoken')
        BOT_CREDENTIALS = credentials('d04f8052-8f74-4c4e-9617-c24969d413c7')
        DIRECTORY = '/home/jenkins/wallet'
        PATH = "/home/jenkins/bin:${env.PATH}"
    }
    stages{
        stage('Setting envs and copying'){
            steps{
                script{
                    sh "export DIRECTORY=${DIRECTORY}"
                    sh 'cp -R /home/jenkins/wallet/ ./wallet'
                }
            }
        }
        stage('Build and Test with Maven'){
            steps{
                // Using maven verify since it tests and builds a jar
                script{
                sh 'mvn clean verify'
                }
            }
        }
        stage('Build docker image'){
            steps{
                script{
                    sh 'docker build -t javabot-image .'
                }
            }
        }
        stage('Push image to OCI Container Registry'){
            steps{
                script{
                    sh 'echo ${OCI_CREDENTIALS_PSW} | docker login --username ${OCI_CREDENTIALS_USR} --password-stdin qro.ocir.io'
                    sh "docker tag javabot-image:latest qro.ocir.io/ax6svbbnc2oh/registry-java-bot:latest"
                    sh 'docker push qro.ocir.io/ax6svbbnc2oh/registry-java-bot:latest'
                }
            }
        }
        stage('Push to cluster'){
            steps{
                script{
                    sh 'kubectl apply -f deployment.yaml'
                    sh 'kubectl rollout restart deployment javabot-springboot-deployment'
                }
            }
        }
        stage('Cleanup'){
            steps{
                script{
                    sh 'rm /home/jenkins/.docker/config.json'
                    sh 'docker logout'
                }
                cleanWs()
            }
        }
    }
}