pipeline {
    agent any
    stages {
        stage('Setup profile') {
            steps {
                sh 'cp ${CONFIG_LOCATION}/application-${ACTIVE_PROFILE}.yml ./src/main/resources'
                sh 'cp ${CONFIG_LOCATION}/logback-${ACTIVE_PROFILE}.xml ./src/main/resources'
            }
        }
        stage('Gradle build') {
            steps {
                sh 'gradle clean bootJar --stacktrace --debug --scan'
                script {
                    buildVersion = sh(script: 'gradle -q printVersion', returnStdout: true)
                }
            }
        }
        stage('Docker build') {
            steps {
                script {
                    echo "buildVersion=${buildVersion}"
                    app = docker.build("book-api:${buildVersion}", "-t book-api:latest --build-arg V_VERSION=${buildVersion} --build-arg V_PROFILE=$ACTIVE_PROFILE .")
                }
            }
        }
    }
}