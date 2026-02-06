#!/bin/bash

set -e
set -o pipefail

DEST_DIR=${1:-"./dist"}
TEMP_TAG="temp-qdar-build-$(date +%s)"

echo "Starting build with tag: $TEMP_TAG"

# 2. Build the image
docker build -t "$TEMP_TAG" .

# 3. Create a temporary container to copy files out
echo "Extracting artifacts to $DEST_DIR"
mkdir -p "$DEST_DIR"
CONTAINER_ID=$(docker create "$TEMP_TAG")
docker cp "$CONTAINER_ID":/output/. "$DEST_DIR"
docker rm "$CONTAINER_ID"

# 4. Cleanup to prevent registry pollution
echo "Cleaning up..."
docker rmi "$TEMP_TAG"

echo "Files are in $DEST_DIR"