#!/bin/bash

# Settings
IMAGE_NAME=nbank-tests
DOCKERHUB_USERNAME=polishevskyi
TAG=latest

# Load variables from the .env file, if it exists
if [ -f .env ]; then
  echo ">>> Loading environment variables from .env file"
  export $(cat .env | xargs)
fi

# Check if the token exists before using it
if [ -z "$DOCKERHUB_TOKEN" ]; then
    echo "!!! ERROR: DOCKERHUB_TOKEN is not set. Please create a .env file or export the variable."
    exit 1
fi

# Login to Docker Hub with a token
echo ">>> Login to Docker Hub with a token"
echo $DOCKERHUB_TOKEN | docker login --username $DOCKERHUB_USERNAME --password-stdin

# Tagging an image
echo ">>> Image tagging"
docker tag $IMAGE_NAME $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

# Send image to Docker Hub
echo ">>> Sending an image to Docker Hub"
docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

echo ">>> Done! The image is available as: docker pull $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"