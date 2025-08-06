#!/bin/bash

echo "🚀 Starting Kubernetes cluster and deploying NBank application"
echo "============================================================="

# STEP 1: Starting Minikube cluster
echo "📦 Starting Minikube cluster..."
minikube start --driver=docker

# Wait for cluster readiness
echo "⏳ Waiting for cluster readiness..."
kubectl wait --for=condition=Ready nodes --all --timeout=300s

# STEP 2: Deploy application via Helm
echo "🎯 Deploying NBank application via Helm..."
# Remove previous release if exists
helm uninstall nbank 2>/dev/null || true

# Install new release
helm install nbank ./nbank-chart

# Wait for pods readiness
echo "⏳ Waiting for pods readiness..."
kubectl wait --for=condition=Ready pods --all --timeout=300s

echo ""
echo "📊 SERVICES INFORMATION"
echo "======================="

# Show all services
echo "🔗 Services in cluster:"
kubectl get svc -o wide

echo ""
echo "🏠 Pods in cluster:"
kubectl get pods -o wide

echo ""
echo "📋 SERVICES AND PORTS DESCRIPTION"
echo "=================================="
echo "Service 1: Backend"
echo "  Internal port: 4111"
echo "  External port: 30411 (NodePort)"
echo "  Access: http://localhost:4111 (via port-forward)"
echo ""
echo "Service 2: Frontend" 
echo "  Internal port: 80"
echo "  External port: 30080 (NodePort)"
echo "  Access: http://localhost:3000 (via port-forward)"
echo ""
echo "Service 3: Selenoid"
echo "  Internal port: 4444" 
echo "  External port: 30444 (NodePort)"
echo "  Access: http://localhost:4444 (via port-forward)"
echo ""
echo "Service 4: Selenoid UI"
echo "  Internal port: 8080"
echo "  External port: 30808 (NodePort)" 
echo "  Access: http://localhost:8080 (via port-forward)"

echo ""
echo "📝 SERVICES LOGS"
echo "==============="
echo "Backend logs:"
kubectl logs deployment/backend --tail=10
echo ""
echo "Frontend logs:"
kubectl logs deployment/frontend --tail=10
echo ""
echo "Selenoid logs:"
kubectl logs deployment/selenoid --tail=10
echo ""
echo "Selenoid UI logs:"
kubectl logs deployment/selenoid-ui --tail=10

echo ""
echo "🔧 CONFIGMAP AND SECRETS"
echo "========================"
echo "ConfigMap for Selenoid:"
kubectl get configmap selenoid-config -o yaml

echo ""
echo "🎯 PORT FORWARDING"
echo "================="
echo "To access services via localhost, execute the following commands:"
echo ""
echo "# Frontend (http://localhost:3000):"
echo "kubectl port-forward svc/frontend 3000:80 &"
echo ""
echo "# Backend (http://localhost:4111):"  
echo "kubectl port-forward svc/backend 4111:4111 &"
echo ""
echo "# Selenoid (http://localhost:4444):"
echo "kubectl port-forward svc/selenoid 4444:4444 &"
echo ""
echo "# Selenoid UI (http://localhost:8080):"
echo "kubectl port-forward svc/selenoid-ui 8080:8080 &"

echo ""
echo "🔍 USEFUL COMMANDS FOR VERIFICATION"
echo "==================================="
echo "# Check services status:"
echo "kubectl get svc"
echo ""
echo "# Check pods status:"
echo "kubectl get pods"
echo ""
echo "# View logs of specific pod:"
echo "kubectl logs <pod-name>"
echo ""
echo "# Scaling (change replicas count):"
echo "kubectl scale deployment backend --replicas=2"
echo "kubectl scale deployment frontend --replicas=3"
echo ""
echo "# Check after scaling:"
echo "kubectl get pods -l app=backend"
echo "kubectl get pods -l app=frontend"

echo ""
echo "✅ Deployment completed! All services are ready to work."