#!/bin/bash

set -e  # Exit on any error

echo ">>> Stop Docker Compose"
docker compose down || echo "Warning: docker compose down failed, continuing..."

echo ">>> Docker pull all browser images"

# Path to the file
json_file="./config/browsers.json"

# Check that jq is set
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

# Check that browsers.json exists
if [ ! -f "$json_file" ]; then
    echo "❌ $json_file not found!"
    exit 1
fi

echo "✅ jq found: $(jq --version)"
echo "✅ browsers.json found: $json_file"

# Extract all .image values via jq
echo "Extracting images from $json_file..."
images=$(jq -r '.. | objects | select(.image) | .image' "$json_file")

if [ -z "$images" ]; then
    echo "❌ No images found in $json_file"
    exit 1
fi

echo "Found images: $images"

# Run through each image and do a docker pull
for image in $images; do
    echo "Pulling $image..."
    docker pull "$image" || echo "Warning: Failed to pull $image, continuing..."
done

echo ">>> Launching Docker Compose."
docker compose up -d

echo ">>> Services started successfully"
docker compose ps