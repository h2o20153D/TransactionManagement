#!/bin/bash

show_help() {
    echo "Usage: $0 [local|docker|k8s]"
    echo ""
    echo "Deployment options:"
    echo "  local  - Build and run locally"
    echo "  docker - Build and run with Docker"
    echo "  k8s    - Deploy to Kubernetes"
    echo ""
}

if [ $# -ne 1 ]; then
    echo "Error: Please provide a deployment environment (local, docker, or k8s)"
    show_help
    exit 1
fi

cd "$(dirname "$0")/.."
PROJECT_ROOT=$(pwd)
echo "Project directory: $PROJECT_ROOT"

case "$1" in
    local)
        echo "Deploying locally..."
        echo "Building application..."
        
        if [ $? -eq 0 ]; then
            echo "Starting application..."
            mvn spring-boot:run
        else
            echo "Build failed, cannot start application"
            exit 1
        fi
        ;;
        
    docker)
        echo "Deploying with Docker..."
        echo "Building Docker image..."
        docker build -t transaction-service:latest .
        
        if [ $? -eq 0 ]; then
            echo "Running Docker container..."
            docker run -p 8080:8080 transaction-service:latest
        else
            echo "Docker image build failed"
            exit 1
        fi
        ;;
        
    k8s)
        echo "Deploying to Kubernetes..."
        
        echo "Applying ConfigMap..."
        kubectl apply -f k8s/configmap.yaml
        
        echo "Applying Deployment..."
        kubectl apply -f k8s/deployment.yaml
        
        echo "Applying Service..."
        kubectl apply -f k8s/service.yaml
        
        if [ $? -eq 0 ]; then
            echo "Checking deployment status..."
            kubectl get pods -l app=transaction-service
            kubectl get svc transaction-service
        else
            echo "Kubernetes deployment failed"
            exit 1
        fi
        ;;
        
    *)
        echo "Error: Invalid deployment environment: $1"
        show_help
        exit 1
        ;;
esac

echo "Deployment completed!"
exit 0
