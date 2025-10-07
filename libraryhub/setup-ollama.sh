#!/bin/bash

# LibraryHub Ollama Setup Script
echo "🦙 Setting up Ollama with Llama for LibraryHub Chatbot"
echo "======================================================"

# Check if Ollama is installed
if ! command -v ollama &> /dev/null; then
    echo "❌ Ollama is not installed. Installing via Homebrew..."
    brew install ollama
else
    echo "✅ Ollama is already installed"
fi

# Start Ollama service in background
echo "🚀 Starting Ollama service..."
ollama serve > /dev/null 2>&1 &
OLLAMA_PID=$!
echo "📝 Ollama service started (PID: $OLLAMA_PID)"

# Wait for service to be ready
echo "⏳ Waiting for Ollama service to be ready..."
sleep 3

# Check if service is running
if curl -s http://localhost:11434/api/tags > /dev/null; then
    echo "✅ Ollama service is running on http://localhost:11434"
else
    echo "❌ Failed to start Ollama service"
    exit 1
fi

# Pull Llama 3.2 model (lightweight version)
echo "📥 Pulling Llama 3.2 model (this may take a few minutes)..."
ollama pull llama3.2

# Verify model is available
echo "🔍 Checking available models..."
ollama list

# Test the model
echo "🧪 Testing the model..."
echo "User question: What are your library hours?" | ollama generate llama3.2

echo ""
echo "🎉 Setup complete! Your LibraryHub chatbot now uses:"
echo "   • 🤖 Llama 3.2 AI model for intelligent responses"
echo "   • 📚 Rule-based fallback for reliability"
echo "   • 🔄 Automatic fallback if AI is unavailable"
echo ""
echo "💡 To use the chatbot:"
echo "   1. Start your Spring Boot application: ./mvnw spring-boot:run"
echo "   2. Visit the chat interface at: http://localhost:8080/chatbot/chat"
echo "   3. Ask questions like:"
echo "      • 'What are your hours?'"
echo "      • 'Find books by Tolkien'"
echo "      • 'Recommend a fantasy series'"
echo ""
echo "⚙️  Configuration:"
echo "   • Ollama URL: http://localhost:11434"
echo "   • Model: llama3.2"
echo "   • Fallback: Enabled"
echo ""
echo "🛑 To stop Ollama service later: kill $OLLAMA_PID"