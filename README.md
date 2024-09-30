# Customer Feedback Service

A feedback management service built with Spring Boot, MongoDB, and JWT for authentication. 
The system allows users to create, update, delete, and search for feedback entries on products.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Environment Setup](#environment-setup)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [Makefile Targets](#makefile-targets)
- [Deploy app on AWS](#deploy-app-on-aws)
- [Deploy Jenkins on AWS](#deploy-jenkins-on-aws)
- [License](#license)

## Prerequisites

Ensure you have the following installed:

- Java 11 or higher
- Gradle
- Docker
- Docker Compose

## Environment Setup

The project uses environment variables for configuration. You can define these in a `.env` file at the root of the project.

Example `.env` file:

```bash
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=securepassword
MONGO_USER=mongo
MONGO_PASSWORD=mongo
```

To check the current environment variables, you can run the following command:

```sh
make env
```

## Building the Project

To build the project, use the `build` target:

```sh
make build
```

This command will compile the code and package the application using Gradle.

## Running the Application

### Running Locally

To run the application locally:

```sh
make run
```

The application will be accessible at `http://localhost:8080`.

### Running with Docker

You can also run the application and the database using Docker:

```sh
make start-docker
```

To stop the application and remove the containers:

```sh
make stop-docker
```

## Running Tests

### Running Locally

To run tests locally, use the following command:

```sh
make test
```

### Running Tests in Docker

To run tests inside a Docker container, use:

```sh
make run-docker-tests
```

This command will spin up the necessary containers and run the test suite inside them.

## Makefile Targets

- **`make build`**: Compiles and packages the application.
- **`make test`**: Runs the test suite.
- **`make run`**: Runs the application locally.
- **`make clean`**: Cleans up the build artifacts.
- **`make rebuild`**: Cleans and rebuilds the project.
- **`make env`**: Prints the environment variables.
- **`make docker-build`**: Builds Docker images for the application.
- **`make start-db`**: Starts the database container.
- **`make start-docker`**: Starts the application and database containers.
- **`make run-docker-tests`**: Runs tests inside a Docker container.
- **`make stop-docker`**: Stops and removes Docker containers.

## Deploy App on AWS

1. Create an EC2 instance like explained [here](https://www.jenkins.io/doc/tutorials/tutorial-for-installing-jenkins-on-AWS/)
2. Install git and clone this repository
     ```sh
    sudo yum update -y
    sudo yum install git -y
    git clone https://github.com/joao-prg/customer_feedback_service.git
     ```
3. Install docker, docker-compose, add ec2-user to the docker group and start docker
   ```sh
   sudo amazon-linux-extras install docker 
   sudo yum install docker -y
   sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
   sudo chmod +x /usr/local/bin/docker-compose
   sudo usermod -a -G docker ec2-user
   newgrp docker
   sudo service docker start
    ```
4. Set the following environment variables:
    - ADMIN_EMAIL
    - ADMIN_PASSWORD
    - MONGO_USER
    - MONGO_PASSWORD
5. Launch app with docker
    ```sh
     docker-compose -f docker-compose-prod.yml up --build -d
     ```

## Deploy Jenkins on AWS

1. See [Jenkins on AWS](https://www.jenkins.io/doc/tutorials/tutorial-for-installing-jenkins-on-AWS/)
    1. Install git and clone this repository
        ```sh
       sudo yum update -y
       sudo yum install git -y
       git clone https://github.com/joao-prg/customer_feedback_service.git
        ```
    2. Install docker, docker-compose, add ec2-user to the docker group and start docker
       ```sh
       sudo amazon-linux-extras install docker 
       sudo yum install docker -y
       sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
       sudo chmod +x /usr/local/bin/docker-compose
       sudo usermod -a -G docker ec2-user
       newgrp docker
       sudo service docker start
        ```
    3. Instead of installing Jenkins locally, launch it with docker
       ```sh
        docker-compose -f docker-compose-jenkins.yml up docker jenkins --build -d
        ```
2. Once Jenkins is up and running:
    1. Make sure the following plugins are installed (Manage Jenkins -> Plugins):
        1. GitHub API
        2. GitHub Branch Source
        3. GitHub Integration
        4. GitHub
        5. Pipeline GitHub Notify Step
        6. Pipeline GitHub Groovy Libraries
        7. Docker Pipeline
    2. Add github credentials (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->Username and password, with the github username and a Personal Access Token as password
        2. Make sure the token has permissions for repo, admin:repo_hook, read:org, and workflow
        3. Set the credentials ID to CUSTOMER_FEEDBACK_SERVICE_CREDENTIALS
    3. Add docker hub credentials (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->Username and password, with the docker hub username and password
        2. Set the credentials ID to DOCKER_HUB_CREDENTIALS
    4. Add ssh private key to remote server (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->SSH Username with private key
        2. Set the credentials ID to REMOTE_SERVER_SSH_KEY
    5. Add admin credentials (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->Username and password, with the admin username and password
        2. Set the credentials ID to ADMIN_CREDENTIALS_ID
    6. Add database credentials (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->Username and password, with the mongo username and password
        2. Set the credentials ID to DB_CREDENTIALS_ID
    7. Add remote server user (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->Secret text
        2. Set the credentials ID to REMOTE_SERVER_USER
    8. Add remote server IP (Manage Jenkins -> Credentials -> System -> Global credentials unrestricted -> Add credentials)
        1. Kind->Secret text
        2. Set the credentials ID to REMOTE_SERVER_IP
    9. Create a new multibranch pipeline (Create a job -> Multibranch Pipeline)
        1. Point to this repository (Branch Sources -> Github)
        2. Set credentials to the github credentials
3. Create github webhook
    1. Go to the repo page -> Settings -> Webhooks -> Add webhook
    2. Set Payload URL to http://<jenkins-url>:<jenkins_port>/github-webhook/
    3. Set Content type to "application/json"
    4. Check "Just the push event" or edit which events will trigger a build
    5. Edit the Security Group Rule so that port 8080 is available to the Github Webhook (set source to 0.0.0.0/0 for public access)

## License

This project is released into the public domain under [The Unlicense](LICENSE). This means you can freely use, modify, and distribute the code with no restrictions. For more details, see the [LICENSE](LICENSE) file.