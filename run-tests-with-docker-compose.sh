#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Change the working directory to the project root (where this script is located).
cd "$(dirname "$0")"

# The directory containing the docker-compose.yml file.
COMPOSE_DIR="infra/docker_compose"

# Check if the compose directory exists.
if [ ! -d "$COMPOSE_DIR" ]; then
    echo "Error: Directory '$COMPOSE_DIR' not found."
    echo "Please check the COMPOSE_DIR variable in the script."
    exit 1
fi

# Change directory to where the docker-compose.yml is located.
# This makes all relative paths inside the compose file work correctly.
cd "$COMPOSE_DIR"

# Cleanup function. This will be called on script exit.
cleanup() {
  echo ""
  echo "Stopping the test environment..."
  # No special flags needed as we are in the correct directory.
  docker compose down
  echo "Environment stopped."
}

# Set a trap to call the cleanup function on script exit.
trap cleanup EXIT

echo "Starting the test environment (backend, frontend, nginx, selenoid...)"
# Start all services. Docker will find docker-compose.yml automatically.
docker compose up -d

echo ""
echo "Environment is up. Running tests..."

# Run the test container. It needs to be on the same network.
# The network is correctly named 'nbank-network' in your compose file.
docker run --rm \
  --network=nbank-network \
  -e APIBASEURL=http://localhost:4111 \
  -e UIBASEURL=http://localhost:80 \
  -e SELENOID_URL=http://localhost:4444 \
  -e SELENOID_UI_URL=http://localhost:6567 \
  polishevskyi/nbank-tests:latest

# The trap will call the cleanup function automatically.
echo ""
echo "Tests completed successfully."