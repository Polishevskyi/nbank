# CI/CD Pipeline Setup

## Description.

Configured CI/CD pipeline for automatic build and deployment of a Docker image with autotests to Docker Hub.

## Functionality.

- ✅ Runs only when changes in the `src/` folder
- ✅ Building a Maven project
- ✅ Checking the code style (Checkstyle)
- ✅ Running tests
- ✅ Building a Docker image with the commit hash tag
- ✅ Push to Docker Hub

## Settings.

### 1. GitHub Secrets

Add the following secrets in the repository settings (Settings → Secrets and variables → Actions):

- `DOCKER_USERNAME` - your Docker Hub username
- `DOCKER_PASSWORD` - your Docker Hub password or access token

### 2. Docker Hub Access Token (recommended)

1. Log in to Docker Hub
2. Go to Account Settings → Security
3. Create a New Access Token
4. Use a token instead of a password in `DOCKER_PASSWORD`.

## Workflow

### Triggers

- Push to `main` or `develop` branches with changes in `rc/`
- Pull Request to `main` or `develop` with changes in `rc/`

### Jobs

#### 1. build-and-test

- Building the project: `mvn clean compile`.
- Checkstyle check: `mvn checkstyle:check`
- Running tests: `mvn test`.

#### 2. build-and-push-docker (for push only)

- Building a Docker image with the commit hash tag
- Push to Docker Hub with tags:
  - `{username}/nbank-autotests:{commit-hash}`
  - `{username}/nbank-autotests:latest`

## Using a Docker image

```bash
# Running the test API
docker run -e TEST_PROFILE=api {username}/nbank-autotests:{commit-hash}

# Running UI tests
docker run -e TEST_PROFILE=ui {username}/nbank