pipeline {
    agent any
    environment {
        DB_CREDENTIALS = credentials('autonomous_database_credentials')
        DIRECTORY = '/home/jenkins/wallet'
    }
    stages{
        stage('Seeing envs and copying'){
            steps{
                // Any maven phase that that triggers the test phase can be used here.
                script{
                sh 'export DATABASE_USER=$DB_CREDENTIALS_USR'
                sh 'export DATABASE_PWD=$DB_CREDENTIALS_PSW'
                sh "export DIRECTORY=${DIRECTORY}"
                sh 'cp -R /home/jenkins/wallet/ ./wallet'
                sh "echo DIRECTORY=${DATABASE_USER}"
                sh "echo DIRECTORY=${DATABASE_PWD}"
                }
            }
        }
        stage('Build and Test with Maven'){
            steps{
                // Any maven phase that that triggers the test phase can be used here.
                script{
                sh 'mvn clean'
                sh 'mvn verify'
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
    }
}