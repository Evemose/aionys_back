# Back-end Submodule

## Overview
This submodule is the back-end part of the project. This README provides instructions on how to build and run the back-end application using Docker Compose.

## Repository Structure

```
back/
├── Dockerfile
├── docker-compose.yml
└── ...
```

## Prerequisites
- Docker Engine

## How to Build and Run

### 1. Clone the Repository
Clone the parent repository along with its submodules:

```sh
git clone --recurse-submodules <repository-url>
cd project-root/back
```

### 2. Build and Run
Navigate to the `back` directory and build the Docker image:

```sh
cd back
docker-compose -f docker-compose.yml up --build
```

## Stopping the Containers
To stop the running containers, use the following command:

```sh
docker-compose down
```

## Cleaning Up
To remove all containers, networks, and volumes created by Docker Compose, use:

```sh
docker-compose down -v
```

## Using .env
To use an `.env` file for environment variables, add the `env_file` section in the `docker-compose.yml`:

```yaml
services:
    main:
        image: "back-dev"
        build:
            context: .
            dockerfile: Dockerfile
        container_name: "back-dev"
        ports:
            - "8080:8080"
        env_file:
            - .env
```

## Troubleshooting
If you encounter issues, please ensure:
- Docker and Docker Compose are correctly installed and running.
- You have cloned the repository with submodules.

For further assistance, please refer to the Docker and Docker Compose documentation.

Feel free to further customize these templates based on the specific details and requirements of your project.
