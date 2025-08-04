#!/bin/bash

docker compose down
images=$(jq -r '.. | objects | select(.image) | .image' "./config/browsers.json")
for image in $images; do
    docker pull "$image" || true
done
docker compose up -d