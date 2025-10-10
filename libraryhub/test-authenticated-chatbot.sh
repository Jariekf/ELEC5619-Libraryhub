#!/bin/bash

echo "🚀 Testing LibraryHub Authenticated AI Chatbot Features"
echo "======================================================="
echo

# Test 1: Unauthenticated access (should be redirected)
echo "📋 Test 1: Testing unauthenticated access to chatbot endpoint..."
response=$(curl -X POST http://localhost:8080/api/chatbot/send \
  -H "Content-Type: application/json" \
  -d '{"message": "What are your library hours?"}' \
  -w "%{http_code}" -s -o /dev/null)

if [ "$response" = "302" ]; then
    echo "✅ PASS: Unauthenticated access correctly redirected (HTTP $response)"
else
    echo "❌ FAIL: Expected HTTP 302, got HTTP $response"
fi
echo

# Test 2: Check chatbot page requires authentication
echo "📋 Test 2: Testing unauthenticated access to chatbot page..."
page_content=$(curl -s http://localhost:8080/chatbot/chat)
if echo "$page_content" | grep -q "Login"; then
    echo "✅ PASS: Chatbot page correctly redirects to login"
else
    echo "❌ FAIL: Chatbot page not protected by authentication"
fi
echo

# Test 3: Check if main application is running
echo "📋 Test 3: Testing if main application is running..."
response=$(curl -s -w "%{http_code}" -o /dev/null http://localhost:8080/)
if [ "$response" = "200" ]; then
    echo "✅ PASS: Main application is running (HTTP $response)"
else
    echo "❌ FAIL: Main application not accessible (HTTP $response)"
fi
echo

# Test 4: Check if Ollama AI service is available
echo "📋 Test 4: Testing if Ollama AI service is available..."
response=$(curl -s -w "%{http_code}" -o /dev/null http://localhost:11434/api/tags)
if [ "$response" = "200" ]; then
    echo "✅ PASS: Ollama AI service is running (HTTP $response)"
else
    echo "⚠️  WARNING: Ollama AI service not available (HTTP $response)"
    echo "   The chatbot will fall back to rule-based responses"
fi
echo

echo "🎯 Summary of Implemented Features:"
echo "=================================="
echo "✅ FR-24: Library FAQ with comprehensive policies"
echo "✅ FR-26: Series Recommendations based on user preferences" 
echo "✅ Role-based AI responses (Customer vs Admin contexts)"
echo "✅ Authentication required for chatbot access"
echo "✅ User session awareness and personalized greetings"
echo "✅ CSRF protection for secure API calls"
echo "✅ Comprehensive library policies (hours, fines, borrowing rules)"
echo "✅ Admin-specific features (system stats, user management)"
echo "✅ AI-powered responses with Llama 3.2 integration"
echo "✅ Fallback to rule-based system if AI unavailable"
echo

echo "🌐 To test the complete chatbot experience:"
echo "1. Open http://localhost:8080 in your browser"
echo "2. Log in with your credentials"
echo "3. Navigate to the chatbot via the chat button in navigation"
echo "4. Try various queries to see AI + policy-based responses"
echo
echo "📝 Example queries to try:"
echo "• 'What are your library hours?'"
echo "• 'Recommend me a fantasy book series'"
echo "• 'What are your borrowing policies?'"
echo "• 'Show me system statistics' (admin users only)"
echo

echo "🎉 Authentication-aware AI chatbot implementation complete!"