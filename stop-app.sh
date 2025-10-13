#!/bin/bash

# LibraryHub Application Stop Script
echo "🛑 Stopping LibraryHub Application"
echo "=================================="

# Kill processes on ports 8080 and 5173
echo "🔍 Finding and stopping services..."

# Stop backend (port 8080)
BACKEND_PID=$(lsof -ti:8080)
if [ ! -z "$BACKEND_PID" ]; then
    echo "🛑 Stopping backend (PID: $BACKEND_PID)..."
    kill $BACKEND_PID
    sleep 2
    # Force kill if still running
    if kill -0 $BACKEND_PID 2>/dev/null; then
        echo "⚠️  Force stopping backend..."
        kill -9 $BACKEND_PID
    fi
    echo "✅ Backend stopped"
else
    echo "ℹ️  Backend not running on port 8080"
fi

# Stop frontend (port 5173)
FRONTEND_PID=$(lsof -ti:5173)
if [ ! -z "$FRONTEND_PID" ]; then
    echo "🛑 Stopping frontend (PID: $FRONTEND_PID)..."
    kill $FRONTEND_PID
    sleep 2
    # Force kill if still running
    if kill -0 $FRONTEND_PID 2>/dev/null; then
        echo "⚠️  Force stopping frontend..."
        kill -9 $FRONTEND_PID
    fi
    echo "✅ Frontend stopped"
else
    echo "ℹ️  Frontend not running on port 5173"
fi

# Clean up log files
if [ -f "backend.log" ]; then
    echo "🧹 Cleaning up backend log..."
    rm backend.log
fi

if [ -f "frontend.log" ]; then
    echo "🧹 Cleaning up frontend log..."
    rm frontend.log
fi

echo ""
echo "✅ All services stopped successfully!"
echo "   You can now restart with: ./start-app.sh"

