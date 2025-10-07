package com.example.libraryhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web controller for serving the chatbot HTML pages
 */
@Controller
public class ChatWebController {

    /**
     * Show the chat interface page
     */
    @GetMapping("/chat")
    public String showChatPage() {
        return "chatbot/chat";
    }
    
    /**
     * Alternative route for chat page
     */
    @GetMapping("/chatbot")
    public String showChatPageAlt() {
        return "chatbot/chat";
    }
}