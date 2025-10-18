import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../../services/api';

interface Message {
  id: string;
  content: string;
  isUser: boolean;
  timestamp: Date;
}

const Chatbot: React.FC = () => {
  const navigate = useNavigate();
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [isConnected, setIsConnected] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    checkConnection();
    addInitialMessage();
  }, []);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const checkConnection = async () => {
    try {
      const health = await apiService.getChatbotHealth();
      setIsConnected(health.status === 'healthy' || health.status === 'ok');
    } catch (error) {
      setIsConnected(false);
    }
  };

  const addInitialMessage = () => {
    const initialMessage: Message = {
      id: '0',
      content: "Hello! I'm your LibraryHub AI assistant. How can I help you today?",
      isUser: false,
      timestamp: new Date()
    };
    setMessages([initialMessage]);
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const addMessage = (content: string, isUser: boolean) => {
    const newMessage: Message = {
      id: Date.now().toString(),
      content,
      isUser,
      timestamp: new Date()
    };
    setMessages(prev => [...prev, newMessage]);
  };

  const sendMessage = async (messageText?: string) => {
    const message = messageText || inputValue.trim();
    if (!message) return;

    addMessage(message, true);
    setInputValue('');
    setIsTyping(true);

    try {
      const response = await apiService.sendChatMessage(message);
      addMessage(response.response || 'Sorry, I could not process your request.', false);
    } catch (error: any) {
      console.error('Chat error:', error);
      addMessage('Sorry, there was an error connecting to the AI service. Please try again later.', false);
    } finally {
      setIsTyping(false);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    sendMessage();
  };

  const quickActions = [
    { icon: '📚', text: 'How do I reserve a book?', message: 'How do I reserve a book?' },
    { icon: '📖', text: 'Available books', message: 'What books are available?' },
    { icon: '⏳', text: 'Waiting list', message: 'How does the waiting list work?' },
    { icon: '💡', text: 'Recommend', message: 'Recommend a good book' }
  ];

  return (
      <div style={{
        minHeight: '100vh',
        backgroundColor: '#f3f4f6',
        display: 'flex',
        flexDirection: 'column'
      }}>
        {/* Header */}
        <div style={{
          backgroundColor: '#1f2937',
          color: 'white',
          padding: '1rem 2rem',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <h1 style={{ fontSize: '1.5rem', margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            🤖 AI Assistant
          </h1>
        </div>

        {/* Back Link */}
        <button
            onClick={() => navigate('/books')}
            style={{
              margin: '1rem 2rem',
              color: '#3b82f6',
              background: 'none',
              border: 'none',
              fontSize: '0.875rem',
              cursor: 'pointer',
              textAlign: 'left',
              padding: 0
            }}
        >
          ← Back to Library
        </button>

        {/* Chat Container */}
        <div style={{
          flex: 1,
          maxWidth: '1200px',
          width: '100%',
          margin: '0 auto 2rem',
          padding: '0 1rem',
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden'
        }}>
          <div style={{
            flex: 1,
            backgroundColor: 'white',
            borderRadius: '8px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden'
          }}>
            {/* Chat Header */}
            <div style={{
              padding: '1.5rem',
              borderBottom: '1px solid #e5e7eb',
              backgroundColor: '#f9fafb'
            }}>
              <h2 style={{
                fontSize: '1.25rem',
                color: '#1f2937',
                margin: 0,
                display: 'flex',
                alignItems: 'center',
                gap: '0.5rem'
              }}>
                LibraryHub Assistant
                <span style={{
                  display: 'inline-block',
                  width: '8px',
                  height: '8px',
                  borderRadius: '50%',
                  backgroundColor: isConnected ? '#10b981' : '#ef4444',
                  marginLeft: '0.5rem'
                }} />
              </h2>
              <p style={{
                fontSize: '0.875rem',
                color: '#6b7280',
                margin: '0.5rem 0 0 0'
              }}>
                Ask me anything about books, reservations, or the library!
              </p>
            </div>

            {/* Messages */}
            <div style={{
              flex: 1,
              padding: '1.5rem',
              overflowY: 'auto',
              display: 'flex',
              flexDirection: 'column',
              gap: '1rem'
            }}>
              {messages.map((msg) => (
                  <div
                      key={msg.id}
                      style={{
                        display: 'flex',
                        gap: '1rem',
                        maxWidth: '80%',
                        alignSelf: msg.isUser ? 'flex-end' : 'flex-start',
                        flexDirection: msg.isUser ? 'row-reverse' : 'row',
                        animation: 'fadeIn 0.3s ease-in'
                      }}
                  >
                    <div style={{
                      width: '36px',
                      height: '36px',
                      borderRadius: '50%',
                      backgroundColor: msg.isUser ? '#3b82f6' : '#10b981',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '1.2rem',
                      flexShrink: 0
                    }}>
                      {msg.isUser ? '👤' : '🤖'}
                    </div>
                    <div>
                      <div style={{
                        padding: '0.75rem 1rem',
                        borderRadius: '12px',
                        backgroundColor: msg.isUser ? '#3b82f6' : '#f3f4f6',
                        color: msg.isUser ? 'white' : '#1f2937',
                        wordWrap: 'break-word'
                      }}>
                        {msg.content}
                      </div>
                      <div style={{
                        fontSize: '0.75rem',
                        color: '#6b7280',
                        marginTop: '0.25rem'
                      }}>
                        {msg.timestamp.toLocaleTimeString()}
                      </div>
                    </div>
                  </div>
              ))}

              {/* Typing Indicator */}
              {isTyping && (
                  <div style={{
                    display: 'flex',
                    gap: '0.5rem',
                    alignItems: 'center',
                    padding: '1rem'
                  }}>
                    <div style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: '#9ca3af',
                      animation: 'bounce 1.4s infinite ease-in-out'
                    }} />
                    <div style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: '#9ca3af',
                      animation: 'bounce 1.4s infinite ease-in-out 0.16s'
                    }} />
                    <div style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: '#9ca3af',
                      animation: 'bounce 1.4s infinite ease-in-out 0.32s'
                    }} />
                    <span style={{ marginLeft: '0.5rem', color: '#6b7280', fontSize: '0.875rem' }}>
                  AI is thinking...
                </span>
                  </div>
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Input Area */}
            <div style={{
              padding: '1.5rem',
              borderTop: '1px solid #e5e7eb',
              backgroundColor: '#f9fafb'
            }}>
              {/* Quick Actions */}
              <div style={{
                display: 'flex',
                gap: '0.5rem',
                flexWrap: 'wrap',
                marginBottom: '1rem'
              }}>
                {quickActions.map((action, idx) => (
                    <button
                        key={idx}
                        onClick={() => sendMessage(action.message)}
                        style={{
                          padding: '0.5rem 1rem',
                          backgroundColor: 'white',
                          border: '1px solid #d1d5db',
                          borderRadius: '6px',
                          fontSize: '0.875rem',
                          cursor: 'pointer',
                          transition: 'all 0.2s'
                        }}
                        onMouseOver={(e) => {
                          e.currentTarget.style.backgroundColor = '#f3f4f6';
                          e.currentTarget.style.borderColor = '#3b82f6';
                        }}
                        onMouseOut={(e) => {
                          e.currentTarget.style.backgroundColor = 'white';
                          e.currentTarget.style.borderColor = '#d1d5db';
                        }}
                    >
                      {action.icon} {action.text}
                    </button>
                ))}
              </div>

              {/* Input Form */}
              <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '1rem' }}>
                <input
                    type="text"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    placeholder="Type your message..."
                    autoComplete="off"
                    style={{
                      flex: 1,
                      padding: '0.75rem 1rem',
                      border: '1px solid #d1d5db',
                      borderRadius: '8px',
                      fontSize: '1rem',
                      outline: 'none'
                    }}
                    onFocus={(e) => e.currentTarget.style.borderColor = '#3b82f6'}
                    onBlur={(e) => e.currentTarget.style.borderColor = '#d1d5db'}
                />
                <button
                    type="submit"
                    disabled={isTyping || !inputValue.trim()}
                    style={{
                      padding: '0.75rem 1.5rem',
                      backgroundColor: isTyping || !inputValue.trim() ? '#9ca3af' : '#3b82f6',
                      color: 'white',
                      border: 'none',
                      borderRadius: '8px',
                      fontSize: '1rem',
                      fontWeight: '500',
                      cursor: isTyping || !inputValue.trim() ? 'not-allowed' : 'pointer',
                      transition: 'background-color 0.2s'
                    }}
                    onMouseOver={(e) => {
                      if (!isTyping && inputValue.trim()) {
                        e.currentTarget.style.backgroundColor = '#2563eb';
                      }
                    }}
                    onMouseOut={(e) => {
                      if (!isTyping && inputValue.trim()) {
                        e.currentTarget.style.backgroundColor = '#3b82f6';
                      }
                    }}
                >
                  Send 📤
                </button>
              </form>
            </div>
          </div>
        </div>

        <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }
        @keyframes bounce {
          0%, 80%, 100% { transform: scale(0); }
          40% { transform: scale(1); }
        }
      `}</style>
      </div>
  );
};

export default Chatbot;