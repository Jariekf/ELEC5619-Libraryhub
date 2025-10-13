import React, { useState, useEffect, useRef } from 'react';
import apiService from '../../services/api';

interface ChatMessage {
  id: string;
  message: string;
  response: string;
  user: string;
  timestamp: string;
}

const Chatbot: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isConnected, setIsConnected] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const exampleQueries = [
    "What are your library hours?",
    "Find books by J.K. Rowling",
    "Recommend a fantasy series",
    "How do I reserve a book?",
    "What's the fine policy?"
  ];

  useEffect(() => {
    checkConnection();
    // Add welcome message
    setMessages([{
      id: '1',
      message: '',
      response: "Hello! I'm your LibraryHub AI assistant. I can help you with book searches, library policies, recommendations, and more. How can I assist you today?",
      user: 'assistant',
      timestamp: new Date().toISOString()
    }]);
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const checkConnection = async () => {
    try {
      await apiService.getChatbotHealth();
      setIsConnected(true);
    } catch (error) {
      setIsConnected(false);
      console.error('Chatbot connection failed:', error);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const sendMessage = async (message: string) => {
    if (!message.trim() || isLoading) return;

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      message: message.trim(),
      response: '',
      user: 'user',
      timestamp: new Date().toISOString()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);

    try {
      const response = await apiService.sendChatMessage(message.trim());
      
      const assistantMessage: ChatMessage = {
        id: (Date.now() + 1).toString(),
        message: '',
        response: response.response,
        user: 'assistant',
        timestamp: new Date().toISOString()
      };

      setMessages(prev => [...prev, assistantMessage]);
    } catch (error) {
      const errorMessage: ChatMessage = {
        id: (Date.now() + 1).toString(),
        message: '',
        response: "I'm sorry, I'm having trouble connecting right now. Please try again later or contact support if the issue persists.",
        user: 'assistant',
        timestamp: new Date().toISOString()
      };

      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    sendMessage(inputMessage);
  };

  const handleExampleClick = (example: string) => {
    sendMessage(example);
  };

  const clearChat = () => {
    setMessages([{
      id: '1',
      message: '',
      response: "Hello! I'm your LibraryHub AI assistant. I can help you with book searches, library policies, recommendations, and more. How can I assist you today?",
      user: 'assistant',
      timestamp: new Date().toISOString()
    }]);
  };

  return (
    <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-lg border border-gray-200 h-[600px] flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b border-gray-200 bg-blue-50">
        <div className="flex items-center space-x-3">
          <div className="h-10 w-10 rounded-full bg-blue-600 flex items-center justify-center">
            <span className="text-white text-lg">🤖</span>
          </div>
          <div>
            <h2 className="text-lg font-semibold text-gray-900">AI Assistant</h2>
            <div className="flex items-center space-x-2">
              <div className={`h-2 w-2 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`}></div>
              <span className="text-sm text-gray-600">
                {isConnected ? 'Online' : 'Offline'}
              </span>
            </div>
          </div>
        </div>
        <button
          onClick={clearChat}
          className="text-sm text-gray-500 hover:text-gray-700 px-3 py-1 rounded-md hover:bg-gray-100"
        >
          Clear Chat
        </button>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`flex ${message.user === 'user' ? 'justify-end' : 'justify-start'}`}
          >
            <div
              className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                message.user === 'user'
                  ? 'bg-library-600 text-white'
                  : 'bg-gray-100 text-gray-900'
              }`}
            >
              {message.user === 'user' ? (
                <div className="flex items-start space-x-2">
                  <span className="text-sm">👤</span>
                  <div>
                    <p className="text-sm">{message.message}</p>
                    <p className="text-xs opacity-75 mt-1">
                      {new Date(message.timestamp).toLocaleTimeString()}
                    </p>
                  </div>
                </div>
              ) : (
                <div className="flex items-start space-x-2">
                  <span className="text-sm">🤖</span>
                  <div>
                    <p className="text-sm whitespace-pre-wrap">{message.response}</p>
                    <p className="text-xs opacity-75 mt-1">
                      {new Date(message.timestamp).toLocaleTimeString()}
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>
        ))}
        
        {isLoading && (
          <div className="flex justify-start">
            <div className="bg-gray-100 text-gray-900 max-w-xs lg:max-w-md px-4 py-2 rounded-lg">
              <div className="flex items-center space-x-2">
                <span className="text-sm">🤖</span>
                <div className="flex items-center space-x-1">
                  <span className="animate-spin">⏳</span>
                  <span className="text-sm">Thinking...</span>
                </div>
              </div>
            </div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>

      {/* Example queries */}
      {messages.length === 1 && (
        <div className="p-4 border-t border-gray-200 bg-gray-50">
          <p className="text-sm text-gray-600 mb-3">Try asking me:</p>
          <div className="flex flex-wrap gap-2">
            {exampleQueries.map((query, index) => (
              <button
                key={index}
                onClick={() => handleExampleClick(query)}
                className="text-xs bg-white text-gray-700 px-3 py-1 rounded-full border border-gray-300 hover:bg-library-50 hover:border-library-300 transition-colors"
              >
                {query}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Input */}
      <div className="p-4 border-t border-gray-200">
        <form onSubmit={handleSubmit} className="flex space-x-2">
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            placeholder="Ask me anything about the library..."
            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-library-500 focus:border-library-500"
            disabled={isLoading}
          />
          <button
            type="submit"
            disabled={!inputMessage.trim() || isLoading}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isLoading ? '⏳' : '📤'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Chatbot;

