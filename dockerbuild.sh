#!/bin/bash

echo "Running mvn clean package..."
if ./mvnw clean package -DskipTests; then
    echo "Maven build succeeded!"
    
    echo "Building Docker image..."
    if docker build . -t jonathonclifford/mm-models-service; then
        echo "Docker image built successfully!"
    else
        echo "Failed to build Docker image." >&2
        exit 1
    fi
else
    echo "Maven build failed." >&2
    exit 1
fi
