#!/bin/bash

echo "=========================================="
echo "Stopping LibraryHub Application"
echo "=========================================="

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Stop Backend
if [ -f "backend.pid" ]; then
    BACKEND_PID=$(cat backend.pid)
    if ps -p $BACKEND_PID > /dev/null 2>&1; then
        echo "Stopping Backend (PID: $BACKEND_PID)..."
        kill $BACKEND_PID

        # Wait for process to stop
        for i in {1..10}; do
            if ! ps -p $BACKEND_PID > /dev/null 2>&1; then
                echo "✓ Backend stopped"
                break
            fi
            sleep 1
        done

        # Force kill if still running
        if ps -p $BACKEND_PID > /dev/null 2>&1; then
            echo "Force stopping backend..."
            kill -9 $BACKEND_PID
            echo "✓ Backend force stopped"
        fi
    else
        echo "Backend is not running"
    fi
    rm backend.pid
else
    echo "Backend PID file not found"

    # Try to find and kill backend process
    BACKEND_PID=$(lsof -ti:8080)
    if [ ! -z "$BACKEND_PID" ]; then
        echo "Found process on port 8080 (PID: $BACKEND_PID)"
        echo "Stopping..."
        kill $BACKEND_PID
        sleep 2
        if ps -p $BACKEND_PID > /dev/null 2>&1; then
            kill -9 $BACKEND_PID
        fi
        echo "✓ Backend stopped"
    fi
fi

# Stop Frontend
if [ -f "frontend.pid" ]; then
    FRONTEND_PID=$(cat frontend.pid)
    if ps -p $FRONTEND_PID > /dev/null 2>&1; then
        echo "Stopping Frontend (PID: $FRONTEND_PID)..."
        kill $FRONTEND_PID

        # Wait for process to stop
        for i in {1..10}; do
            if ! ps -p $FRONTEND_PID > /dev/null 2>&1; then
                echo "✓ Frontend stopped"
                break
            fi
            sleep 1
        done

        # Force kill if still running
        if ps -p $FRONTEND_PID > /dev/null 2>&1; then
            echo "Force stopping frontend..."
            kill -9 $FRONTEND_PID
            echo "✓ Frontend force stopped"
        fi
    else
        echo "Frontend is not running"
    fi
    rm frontend.pid
else
    echo "Frontend PID file not found"

    # Try to find and kill frontend process
    FRONTEND_PID=$(lsof -ti:5173)
    if [ ! -z "$FRONTEND_PID" ]; then
        echo "Found process on port 5173 (PID: $FRONTEND_PID)"
        echo "Stopping..."
        kill $FRONTEND_PID
        sleep 2
        if ps -p $FRONTEND_PID > /dev/null 2>&1; then
            kill -9 $FRONTEND_PID
        fi
        echo "✓ Frontend stopped"
    fi
fi

# Clean up backend app.pid if exists
if [ -f "libraryhub/app.pid" ]; then
    rm libraryhub/app.pid
fi

echo ""
echo "=========================================="
echo "Application Stopped Successfully!"
echo "=========================================="