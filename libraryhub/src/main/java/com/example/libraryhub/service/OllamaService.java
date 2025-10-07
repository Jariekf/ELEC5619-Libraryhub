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

import java.time.Duration;
import java.util.Map;

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
            prompt.append("You are assisting a library administrator. ");
            prompt.append("Provide detailed operational information, system insights, and administrative guidance when appropriate. ");
            
            // Add admin policy context
            try {
                var adminPolicies = policyService.getAdminPolicies();
                prompt.append("Admin Context: Monthly budget $").append(adminPolicies.get("monthlyBudget"));
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
     * Determine if user is an admin based on username
     */
    public boolean isAdminUser(String username) {
        // Simple admin detection - you could enhance this with a proper user service
        return username != null && (username.toLowerCase().contains("admin") || 
                                   username.toLowerCase().contains("librarian") ||
                                   username.toLowerCase().contains("manager"));
    }
}