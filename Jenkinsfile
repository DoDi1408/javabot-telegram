pipeline {
    agent any
    stages{
        stage('Build and Test with Maven'){
            steps{
                try {
                    // Any maven phase that that triggers the test phase can be used here.
                    script{
                    sh 'mvn clean'
                    sh 'mvn verify'
                    }
                } catch(err) {
                    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                    throw err
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