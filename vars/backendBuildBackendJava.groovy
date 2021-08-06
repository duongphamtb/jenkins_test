def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    pipeline {
        agent any
        triggers {
            githubPush()
        }
        stages {
            stage('Git checkout') {
                steps {
                    git branch: "main",
                        //credentialsId: "BlueOcean Folder Credentials",
                        credentialsId: "test2",
                        url: "git@github.com:duongphamtb/jenkins_test.git"
                        //url: "https://github.com/duongphamtb/jenkins_test"
                }
            }
            stage('Build') {
                steps {
                    echo 'Duong start testing111...'
                }
            }
            stage('Test') {
                steps {
                    echo 'Testing..'
                }
            }
            stage('Deploy') {
                steps {
                    echo 'Deploying....'
                }
            }
        }
        post {
            always {
                cleanWs()
            }
        }
    }
}