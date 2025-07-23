#!/bin/bash

# Setup
IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api} # startup argument
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR=./test-output/$TIMESTAMP

# Build a Docker image
echo ">>> The test build is running"
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

# Starting a Docker container
echo ">>> Tests are running"
docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://192.168.0.106 \
  -e UIBASEURL=http://192.168.0.106 \
$IMAGE_NAME

# Totals output
echo ">>> Tests completed"
echo "Log file: $TEST_OUTPUT_DIR/logs/run.log"
echo "Test results: $TEST_OUTPUT_DIR/results"
echo "Report: $TEST_OUTPUT_DIR/report"