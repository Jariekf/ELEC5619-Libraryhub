#!/bin/bash

echo "🧪 Testing LibraryHub Chatbot API..."

# Wait for application to start
sleep 5

echo "1. Testing chatbot health endpoint..."
curl -s http://localhost:8080/api/chatbot/health | jq .

echo -e "\n2. Testing chatbot with FAQ query..."
curl -X POST http://localhost:8080/api/chatbot/send \
  -H "Content-Type: application/json" \
  -d '{"message": "What are your library hours?"}' \
  -s | jq .

echo -e "\n3. Testing chatbot with book search..."
curl -X POST http://localhost:8080/api/chatbot/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Find books by Tolkien"}' \
  -s | jq .

echo -e "\n4. Testing chatbot with greeting..."
curl -X POST http://localhost:8080/api/chatbot/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}' \
  -s | jq .

echo -e "\nTest complete!"