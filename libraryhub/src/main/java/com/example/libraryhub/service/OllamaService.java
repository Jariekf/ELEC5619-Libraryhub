package com.example.libraryhub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
// PolicyService is now in the same package

import com.example.libraryhub.model.*;
import com.example.libraryhub.repository.*;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for integrating with Ollama to run open-source LLMs like Llama locally
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    private final WebClient.Builder webClientBuilder;
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private UserContextService userContextService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private WaitingListRepository waitingListRepository;
    
    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;
    
    @Value("${ollama.model:llama3.2}")
    private String defaultModel;
    
    @Value("${ollama.timeout:30}")
    private int timeoutSeconds;

    /**
     * Generate a response using the specified Ollama model
     */
    public Mono<String> generateResponse(String prompt) {
        return generateResponse(prompt, defaultModel);
    }

    /**
     * Generate a response using a specific model
     */
    public Mono<String> generateResponse(String prompt, String model) {
        WebClient client = webClientBuilder
                .baseUrl(ollamaBaseUrl)
                .build();

        Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                        "temperature", 0.7,
                        "max_tokens", 500,
                        "top_p", 0.9
                )
        );

        log.debug("Sending request to Ollama: model={}, prompt={}", model, prompt);

        return client.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .map(response -> {
                    String responseText = (String) response.get("response");
                    log.debug("Received response from Ollama: {}", responseText);
                    return responseText != null ? responseText.trim() : "I'm having trouble generating a response right now.";
                })
                .doOnError(error -> log.error("Error calling Ollama API: {}", error.getMessage(), error))
                .onErrorReturn("I'm currently experiencing technical difficulties. Please try again later.");
    }

    /**
     * Check if Ollama service is available
     */
    public Mono<Boolean> isAvailable() {
        WebClient client = webClientBuilder
                .baseUrl(ollamaBaseUrl)
                .build();

        return client.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(5))
                .map(response -> true)
                .doOnSuccess(available -> log.debug("Ollama service available: {}", available))
                .onErrorReturn(false);
    }

    /**
     * List available models
     */
    public Mono<String[]> listModels() {
        WebClient client = webClientBuilder
                .baseUrl(ollamaBaseUrl)
                .build();

        return client.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(10))
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    java.util.List<Map<String, Object>> models = 
                        (java.util.List<Map<String, Object>>) response.get("models");
                    
                    if (models == null) return new String[0];
                    
                    return models.stream()
                            .map(model -> (String) model.get("name"))
                            .toArray(String[]::new);
                })
                .doOnError(error -> log.error("Error listing Ollama models: {}", error.getMessage()))
                .onErrorReturn(new String[0]);
    }

    /**
     * Create a library-specific prompt with context and role-based information
     */
    public String createLibraryPrompt(String userMessage, String username, String context) {
        return createLibraryPrompt(userMessage, username, context, false);
    }
    
    /**
     * Create a library-specific prompt with enhanced context for admin users
     */
    public String createLibraryPrompt(String userMessage, String username, String context, boolean isAdmin) {
        StringBuilder prompt = new StringBuilder();
        
        // Base system prompt
        prompt.append("You are LibraryBot, an AI assistant for LibraryHub digital library system. ");
        prompt.append("You help users with book recommendations, library policies, series suggestions, and general questions. ");
        prompt.append("Be helpful, friendly, informative, and concise. ");
        
        // Role-specific instructions
        if (isAdmin) {
            prompt.append("You are LibraryBot assisting a library administrator. ");
            prompt.append("CRITICAL INSTRUCTIONS: ");
            prompt.append("1. You MUST use ONLY the real database data provided below. ");
            prompt.append("2. NEVER invent, guess, or hallucinate any names, usernames, numbers, or statistics. ");
            prompt.append("3. If the real data shows no information, say 'No data available' or 'No users found'. ");
            prompt.append("4. Do NOT provide SQL queries or technical instructions. ");
            prompt.append("5. Give direct answers based ONLY on the actual data shown. ");
            
            // Add real database context for admin queries
            String realDataContext = buildRealDatabaseContext(userMessage);
            if (realDataContext != null && !realDataContext.trim().isEmpty()) {
                log.info("=== INJECTING REAL DATABASE DATA FOR ADMIN QUERY ===\n{}", realDataContext);
                prompt.append("\n\n=== REAL LIBRARY DATABASE DATA (USE ONLY THIS) ===\n");
                prompt.append(realDataContext);
                prompt.append("\n=== END OF REAL DATA ===\n");
                prompt.append("\nSTRICT RULE: Answer using ONLY the usernames and data shown above. ");
                prompt.append("If a username is not listed above, do NOT mention any names like 'John Doe' or 'Emily Johnson'. ");
                prompt.append("Instead say 'Based on the current data, [describe what you see or say no data available]'.");
            } else {
                log.warn("No database context available for admin query: {}", userMessage);
                prompt.append("\n\nNo database context available for this query. ");
                prompt.append("Please inform the admin that specific database access is required for this type of query.");
            }
            
            // Add admin policy context
            try {
                var adminPolicies = policyService.getAdminPolicies();
                prompt.append("\nAdmin Policies: Monthly budget $").append(adminPolicies.get("monthlyBudget"));
                prompt.append(", Staff levels: ").append(adminPolicies.get("staffLevels"));
                prompt.append(", Data retention: ").append(adminPolicies.get("dataRetention")).append(". ");
            } catch (Exception e) {
                log.debug("Could not load admin policies for prompt");
            }
        } else {
            prompt.append("You are assisting a library customer. ");
            prompt.append("Focus on helping them find books, understand policies, and get recommendations. ");
        }
        
        // Add library policy context for FAQ questions
        if (userMessage.toLowerCase().matches(".*(hour|fine|borrow|policy|rule|reserve|membership).*")) {
            try {
                var policies = policyService.getAllPolicies();
                prompt.append("Library Info: ");
                if (policies.containsKey("hours")) {
                    var hours = (java.util.Map<String, String>) policies.get("hours");
                    prompt.append("Hours Mon-Thu 9-8pm, Fri 9-6pm, Sat 10-4pm, Sun 12-5pm. ");
                }
                if (policies.containsKey("borrowing")) {
                    var borrowing = (java.util.Map<String, Object>) policies.get("borrowing");
                    prompt.append("Max books: ").append(borrowing.get("maxBooks"));
                    prompt.append(", Loan period: ").append(borrowing.get("loanPeriod")).append(". ");
                }
                if (policies.containsKey("fines")) {
                    var fines = (java.util.Map<String, Object>) policies.get("fines");
                    prompt.append("Overdue fine: ").append(fines.get("overduePerDay")).append(" per day. ");
                }
            } catch (Exception e) {
                log.debug("Could not load policies for prompt");
            }
        }
        
        // Add specific context
        if (context != null && !context.trim().isEmpty()) {
            prompt.append("Context: ").append(context).append(" ");
        }
        
        // Add comprehensive user context
        if (username != null) {
            prompt.append("User: ").append(username).append(". ");
            
            // Include user's library activity and preferences
            try {
                String userSummary = userContextService.generateUserSummary(username);
                if (userSummary != null && !userSummary.equals("User information not available.")) {
                    prompt.append("User Context: ").append(userSummary).append(" ");
                }
            } catch (Exception e) {
                log.debug("Could not load user context for prompt: {}", e.getMessage());
            }
        }
        
        // Add the actual question
        prompt.append("\n\nQuestion: ").append(userMessage);
        prompt.append("\n\nProvide a helpful, accurate response as LibraryBot. ");
        prompt.append("If asking about specific books, mention they can search the catalog. ");
        prompt.append("If asking about library services, reference actual policies when possible. ");
        prompt.append("Keep responses concise but informative:");
        
        return prompt.toString();
    }
    
    /**
     * Build real database context for admin queries - ADMIN ACCESS ONLY
     * This method provides actual data from the database to prevent AI hallucination
     */
    private String buildRealDatabaseContext(String userMessage) {
        try {
            StringBuilder context = new StringBuilder();
            String lowerMessage = userMessage.toLowerCase();
            
            // User-related queries (including most/top borrower queries)
            if (lowerMessage.contains("user") || lowerMessage.contains("customer") || 
                lowerMessage.contains("borrow") || lowerMessage.contains("most") || 
                lowerMessage.contains("top") || lowerMessage.contains("who")) {
                List<User> allUsers = userRepository.findAll();
                List<Reservation> allReservations = reservationRepository.findAllWithDetails();
                
                context.append("=== USERS DATA ===\n");
                context.append("Total users in system: ").append(allUsers.size()).append("\n");
                
                // User list with basic info
                context.append("Active users:\n");
                allUsers.stream().limit(10).forEach(user -> {
                    context.append("- ").append(user.getUsername())
                            .append(" (").append(user.getRole().name())
                            .append(", joined: ").append(user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM yyyy")))
                            .append(")\n");
                });
                if (allUsers.size() > 10) {
                    context.append("... and ").append(allUsers.size() - 10).append(" more users\n");
                }
                
                // Borrowing statistics
                if (!allReservations.isEmpty()) {
                    context.append("\n=== BORROWING DATA ===\n");
                    context.append("Total active reservations: ").append(allReservations.size()).append("\n");
                    
                    // Count borrows per user
                    Map<String, Long> userBorrowCounts = allReservations.stream()
                            .collect(Collectors.groupingBy(r -> r.getUser().getUsername(), Collectors.counting()));
                    
                    if (!userBorrowCounts.isEmpty()) {
                        context.append("\nCURRENT ACTIVE BORROWERS (sorted by most books borrowed):\n");
                        userBorrowCounts.entrySet().stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .forEach(entry -> context.append("- ").append(entry.getKey())
                                        .append(": ").append(entry.getValue()).append(" currently borrowed book")
                                        .append(entry.getValue() > 1 ? "s" : "").append("\n"));
                        
                        // Explicitly identify the top borrower
                        String topBorrower = userBorrowCounts.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse("None");
                        Long topBorrowCount = userBorrowCounts.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getValue)
                                .orElse(0L);
                        
                        context.append("\nTOP BORROWER: ").append(topBorrower)
                                .append(" with ").append(topBorrowCount).append(" currently borrowed book")
                                .append(topBorrowCount > 1 ? "s" : "").append("\n");
                    } else {
                        context.append("\nNo borrowing data available.\n");
                    }
                } else {
                    context.append("\n=== BORROWING DATA ===\n");
                    context.append("No active reservations in the system.\n");
                    context.append("No users currently have borrowed books.\n");
                }
            }
            
            // Book-related queries
            if (lowerMessage.contains("book") || lowerMessage.contains("popular") || lowerMessage.contains("borrow")) {
                List<Book> allBooks = bookRepository.findAll();
                List<Review> allReviews = reviewRepository.findAll();
                
                context.append("\n=== BOOKS DATA ===\n");
                context.append("Total books in catalog: ").append(allBooks.size()).append("\n");
                
                long availableBooks = allBooks.stream().mapToLong(book -> book.isAvailable() ? 1 : 0).sum();
                context.append("Available books: ").append(availableBooks).append("\n");
                context.append("Books currently borrowed: ").append(allBooks.size() - availableBooks).append("\n");
                
                // Popular books (those currently borrowed)
                List<Book> borrowedBooks = allBooks.stream().filter(book -> !book.isAvailable()).collect(Collectors.toList());
                if (!borrowedBooks.isEmpty()) {
                    context.append("\nCurrently borrowed books:\n");
                    borrowedBooks.stream().limit(5).forEach(book -> 
                        context.append("- ").append(book.getTitle()).append(" by ").append(book.getAuthor()).append("\n")
                    );
                }
            }
            
            // Review-related queries
            if (lowerMessage.contains("review")) {
                List<Review> allReviews = reviewRepository.findAll();
                context.append("\n=== REVIEWS DATA ===\n");
                context.append("Total reviews: ").append(allReviews.size()).append("\n");
                
                if (!allReviews.isEmpty()) {
                    double avgRating = allReviews.stream()
                            .mapToInt(Review::getRating)
                            .average().orElse(0.0);
                    context.append("Average rating: ").append(String.format("%.1f", avgRating)).append("/5\n");
                    
                    // Most active reviewers
                    Map<String, Long> reviewCounts = allReviews.stream()
                            .collect(Collectors.groupingBy(Review::getUsername, Collectors.counting()));
                    
                    context.append("Most active reviewers:\n");
                    reviewCounts.entrySet().stream()
                            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                            .limit(3)
                            .forEach(entry -> context.append("- ").append(entry.getKey())
                                    .append(": ").append(entry.getValue()).append(" review(s)\n"));
                }
            }
            
            // Waiting list queries
            if (lowerMessage.contains("waiting")) {
                List<WaitingList> waitingEntries = waitingListRepository.findAll();
                context.append("\n=== WAITING LIST DATA ===\n");
                context.append("Total waiting list entries: ").append(waitingEntries.size()).append("\n");
                
                if (!waitingEntries.isEmpty()) {
                    Map<String, List<WaitingList>> bookWaitingMap = waitingEntries.stream()
                            .collect(Collectors.groupingBy(w -> w.getBook().getTitle()));
                    
                    context.append("Books with waiting lists:\n");
                    bookWaitingMap.entrySet().stream()
                            .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                            .limit(3)
                            .forEach(entry -> context.append("- ").append(entry.getKey())
                                    .append(": ").append(entry.getValue().size()).append(" waiting\n"));
                }
            }
            
            return context.toString();
            
        } catch (Exception e) {
            log.error("Error building database context for admin: {}", e.getMessage(), e);
            return "Database context unavailable due to system error.";
        }
    }
    
    /**
     * Determine if user is an admin based on username
     */
    public boolean isAdminUser(String username) {
        // Simple admin detection - you could enhance this with a proper user service
        return username != null && (username.toLowerCase().contains("admin") || 
                                   username.toLowerCase().contains("librarian") ||
                                   username.toLowerCase().contains("manager"));
    }
}
