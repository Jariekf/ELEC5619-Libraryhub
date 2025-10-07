# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview
LibraryHub is a Spring Boot-based library management system that allows users to reserve books, join waiting lists, write reviews, and receive AI-powered recommendations. The system supports role-based access control with Admin and Customer roles.

## Development Commands

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run tests  
./mvnw test

# Run specific test class
./mvnw test -Dtest=LibraryhubApplicationTests

# Start the application
./mvnw spring-boot:run

# Package as JAR
./mvnw clean package
```

### Database Setup
- Ensure MySQL is running on localhost:3306
- Create database: `CREATE DATABASE librarydb;`
- Create user: `CREATE USER 'libraryuser'@'localhost' IDENTIFIED BY 'mypassword';`
- Grant privileges: `GRANT ALL PRIVILEGES ON librarydb.* TO 'libraryuser'@'localhost';`
- Application runs on http://localhost:8080
- Default admin credentials: username=`admin`, password=`admin123`

### AI Chatbot Setup
```bash
# Quick setup with Llama 3.2
./setup-ollama.sh

# Manual setup
brew install ollama
ollama serve &
ollama pull llama3.2

# Test the chatbot at: http://localhost:8080/chatbot/chat
# NOTE: Login required - chatbot is now authentication-aware!
```

## Architecture

### Core Domain Models
- **User** (abstract): Base class using single-table inheritance with Admin and Customer subclasses
- **Book**: Central entity with availability tracking and relationships to Reviews and WaitingList
- **Reservation**: Tracks active book borrowing by customers
- **Review**: Star ratings and comments on books by users
- **WaitingList**: Queue management for unavailable books

### Key Services
- **BookService**: Core book management and CRUD operations
- **ReservationService**: Handles book checkout/return and availability updates
- **WaitingListService**: Manages queue when books are unavailable
- **ReviewService**: Review submission, validation, and average rating calculations
- **RecommendationService**: AI-powered book recommendations based on user reading history
- **ChatbotService**: AI chatbot using Microsoft DialoGPT-small via DJL (Deep Java Library)
- **AuthService**: User authentication and registration

### Security Configuration
- Role-based access control (ADMIN/CUSTOMER roles)
- Form-based authentication with login/logout
- Secured endpoints based on user roles
- Admin users can manage books and delete any reviews
- Customers can reserve books and manage their own reviews

### AI/ML Integration
- **Ollama Integration**: Uses open-source LLMs (Llama 3.2) for intelligent chatbot responses
- **Hybrid Chatbot System**: AI-powered responses with rule-based fallback for reliability
- **Intent Classification**: Smart routing of queries to appropriate handlers (FAQ, search, recommendations)
- **Recommendations**: Collaborative filtering based on user ratings and reading patterns
- **Natural Language Processing**: Parses user queries into structured database searches
- Models run locally via Ollama (no external API dependencies)

### Database Design
- Single-table inheritance for User hierarchy (Admin/Customer)
- JPA/Hibernate with MySQL backend
- Cascade operations for data integrity (e.g., deleting books removes waiting list entries)
- Optimistic locking and transaction management for concurrent access

### Web Layer
- **Thymeleaf** templates for server-side rendering
- **Spring Security** integration in templates for role-based UI elements
- RESTful endpoints for chatbot API (`/api/chatbot/send`)
- Form-based interactions for book operations (reserve, return, review)

## Development Notes

### Adding New Features
- Controllers should use `@AuthenticationPrincipal UserDetails` for user context
- Always validate user permissions before sensitive operations
- Use `RedirectAttributes` for flash messages in redirect scenarios
- Follow the existing service → repository → entity pattern

### Testing Strategy
- Integration tests in `src/test/java`
- Test database configuration uses the same MySQL setup
- Mock AI services in tests to avoid model loading overhead

### AI Chatbot Development
- **Functional Requirements Implemented**:
  - FR-23: Chat Interface with natural language interaction
  - FR-24: Library FAQ support (hours, policies, borrowing rules)
  - FR-26: Series Recommendations based on user history
  - FR-27: Natural Language Search parsing

- **Configuration Options**:
  ```properties
  chatbot.use-ai=true                    # Enable/disable AI responses
  chatbot.fallback-to-rules=true         # Enable rule-based fallback
  ollama.base-url=http://localhost:11434 # Ollama service URL
  ollama.model=llama3.2                  # Default model to use
  ollama.timeout=30                      # Response timeout in seconds
  ```

- **Hybrid Architecture**:
  - AI responses via Ollama/Llama when available
  - Rule-based fallback for reliability and speed
  - Intent classification routes queries appropriately
  - Context-aware prompts with library-specific knowledge

- **Development Tips**:
  - Test with Ollama offline to verify fallback behavior
  - Monitor logs for AI vs rule-based response usage
  - Customize prompts in `OllamaService.createLibraryPrompt()`
  - Add new intents in `ChatbotIntent` enum and update classification logic
