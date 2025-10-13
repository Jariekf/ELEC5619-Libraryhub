#!/bin/bash

# LibraryHub Full Stack Application Startup Script
echo "🚀 Starting LibraryHub Full Stack Application"
echo "=============================================="

# Check if we're in the right directory
if [ ! -d "libraryhub" ] || [ ! -d "libraryhub-frontend" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    echo "   Expected structure:"
    echo "   - libraryhub/ (Spring Boot backend)"
    echo "   - libraryhub-frontend/ (React frontend)"
    exit 1
fi

# Function to check if a port is in use
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Port $1 is already in use"
        return 1
    else
        echo "✅ Port $1 is available"
        return 0
    fi
}

# Check required ports
echo "🔍 Checking port availability..."
check_port 8080 || {
    echo "❌ Backend port 8080 is in use. Please stop the service using this port."
    exit 1
}

check_port 5173 || {
    echo "❌ Frontend port 5173 is in use. Please stop the service using this port."
    exit 1
}

# Check if Java and Maven are available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    exit 1
fi

if ! command -v mvn &> /dev/null && [ ! -f "libraryhub/mvnw" ]; then
    echo "❌ Maven is not installed and mvnw wrapper not found"
    exit 1
fi

# Check if Node.js and npm are available
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed or not in PATH"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed or not in PATH"
    exit 1
fi

echo ""
echo "📋 Starting services..."

# Start backend in background
echo "🔧 Starting Spring Boot backend..."
cd libraryhub
if [ -f "mvnw" ]; then
    ./mvnw spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
else
    mvn spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
fi
cd ..

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 10

# Check if backend is running
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "⚠️  Backend might not be ready yet, continuing with frontend..."
fi

# Start frontend in background
echo "⚛️  Starting React frontend..."
cd libraryhub-frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo ""
echo "🎉 Services started successfully!"
echo "================================"
echo "📚 Backend (Spring Boot): http://localhost:8080"
echo "⚛️  Frontend (React):     http://localhost:5173"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f backend.log"
echo "   Frontend: tail -f frontend.log"
echo ""
echo "🛑 To stop all services:"
echo "   kill $BACKEND_PID $FRONTEND_PID"
echo "   or press Ctrl+C and run: ./stop-app.sh"
echo ""
echo "🔍 Check service status:"
echo "   Backend health:  curl http://localhost:8080/actuator/health"
echo "   Frontend status: curl http://localhost:5173"
echo ""

# Wait a bit more for frontend to fully start
sleep 3

# Open browser to login page
echo "🌐 Opening browser to login page..."
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    open http://localhost:5173/login
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    xdg-open http://localhost:5173/login
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows
    start http://localhost:5173/login
fi

echo ""

# Keep script running and show logs
echo "📊 Showing live logs (Ctrl+C to stop all services)..."
echo ""

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "🛑 Stopping services..."
    kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
    echo "✅ Services stopped"
    exit 0
}

# Set trap to cleanup on script exit
trap cleanup SIGINT SIGTERM

# Show logs from both services
tail -f backend.log frontend.log

