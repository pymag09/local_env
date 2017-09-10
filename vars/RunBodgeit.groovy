package pymag.dsl

import pymag.dsl.Docker

@NonCPS
def call(steps) {
    def dockeris = new Docker().IsDockerInstalled
    println "BUILDING...."
    node(){
        stage("Build and put into container") {
            if (dockeris) {
                steps.echo "BUILDING..."
                steps.git url: "https://github.com/psiinon/bodgeit.git"
                steps.sh 'mkdir -p $WORKSPACE/build/WEB-INF/classes'
                steps.withAnt(installation: 'ant-latest') {
                    sh:
                    ant build test
                }
            } else {
                println "Docker is not installed"
            }
        }
        stage("Run container") {
            if (dockeris)
                steps.sh 'docker run -d -v /var/lib/jenkins/workspace/bodgeit/build/bodgeit.war:/usr/local/tomcat/webapps/bodgeit.war --name bodgeit -p 8181:8080 tomcat'
        }
    }
}