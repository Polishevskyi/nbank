#!/bin/bash

echo ">>> Stop Docker Compose"
docker compose down

echo ">>> Docker pull all browser images"

# Path to the file
json_file="./config/browsers.json"

# Check that jq is set
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

# Extract all .image values via jq
images=$(jq -r '.. | objects | select(.image) | .image' "$json_file")

# Run through each image and do a docker pull
for image in $images; do
    echo "Pulling $image..."
    docker pull "$image"
done

echo ">>> Launching Docker Compose."
docker compose up -d