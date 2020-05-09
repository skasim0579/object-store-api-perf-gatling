pipeline {
stages {
stage('Clone the project') {
git 'https://github.com/skasim0579/object-store-api-perf-gatling.git'
}
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