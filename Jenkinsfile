pipeline {
    agent any

    environment {
        // Docker 관련 환경 변수
        IMAGE_NAME = 'jihye0/testapp' // Docker Hub에서 사용할 이미지 이름
        DOCKER_TAG = 'latest' // 사용할 태그
        AWS_EC2_INSTANCE = '52.78.114.2'  // EC2 IP 주소
        SSH_KEY_PATH = credentials('ec2-ssh-key') // EC2 SSH 키
        EC2_SSH_USER = 'ubuntu'  // EC2 SSH 사용자
        DOCKER_COMPOSE_PATH = 'docker-compose.yml'  // Docker Compose 파일 경로
        FLYWAY_URL = 'jdbc:mysql://mysql:3306/test' // MySQL DB URL
        FLYWAY_USER = 'root' // MySQL DB 사용자
        FLYWAY_PASSWORD = credentials('flyway-db-password') // MySQL DB 비밀번호
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')  // DockerHub 로그인 정보
    }

    stages {
        stage('Checkout') {
            steps {
                // Git에서 최신 코드를 체크아웃합니다.
                git branch: 'main', url: 'https://github.com/EJH506/pipeline-practice.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    // Gradle을 사용해 빌드를 실행합니다.
                    sh './gradlew clean build'  // 빌드 명령어
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    // Docker 이미지를 빌드하고 Docker Hub에 푸시합니다.
                    sh "docker build -t ${IMAGE_NAME}:${DOCKER_TAG} ."
                    // Docker Hub에 로그인하여 이미지를 푸시합니다.
                    sh "docker login -u ${DOCKERHUB_CREDENTIALS_USR} -p ${DOCKERHUB_CREDENTIALS_PSW}"
                    sh "docker push ${IMAGE_NAME}:${DOCKER_TAG}"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    // SSH를 통해 EC2 서버에 접속하여 배포 작업을 수행합니다.
                    sh """
                    # 프로젝트 파일을 EC2로 전송
                    scp -i ${SSH_KEY_PATH} -r ./demo ${EC2_SSH_USER}@${AWS_EC2_INSTANCE}:/home/ubuntu/demo

                    # EC2에 접속해 배포 수행
                    ssh -i ${SSH_KEY_PATH} ${EC2_SSH_USER}@${AWS_EC2_INSTANCE} <<EOF
                        cd /home/ubuntu/demo  # 애플리케이션 디렉토리로 이동

                        # Docker Compose로 애플리케이션과 MySQL 컨테이너를 실행
                        docker-compose pull
                        docker-compose up -d  # 컨테이너들을 백그라운드에서 실행

                        # Flyway를 사용하여 데이터베이스 마이그레이션 실행
                        docker exec -T mysql flyway -url=${FLYWAY_URL} -user=${FLYWAY_USER} -password=${FLYWAY_PASSWORD} migrate
                    EOF
                    """
                }
            }
        }
    }

    post {
        success {
            echo '배포 성공!'
        }
        failure {
            echo '배포 실패...'
        }
    }
}