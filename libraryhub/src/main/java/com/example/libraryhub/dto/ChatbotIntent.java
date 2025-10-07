package com.example.libraryhub.dto;

/**
 * Enumeration of different chatbot intents for routing user queries
 * to appropriate handlers.
 */
public enum ChatbotIntent {
    /**
     * Questions about library policies, rules, hours, etc.
     */
    FAQ,
    
    /**
     * Book series recommendations based on user history
     */
    SERIES_RECOMMENDATION,
    
    /**
     * Natural language book search queries
     */
    BOOK_SEARCH,
    
    /**
     * General conversational queries
     */
    GENERAL_CONVERSATION,
    
    /**
     * Unknown or unclear intent
     */
    UNKNOWN
}