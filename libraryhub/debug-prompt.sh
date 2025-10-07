#!/bin/bash

echo "🔍 Debug: What prompt is being sent to the AI?"
echo "=============================================="

# Start the application in the background if not running
if ! curl -s http://localhost:8080 > /dev/null; then
    echo "Starting LibraryHub..."
    ./mvnw spring-boot:run > app.log 2>&1 &
    APP_PID=$!
    
    # Wait for startup
    echo "Waiting for application to start..."
    for i in {1..30}; do
        if curl -s http://localhost:8080 > /dev/null; then
            break
        fi
        echo -n "."
        sleep 2
    done
    echo
fi

echo "✅ Application is running"
echo 
echo "💡 To see exactly what data the AI receives, check the application logs."
echo "   The buildRealDatabaseContext method will show you what real data is available."
echo
echo "🚀 Now test the chatbot by:"
echo "1. Going to http://localhost:8080/login"
echo "2. Login with admin/admin123"  
echo "3. Go to http://localhost:8080/chatbot/chat"
echo "4. Ask: 'Which customer has borrowed the most books?'"
echo
echo "The AI should now use ONLY real database data and not hallucinate names!"
echo
echo "If you still get fake names, it means:"
echo "• No users have actually borrowed books in your database"
echo "• The reservation table is empty"  
echo "• You need to create some test data first"
echo
echo "To create test data, you can:"
echo "1. Register some users as customers"
echo "2. Have them borrow some books"
echo "3. Then ask the AI again"