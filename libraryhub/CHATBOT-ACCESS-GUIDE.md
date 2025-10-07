# 🤖 LibraryHub AI Chatbot - Quick Access Guide

## 🎯 Where to Find the AI Chatbot

After logging in, you can access the AI chatbot from multiple locations:

### 1. **Main Books Page** 
- URL: http://localhost:8080/books
- Look for the green **"🤖 AI Assistant"** button in the top navigation bar
- Available to all authenticated users (both customers and admins)

### 2. **Customer Dashboard**
- URL: http://localhost:8080/customer/dashboard  
- Click **"Chat with AI Assistant"** button
- Available to users with CUSTOMER role

### 3. **Admin Panel**
- URL: http://localhost:8080/admin/dashboard
- Green **"🤖 AI Assistant"** button in the admin navigation
- Available to users with ADMIN role

### 4. **Direct Access**
- URL: http://localhost:8080/api/chatbot/chat
- Direct link to the chat interface
- Works for all authenticated users

## 🚀 Getting Started

1. **Start the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Setup AI Model (Optional but Recommended):**
   ```bash
   ./setup-ollama.sh
   ```

3. **Login:**
   - Go to: http://localhost:8080
   - Default admin: `admin` / `admin123`
   - Or create a customer account

4. **Access Chatbot:**
   - Click the **"🤖 AI Assistant"** button anywhere in the app
   - Or visit http://localhost:8080/api/chatbot/chat directly

## 💡 What You Can Ask

### Library FAQ Examples:
- *"What are your library hours?"*
- *"How do I reserve a book?"* 
- *"What's the fine policy?"*
- *"How do I create an account?"*

### Book Search Examples:
- *"Find books by J.K. Rowling"*
- *"Show me fantasy books"*
- *"Are there any available mystery novels?"*
- *"Search for books with 'dragon' in the title"*

### Series Recommendations:
- *"Recommend a fantasy series"*
- *"What book series should I read next?"*
- *"Suggest something similar to Harry Potter"*

### General Questions:
- *"Hello!"*
- *"How does this library work?"*
- *"Thank you"*

## 🔧 Features

- **🦙 AI-Powered**: Uses Llama 3.2 for intelligent responses
- **🛡️ Reliable Fallback**: Works even when AI is offline
- **👤 User Context**: Knows who you are and your preferences  
- **💬 Natural Language**: Understands casual conversation
- **⚡ Fast**: Quick responses with typing indicators
- **📱 Responsive**: Works on mobile and desktop

## 🎨 UI Features

- Modern chat interface with message bubbles
- Typing indicators while AI thinks
- Example query buttons to get started
- User authentication display
- Navigation integrated with main app
- Bootstrap styling matching the library theme

## 🔍 Testing Without AI

If you haven't set up Ollama yet, the chatbot will still work using rule-based responses. You'll get helpful answers to FAQ questions and book searches, just without the advanced AI conversation capabilities.

---

**Happy chatting with your AI-powered LibraryHub assistant! 🚀📚**