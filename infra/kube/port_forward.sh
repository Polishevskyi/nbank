#!/bin/bash

echo "🌐 Setting up port forwarding for NBank services"
echo "================================================="

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Port $port is already in use"
        return 1
    else
        return 0
    fi
}

# Function to stop existing port-forward processes
cleanup_port_forwards() {
    echo "🧹 Stopping existing port-forward processes..."
    pkill -f "kubectl port-forward" 2>/dev/null || true
    sleep 2
}

# Stop previous processes
cleanup_port_forwards

echo ""
echo "🚀 Starting port forwarding..."

# Check port availability
ports_to_check=(3000 4111 4444 8080)
for port in "${ports_to_check[@]}"; do
    if ! check_port $port; then
        echo "Stopping process on port $port..."
        lsof -ti:$port | xargs kill -9 2>/dev/null || true
        sleep 1
    fi
done

echo ""
echo "🔗 Setting up port forwarding for all services:"

# Frontend
echo "📱 Frontend: http://localhost:3000"
kubectl port-forward svc/frontend 3000:80 > /dev/null 2>&1 &
FRONTEND_PID=$!

sleep 2

# Backend  
echo "⚙️  Backend: http://localhost:4111"
kubectl port-forward svc/backend 4111:4111 > /dev/null 2>&1 &
BACKEND_PID=$!

sleep 2

# Selenoid
echo "🤖 Selenoid: http://localhost:4444"
kubectl port-forward svc/selenoid 4444:4444 > /dev/null 2>&1 &
SELENOID_PID=$!

sleep 2

# Selenoid UI
echo "🖥️  Selenoid UI: http://localhost:8080"
kubectl port-forward svc/selenoid-ui 8080:8080 > /dev/null 2>&1 &
SELENOID_UI_PID=$!

sleep 3

echo ""
echo "✅ Port forwarding configured!"
echo ""
echo "🌐 Available services:"
echo "┌─────────────────┬──────────────────────────┬────────────┐"
echo "│ Service         │ URL                      │ PID        │"
echo "├─────────────────┼──────────────────────────┼────────────┤"
echo "│ Frontend        │ http://localhost:3000    │ $FRONTEND_PID        │"
echo "│ Backend         │ http://localhost:4111    │ $BACKEND_PID        │"
echo "│ Selenoid        │ http://localhost:4444    │ $SELENOID_PID        │"
echo "│ Selenoid UI     │ http://localhost:8080    │ $SELENOID_UI_PID        │"
echo "└─────────────────┴──────────────────────────┴────────────┘"

echo ""
echo "🔍 Checking services availability:"

# Function to check HTTP response
check_service() {
    local url=$1
    local name=$2
    local timeout=5
    
    if curl -s --max-time $timeout "$url" > /dev/null 2>&1; then
        echo "✅ $name - available"
    else
        echo "❌ $name - unavailable or still loading"
    fi
}

sleep 5

check_service "http://localhost:3000" "Frontend"
check_service "http://localhost:4111/actuator/health" "Backend Health"
check_service "http://localhost:4444/status" "Selenoid Status" 
check_service "http://localhost:8080" "Selenoid UI"

echo ""
echo "📋 Useful commands:"
echo "# Check active port-forward processes:"
echo "ps aux | grep 'kubectl port-forward'"
echo ""
echo "# Stop all port-forward:"
echo "pkill -f 'kubectl port-forward'"
echo ""
echo "# Check port availability:"
echo "lsof -i :3000,4111,4444,8080"

echo ""
echo "🎯 To stop all port-forward processes press Ctrl+C"
echo "   or execute: pkill -f 'kubectl port-forward'"

# Wait for signal to stop
trap 'echo ""; echo "🛑 Stopping port forwarding..."; cleanup_port_forwards; echo "✅ All port-forward processes stopped"; exit 0' INT

echo ""
echo "⏳ Port-forward processes are running... (Ctrl+C to stop)"

# Infinite loop to keep process running
while true; do
    sleep 10
    # Check if all processes are still alive
    if ! kill -0 $FRONTEND_PID $BACKEND_PID $SELENOID_PID $SELENOID_UI_PID 2>/dev/null; then
        echo "⚠️  Some port-forward processes terminated. Restarting..."
        exec "$0"
    fi
done