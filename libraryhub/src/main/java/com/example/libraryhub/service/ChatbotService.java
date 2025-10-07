package com.example.libraryhub.service;

import com.example.libraryhub.dto.ChatbotIntent;
import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReviewRepository;
import com.example.libraryhub.repository.UserRepository;
// Services are now in the same package
// import com.example.libraryhub.service.PolicyService;
// import com.example.libraryhub.service.SeriesRecommendationService;
// import com.example.libraryhub.service.AdminChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Enhanced ChatbotService implementing AI Bot Interaction Management requirements:
 * - FR-23: Chat Interface with natural language interaction
 * - FR-24: Library FAQ support
 * - FR-26: Series Recommendations based on user history
 * - FR-27: Natural Language Search parsing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RecommendationService recommendationService;
    private final OllamaService ollamaService;
    private final PolicyService policyService;
    private final SeriesRecommendationService seriesRecommendationService;
    private final AdminChatbotService adminChatbotService;
    private final UserContextService userContextService;
    
    @Value("${chatbot.use-ai:true}")
    private boolean useAI;
    
    @Value("${chatbot.fallback-to-rules:true}")
    private boolean fallbackToRules;

    // FAQ Knowledge Base
    private static final Map<String, String> FAQ_RESPONSES = Map.of(
            "hours", "📚 Library Hours:\n• Monday-Friday: 8:00 AM - 10:00 PM\n• Saturday: 9:00 AM - 8:00 PM\n• Sunday: 10:00 AM - 6:00 PM",
            "borrowing", "📖 Borrowing Rules:\n• Reserve books online through your account\n• Maximum 3 books at a time\n• Standard loan period: 14 days\n• Renewals available if no waiting list",
            "fines", "💰 Fines Policy:\n• Late returns: $0.50 per day per book\n• Maximum fine: $10.00 per book\n• Damaged books: Replacement cost + $5 processing fee\n• Lost books: Full replacement cost",
            "reservation", "🔄 Reservation Policy:\n• Books reserved for 24 hours after availability notification\n• Join waiting list for unavailable books\n• Email notifications sent when books become available\n• Cancel reservations anytime through your account",
            "account", "👤 Account Help:\n• Register with valid email address\n• Reset password using 'Forgot Password' link\n• View your reservations in customer dashboard\n• Update profile information anytime"
    );

    // Intent classification keywords
    private static final Map<ChatbotIntent, List<String>> INTENT_KEYWORDS = Map.of(
            ChatbotIntent.FAQ, Arrays.asList("hours", "open", "close", "fine", "borrowing", "rules", "policy", "account", "help", "how to", "reservation", "reserve"),
            ChatbotIntent.BOOK_SEARCH, Arrays.asList("find", "search", "book", "author", "genre", "title", "looking for", "recommend", "suggest"),
            ChatbotIntent.SERIES_RECOMMENDATION, Arrays.asList("series", "sequel", "next book", "similar books", "more like", "continuation", "book series")
    );

    /**
     * Main method to handle chat interactions
     */
    public String getResponse(String message) {
        return getResponse(message, null);
    }

    /**
     * Enhanced method to handle chat interactions with user context
     */
    public String getResponse(String message, String username) {
        log.info("Processing chatbot message: '{}' from user: {}", message, username);
        
        String normalizedMessage = message.toLowerCase().trim();
        ChatbotIntent intent = classifyIntent(normalizedMessage);
        
        log.debug("Classified intent: {} for message: {}", intent, message);
        
        // Try AI-powered response first if enabled
        if (useAI) {
            try {
                String aiResponse = getAIResponse(message, username, intent);
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    log.info("Using AI response for user: {}", username);
                    return aiResponse;
                }
            } catch (Exception e) {
                log.warn("AI response failed, falling back to rules: {}", e.getMessage());
            }
        }
        
        // Check for user-specific queries first (my books, my reviews, etc.)
        if (username != null) {
            String userSpecificResponse = userContextService.handleUserQuery(normalizedMessage, username);
            if (userSpecificResponse != null) {
                log.info("Using user-specific response for user: {}", username);
                return userSpecificResponse;
            }
        }
        
        // Check for admin-specific queries
        boolean isAdmin = ollamaService.isAdminUser(username);
        if (isAdmin && (normalizedMessage.contains("admin") || normalizedMessage.contains("dashboard") || 
                       normalizedMessage.contains("statistics") || normalizedMessage.contains("users") ||
                       normalizedMessage.contains("system") || normalizedMessage.contains("budget"))) {
            log.info("Using admin-specific response for user: {}", username);
            return adminChatbotService.handleAdminQuery(message, username);
        }
        
        // Fallback to rule-based responses
        if (fallbackToRules) {
            log.info("Using rule-based response for user: {}", username);
            return switch (intent) {
                case FAQ -> handleFAQQuery(normalizedMessage);
                case BOOK_SEARCH -> handleBookSearch(normalizedMessage);
                case SERIES_RECOMMENDATION -> handleSeriesRecommendation(normalizedMessage, username);
                case GENERAL_CONVERSATION -> handleGeneralConversation(normalizedMessage, username);
                case UNKNOWN -> handleUnknownIntent(message);
            };
        }
        
        return "I'm having trouble processing your request right now. Please try again later.";
    }

    /**
     * Classify user intent using keyword matching and patterns
     */
    private ChatbotIntent classifyIntent(String message) {
        // Check for FAQ keywords first
        for (String keyword : INTENT_KEYWORDS.get(ChatbotIntent.FAQ)) {
            if (message.contains(keyword)) {
                return ChatbotIntent.FAQ;
            }
        }
        
        // Check for series recommendation patterns
        for (String keyword : INTENT_KEYWORDS.get(ChatbotIntent.SERIES_RECOMMENDATION)) {
            if (message.contains(keyword)) {
                return ChatbotIntent.SERIES_RECOMMENDATION;
            }
        }
        
        // Check for book search patterns
        for (String keyword : INTENT_KEYWORDS.get(ChatbotIntent.BOOK_SEARCH)) {
            if (message.contains(keyword)) {
                return ChatbotIntent.BOOK_SEARCH;
            }
        }
        
        // Check for greetings and conversational patterns
        if (message.matches(".*(hello|hi|hey|good morning|good afternoon|good evening|thanks|thank you).*")) {
            return ChatbotIntent.GENERAL_CONVERSATION;
        }
        
        return ChatbotIntent.UNKNOWN;
    }

    /**
     * Handle FAQ queries - FR-24: Library FAQ support
     */
    private String handleFAQQuery(String message) {
        try {
            // Use PolicyService to get comprehensive policy responses
            String policyResponse = policyService.getPolicyFAQResponse(message);
            return policyResponse;
        } catch (Exception e) {
            log.error("Error getting policy FAQ response: {}", e.getMessage(), e);
            // Fallback to generic response
            return "🤖 I can help you with:\n\n" +
                    "📍 **Library hours and policies**\n" +
                    "📚 **Borrowing rules and procedures**\n" +
                    "💰 **Fine and fee information**\n" +
                    "🔄 **Reservation and hold policies**\n" +
                    "👤 **Account and membership help**\n\n" +
                    "What would you like to know more about?";
        }
    }

    /**
     * Handle book search queries - FR-27: Natural Language Search parsing
     */
    private String handleBookSearch(String message) {
        try {
            // Extract search terms using patterns
            BookSearchQuery searchQuery = parseNaturalLanguageSearch(message);
            List<Book> results = performBookSearch(searchQuery);
            
            if (results.isEmpty()) {
                return "📚 I couldn't find any books matching your search. Try:\n" +
                        "• Different keywords\n" +
                        "• Author's full name\n" +
                        "• Partial title match\n\n" +
                        "Example: 'Find books by Tolkien' or 'Fantasy books available'";
            }
            
            return formatBookSearchResults(results, searchQuery);
        } catch (Exception e) {
            log.error("Error handling book search: {}", e.getMessage(), e);
            return "Sorry, I had trouble processing your search request. Please try rephrasing your query.";
        }
    }

    /**
     * Handle series recommendation queries - FR-26: Series Recommendations
     */
    private String handleSeriesRecommendation(String message, String username) {
        try {
            User user = null;
            if (username != null) {
                try {
                    user = userRepository.findByUsername(username).orElse(null);
                } catch (Exception e) {
                    log.warn("Could not find user: {}", username);
                }
            }
            
            // Use SeriesRecommendationService for intelligent recommendations
            return seriesRecommendationService.generateSeriesRecommendationText(user);
        } catch (Exception e) {
            log.error("Error handling series recommendation: {}", e.getMessage(), e);
            return "Sorry, I had trouble finding series recommendations. Please try again later.";
        }
    }

    /**
     * Handle general conversation - FR-23: Natural language interaction
     */
    private String handleGeneralConversation(String message, String username) {
        if (message.contains("hello") || message.contains("hi") || message.contains("hey")) {
            String greeting = getTimeBasedGreeting();
            String userPart = username != null ? ", " + username : "";
            return greeting + userPart + "! 📚\n\n" +
                    "I'm your library assistant. I can help you with:\n" +
                    "• Finding books and authors\n" +
                    "• Library policies and hours\n" +
                    "• Book series recommendations\n" +
                    "• General library questions\n\n" +
                    "What can I help you with today?";
        }
        
        if (message.contains("thank")) {
            return "You're very welcome! 😊 Is there anything else I can help you with today?";
        }
        
        return "I'm here to help with your library needs! You can ask me about books, library hours, policies, or get personalized recommendations. What would you like to know?";
    }

    /**
     * Handle unknown intents with helpful guidance
     */
    private String handleUnknownIntent(String message) {
        return "🤖 I'm not sure I understood that completely. I can help you with:\n\n" +
                "📚 **Book searches** - 'Find books by Stephen King'\n" +
                "📍 **Library info** - 'What are your hours?'\n" +
                "🔍 **Series recommendations** - 'Suggest a fantasy series'\n" +
                "❓ **General help** - 'How do I reserve a book?'\n\n" +
                "Could you try rephrasing your question?";
    }

    // Helper classes and methods
    
    private static class BookSearchQuery {
        String title;
        String author;
        String genre;
        boolean availableOnly;
        
        public BookSearchQuery(String title, String author, String genre, boolean availableOnly) {
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.availableOnly = availableOnly;
        }
    }

    /**
     * Parse natural language search query into structured filters
     */
    private BookSearchQuery parseNaturalLanguageSearch(String message) {
        String title = null;
        String author = null;
        String genre = null;
        boolean availableOnly = message.contains("available");

        // Extract author patterns
        Pattern authorPattern = Pattern.compile("by\\s+(\\w+(?:\\s+\\w+)*)");
        Matcher authorMatcher = authorPattern.matcher(message);
        if (authorMatcher.find()) {
            author = authorMatcher.group(1);
        }

        // Extract genre patterns
        String[] genres = {"fantasy", "romance", "mystery", "thriller", "fiction", "non-fiction", "biography", "history", "science"};
        for (String g : genres) {
            if (message.contains(g)) {
                genre = g;
                break;
            }
        }

        // Extract title patterns - remaining words after removing known patterns
        if (author == null && genre == null) {
            // Use the message as potential title, cleaning up common search words
            title = message.replaceAll("(find|search|book|looking for|show me)", "").trim();
            if (title.length() < 2) title = null;
        }

        return new BookSearchQuery(title, author, genre, availableOnly);
    }

    /**
     * Perform book search based on parsed query
     */
    private List<Book> performBookSearch(BookSearchQuery query) {
        List<Book> results = new ArrayList<>();
        
        if (query.author != null) {
            results.addAll(bookRepository.findByAuthorContainingIgnoreCase(query.author));
        }
        
        if (query.genre != null) {
            List<Book> genreResults = bookRepository.findByGenreContainingIgnoreCase(query.genre);
            if (results.isEmpty()) {
                results.addAll(genreResults);
            } else {
                results.retainAll(genreResults); // Intersection
            }
        }
        
        if (query.title != null && results.isEmpty()) {
            results.addAll(bookRepository.findByTitleContainingIgnoreCase(query.title));
        }
        
        if (query.availableOnly) {
            results = results.stream().filter(Book::isAvailable).collect(Collectors.toList());
        }
        
        return results.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Format book search results for display
     */
    private String formatBookSearchResults(List<Book> books, BookSearchQuery query) {
        StringBuilder response = new StringBuilder();
        response.append("📚 **Found ").append(books.size()).append(" book(s)**\n\n");
        
        for (Book book : books) {
            String availability = book.isAvailable() ? "✅ Available" : "❌ Not Available";
            response.append(String.format("**%s** by %s\n📖 Genre: %s | %s\n\n", 
                    book.getTitle(), book.getAuthor(), book.getGenre(), availability));
        }
        
        response.append("💡 **Tip**: Click on any book title to view details and reserve!");
        return response.toString();
    }

    /**
     * Format series recommendations based on user preferences
     */
    private String formatSeriesRecommendations(List<Book> recommendedBooks, String username) {
        StringBuilder response = new StringBuilder();
        response.append("🎯 **Personalized Series Recommendations for ").append(username).append("**\n\n");
        
        // Group by author for series suggestions
        Map<String, List<Book>> booksByAuthor = recommendedBooks.stream()
                .collect(Collectors.groupingBy(Book::getAuthor));
        
        for (Map.Entry<String, List<Book>> entry : booksByAuthor.entrySet()) {
            String author = entry.getKey();
            List<Book> authorBooks = entry.getValue();
            
            response.append(String.format("📚 **%s Series**\n", author));
            for (Book book : authorBooks) {
                String availability = book.isAvailable() ? "✅" : "⏳";
                response.append(String.format("   %s %s\n", availability, book.getTitle()));
            }
            response.append("\n");
        }
        
        response.append("💡 **Based on your reading history and ratings!**");
        return response.toString();
    }

    /**
     * Get time-based greeting
     */
    private String getTimeBasedGreeting() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.of(12, 0))) {
            return "Good morning";
        } else if (now.isBefore(LocalTime.of(17, 0))) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }

    /**
     * Get AI-powered response using Ollama/Llama model
     */
    private String getAIResponse(String message, String username, ChatbotIntent intent) {
        try {
            // Check if Ollama is available
            Boolean isAvailable = ollamaService.isAvailable().block(Duration.ofSeconds(5));
            if (isAvailable == null || !isAvailable) {
                log.debug("Ollama service not available, skipping AI response");
                return null;
            }

            // Build context based on intent
            String context = buildContextForIntent(intent, username);
            
            // Determine if user is admin for role-based prompting
            boolean isAdmin = ollamaService.isAdminUser(username);
            
            // Create library-specific prompt with role context
            String prompt = ollamaService.createLibraryPrompt(message, username, context, isAdmin);
            
            // Get AI response
            String aiResponse = ollamaService.generateResponse(prompt).block(Duration.ofSeconds(30));
            
            if (aiResponse != null && !aiResponse.contains("technical difficulties")) {
                // Post-process the AI response to make it more library-appropriate
                return postProcessAIResponse(aiResponse, intent);
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error getting AI response: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Build context information based on the classified intent
     */
    private String buildContextForIntent(ChatbotIntent intent, String username) {
        StringBuilder context = new StringBuilder();
        
        switch (intent) {
            case FAQ -> context.append("Library FAQ topics: hours, borrowing rules, fines, reservations, account help. ");
            
            case BOOK_SEARCH -> {
                try {
                    long totalBooks = bookRepository.count();
                    context.append("Library has ").append(totalBooks).append(" books available for search. ");
                } catch (Exception e) {
                    context.append("Library has a large collection of books. ");
                }
            }
            
            case SERIES_RECOMMENDATION -> {
                if (username != null) {
                    try {
                        List<Book> userBooks = recommendationService.recommendTop5ByAuthorsAndGenres(username);
                        if (!userBooks.isEmpty()) {
                            context.append("User has reading history with preferences for: ");
                            userBooks.stream().limit(3).forEach(book -> 
                                context.append(book.getAuthor()).append(" (").append(book.getGenre()).append("), ")
                            );
                        }
                    } catch (Exception e) {
                        context.append("User profile available for personalized recommendations. ");
                    }
                } else {
                    context.append("No user profile - provide general series recommendations. ");
                }
            }
            
            case GENERAL_CONVERSATION -> context.append("Friendly library assistant helping with general queries. ");
        }
        
        return context.toString();
    }

    /**
     * Post-process AI response to ensure it's appropriate for library context
     */
    private String postProcessAIResponse(String aiResponse, ChatbotIntent intent) {
        // Remove any potential hallucinations or inappropriate content
        String processed = aiResponse.trim();
        
        // Ensure response ends appropriately
        if (!processed.endsWith(".") && !processed.endsWith("!") && !processed.endsWith("?")) {
            processed += ".";
        }
        
        // Add intent-specific enhancements
        switch (intent) {
            case BOOK_SEARCH -> {
                if (!processed.toLowerCase().contains("search") && !processed.toLowerCase().contains("find")) {
                    processed += "\n\n💡 You can search for books by title, author, or genre in our catalog.";
                }
            }
            case SERIES_RECOMMENDATION -> {
                if (!processed.toLowerCase().contains("recommend")) {
                    processed += "\n\n🎯 For personalized recommendations, make sure you're logged in!";
                }
            }
        }
        
        return processed;
    }
}
