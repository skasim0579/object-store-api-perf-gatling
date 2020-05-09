pipeline {
agent any
    stages {
        stage("Build Maven") {
            steps {
                sh 'mvn -B clean package'
            }
        }
        stage("Run Gatling") {
            steps {
                sh 'mvn gatling:test -Dserver.host=localhost -Dserver.port=8081'
            }
            post {
                always {
                    gatlingArchive()
                }
            }
        }
    }
}