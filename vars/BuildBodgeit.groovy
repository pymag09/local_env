package com.pymag.dsl

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        stage("Build and put into container") {
            echo "++++++++ GIT ++++++++"
            git branch: 'master', url: 'https://github.com/psiinon/bodgeit.git'
            echo "++++++++ MKDIR ++++++++"
            sh 'mkdir -p $WORKSPACE/build/WEB-INF/classes'
            echo "++++++++ ANT ++++++++"
            withAnt(installation: "${config.anttool}") {
                sh 'ant build test'
            }
            archiveArtifacts artifacts: 'bodgeit/build/bodgeit.war', fingerprint: true, onlyIfSuccessful: true
        }
    }
}
