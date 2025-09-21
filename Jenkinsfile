pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS = credentials('dockerHubCredentials')
        MY_KUBECONFIG = credentials('kubeConfigFile')
    }
    stages {
        stage('Secret check with Trufflehog') {
            steps {
                echo '------ Printing remotes ------'
                sh 'git remote -v'
                echo '------ Fetching main branch ------'
                sh 'git fetch origin main'
                echo '------ Trufflehog Version ------'
                sh 'trufflehog --version'
                echo '------ Running Trufflehog secret scan ------'
                sh 'trufflehog git file://. --since-commit main --branch "$BRANCH_NAME" --fail --no-update'
            }
        }
//         stage('Build medical-models-service') {
//             steps {
//                 echo '------ Java Version ------'
//                 sh 'java -version'
//                 echo '------ Building the application into a docker image ------'
//                 sh './dockerbuild.sh'
//                 echo '------ Pushing the docker image to artifact store ------'
//                 sh 'echo "$DOCKER_CREDENTIALS_PSW" | docker login -u "$DOCKER_CREDENTIALS_USR" --password-stdin'
//                 sh './dockerpush.sh'
//             }
//         }
//         stage('Checkout medical-models-k8s') {
//             steps {
//                 echo '------ Checking out additional repository: medical-models-k8s ------'
//                 checkout scmGit(
//                     branches: [[name: '*/main']],
//                     extensions: [],
//                     userRemoteConfigs: [[
//                         credentialsId: 'gitea-jenkins-user-and-pass',
//                         url: 'http://gitea.busybunyip.com/medical-models/medical-models-k8s.git'
//                     ]]
//                 )
//             }
//         }
//         stage('Deploy to Prod') {
//             steps {
//                 echo '------ kubectl version ------'
//                 sh 'kubectl version --client'
//                 echo '------ Delete current mm-models-service pods ------'
//                 sh 'kubectl --kubeconfig $MY_KUBECONFIG delete deployment deployment-mm-models-service --ignore-not-found'
//                 echo '------ Deploying new version of mm-models-service ------'
//                 sh 'kubectl --kubeconfig $MY_KUBECONFIG apply -f mm-models.yaml'
//             }
//         }
    }
}

