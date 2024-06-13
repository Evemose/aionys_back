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

### 2. Create the .env File

Copy the .env-template file to create the required .env file:

```sh
cp .env-template .env
```

Ensure the .env file contains the necessary environment variables. Modify values as needed for your setup.

### Important Note

When running in Docker (or in eny environment where frontend and backend apps are running on different hosts), set SPRING_ACTIVE to dev in your .env file due to specifics of cross-origin cookie sharing requiring HTTPS communication for SameSite=None cookies used for authentication.

### 3. Build and Run

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

## Troubleshooting

If you encounter issues, please ensure:

- Docker and Docker Compose are correctly installed and running.
- You have cloned the repository with submodules.

For further assistance, please refer to the Docker and Docker Compose documentation.

Feel free to further customize these templates based on the specific details and requirements of your project.
