#!/bin/bash

echo "=========================================="
echo "Starting LibraryHub Application"
echo "=========================================="

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Check if MySQL is running
if ! pgrep -x "mysqld" > /dev/null; then
    echo "Error: MySQL is not running!"
    echo "Please start MySQL first:"
    echo "  Mac: brew services start mysql"
    echo "  Linux: sudo systemctl start mysql"
    exit 1
fi

echo "✓ MySQL is running"

# Start Backend
echo ""
echo "Starting Backend Server..."
cd libraryhub

# Check if already running
if [ -f "app.pid" ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        echo "Backend is already running (PID: $PID)"
    else
        rm app.pid
    fi
fi

# Start backend in background
nohup ./mvnw spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > app.pid

echo "✓ Backend started (PID: $BACKEND_PID)"
echo "  Log: backend.log"
echo "  URL: http://localhost:8080"

# Wait for backend to start
echo ""
echo "Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8080 > /dev/null 2>&1; then
        echo "✓ Backend is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "Warning: Backend took too long to start"
        echo "Check backend.log for errors"
    fi
    sleep 2
    echo -n "."
done

# Start Frontend
echo ""
echo "Starting Frontend Server..."
cd ../libraryhub-frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi

# Start frontend in background
nohup npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!

echo "✓ Frontend started (PID: $FRONTEND_PID)"
echo "  Log: frontend.log"
echo "  URL: http://localhost:5173"

# Save PIDs to file
cd ..
echo "$BACKEND_PID" > backend.pid
echo "$FRONTEND_PID" > frontend.pid

echo ""
echo "=========================================="
echo "Application Started Successfully!"
echo "=========================================="
echo "Backend:  http://localhost:8080"
echo "Frontend: http://localhost:5173"
echo ""
echo "To stop the application, run: ./stop.sh"
echo "To view logs:"
echo "  Backend:  tail -f backend.log"
echo "  Frontend: tail -f frontend.log"
echo "=========================================="

# Open application in browser
echo ""
echo "Opening application in browser..."
sleep 2

# Detect OS and open browser
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    open http://localhost:5173
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    if command -v xdg-open > /dev/null; then
        xdg-open http://localhost:5173
    elif command -v gnome-open > /dev/null; then
        gnome-open http://localhost:5173
    fi
elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
    # Windows
    start http://localhost:5173
fi

echo "✓ Browser opened"