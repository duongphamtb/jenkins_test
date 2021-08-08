def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    pipeline {
        agent {
            node {
                label "master"
            }
        }
        options {
            disableConcurrentBuilds()
        }
        environment {
            ACCOUNT_ID = "xxx"
            AWS_CREDENTIALS_ID = "jenkins-aws-credentials"
            AWS_REGION = "eu-west-1"
            DEPLOY_IMAGE = "xxx.dkr.ecr.eu-west-1.amazonaws.com/deploy-image:latest"
            ANSIBLE_SSH_CREDENTIALS_ID = "test2"
            ANSIBLE_VAULT = "ansible-vault"
        }
        parameters {
            string(name: 'GIT_BRANCH_NAME', defaultValue: "master", description: "Branch for starting ansible playbook")
            string(name: 'ANSIBLE_TAGS', defaultValue: "", description: "May be empty. Possible to set list tags separeted by ',' . Tags list: ${config.ANSIBLE_TAGS_LIST}")
        }
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
                    echo 'Duong start s...'
                }
            }
            stage('Test') {
                steps {
                    echo 'Another Testing..'
                }
            }
            stage("Deploy") {
                steps {
                  //  ansiColor('xterm') {
                        script {
                            // if (config.EKS_CLUSTER) {
                            //     sh "aws eks --region ${env.AWS_REGION} update-kubeconfig --name ${config.EKS_CLUSTER}"
                            // }
                            ansiblePlaybook(
                                                //credentialsId: "${env.ANSIBLE_SSH_CREDENTIALS_ID}",
                                                // vaultCredentialsId: """${env.ANSIBLE_VAULT}""",
                                                inventory: 'ansible/inventories/hosts.yml',
                                                playbook: """ansible/playbooks/${config.ANSIBLE_PLAYBOOK}""",
                                                tags: """${config.ANSIBLE_TAGS_LIST}""",
                                                colorized: true
                                            )
                        }
                  //  }
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