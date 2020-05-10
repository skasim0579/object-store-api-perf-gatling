node {
		stage('Client Project - Checkout') {
			git branch: 'dev', credentialsId: 'skasim0579', url:  'https://github.com/AAFC-BICoE/object-store-api.git'
		}
		stage('Client Project - Copy config files') {
		    sh "mvn clean"
			sh 'cp local/docker-compose.yml.example docker-compose.yml'
			sh 'cp local/*.env .'
		}
		stage("Client Project - Run docker-compose") {
            sh 'nohup docker-compose up --build &'
			sh "while ! httping -qc1 http://localhost:8081/api/v1 ; do sleep 1 ; done"
        }
		stage('Gatling Project - Checkout') {
			git branch: 'master', credentialsId: 'skasim0579', url:  'https://github.com/skasim0579/object-store-api-perf-gatling.git'
		}
		stage('Gatling Project - Clean compile') {
		    sh "mvn clean compile"
		}
		stage("Gatling Project - Run Gatling") {
                sh 'mvn gatling:test -Dserver.host=localhost -Dserver.port=8081'
				gatlingArchive()
        }
		stage("Final Cleanup") {
			sh "cid1=`docker container ps | grep 'object-store-api' | grep '8081' | awk '{print \$1}'`; docker container stop \$cid1"
			sh "docker rm -f \$(docker ps -a -q)"
        }
}