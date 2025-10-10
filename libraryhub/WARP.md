# 🏛️ LibraryHub - AI-Enhanced Library Management System

This file provides comprehensive guidance for WARP (warp.dev) when working with the LibraryHub codebase.

## 🎯 Project Overview
LibraryHub is an advanced Spring Boot-based library management system featuring authentication-aware AI chatbot capabilities. Users can reserve books, join waiting lists, write reviews, and receive personalized AI-powered recommendations through a sophisticated role-based system.

## 🤖 AI Chatbot Features

### Authentication-Aware System
The LibraryHub AI chatbot integrates with Spring Security for role-based responses:

- **Authentication Required**: Users must be logged in to access the chatbot
- **Role-Based Context**: Different responses for ADMIN and CUSTOMER roles
- **CSRF Protection**: Full security integration with Spring Security
- **Session Management**: Uses authenticated user context for personalization
- **Comprehensive User Data**: Access to full user profiles, reservations, reviews, and reading history

### Ollama Integration
The system uses Ollama with the Llama 3.2 model for local AI processing:

- **Local AI Model**: Llama 3.2 running via Ollama on localhost:11434
- **Real-time Responses**: Direct API integration for fast responses
- **Fallback System**: Rule-based responses when AI is unavailable
- **Context-Aware**: Prompts include user role, personal data, and library-specific context

### Policy Integration
Comprehensive library policy support from `library-policies.properties`:

- **Borrowing Rules**: Loan periods, limits, renewal policies
- **Fine Structure**: Late fees, maximum fines, processing charges
- **Operating Hours**: Daily schedules, holiday hours
- **Membership**: Registration requirements, account policies
- **Digital Services**: Online catalog, reservation system
- **Admin Policies**: Budget, inventory, maintenance schedules

### Customer Features
- **Comprehensive Personal Data**: Full access to user profiles, reservation history, reviews, and preferences
- **Personalized Recommendations**: Book series suggestions based on detailed reading history
- **Account Information**: Current borrows, reservations, waiting lists, and reading preferences
- **Library Assistance**: Hours, policies, procedures, account help
- **Book Search**: Natural language search with availability checking
- **FAQ Support**: Comprehensive policy questions and answers
- **Personalized Queries**: "My books", "my reviews", "my reservations" with detailed responses

### Admin Features
- **Comprehensive User Management**: 
  - Complete user roster with activity summaries
  - Detailed individual user reports with full history
  - User activity analysis and engagement metrics
- **Advanced Book & Loan Management**:
  - All current borrows with user details and dates
  - Comprehensive overdue analysis with fine calculations
  - Popular books ranking based on borrowing and review data
- **System Analytics**:
  - Real-time statistics from actual database
  - User engagement and borrowing patterns
  - Review analytics and rating summaries
- **Operational Insights**:
  - Waiting list management and analysis
  - Budget tracking and financial overview
  - System health monitoring and performance metrics
- **Administrative Commands**:
  - Specific user lookup: "user [username]" for detailed reports
  - Bulk queries: "all users", "user activity", "popular books"
  - System queries: "overdue books", "waiting list", "all reviews"

## 📊 System Architecture

### Service Layer Enhancement
Enhanced microservices architecture for comprehensive functionality:

```
ChatbotService (Main orchestrator)
├── OllamaService (AI integration with role-based prompts)
├── PolicyService (Library policies and FAQ responses)
├── SeriesRecommendationService (Personalized book suggestions)
├── AdminChatbotService (Admin system queries)
├── AdminUserService (Comprehensive user/system data access)
├── UserContextService (Complete user data aggregation)
├── RecommendationService (General recommendations)
└── Enhanced Repository Layer (All entity repositories)
```

### Database Integration
- **Complete User Profiles**: Full user data including preferences, history, and activity
- **Comprehensive Book Data**: Full catalog with metadata, availability, and statistics
- **Detailed Reservations**: Current and historical borrowing with date tracking
- **Rich Review System**: User ratings, comments, and engagement metrics
- **Waiting List Management**: Queue management and notification system
- **Real-time Analytics**: Live data aggregation for admin insights
- **Policy Configuration**: Centralized policy and configuration management

## 🎯 Example Usage

### Customer Interactions
```
"What are your opening hours?"
"Find books by Stephen King"
"Recommend a fantasy series for me"
"How do I renew a book?"
"Show me my current reservations"
"What books have I reviewed?"
"Am I on any waiting lists?"
"Show me my reading history"
```

### Admin Interactions
```
"Show me all users"
"What's the user activity like?"
"Show me user alice" (detailed user report)
"List all current borrows"
"Show me overdue books"
"What are the most popular books?"
"Show me all reviews"
"What's on the waiting list?"
"Give me system statistics"
"Show me the budget status"
"Check system health"
"Admin help" (comprehensive command menu)
```

## 🚀 Technical Implementation

### Security Features
- **Authentication Gates**: All endpoints require valid user session
- **Role-Based Access**: Different service methods for ADMIN vs CUSTOMER
- **CSRF Protection**: Integrated with Spring Security tokens
- **Session Validation**: Continuous authentication state checking
- **Data Access Control**: Role-based data filtering and access restrictions

### Frontend Enhancements
- **Advanced Markdown Rendering**: Rich text formatting with proper bold/italic/list support
- **Role-Aware UI**: Different examples and features based on user role
- **Authentication Flow**: Seamless login integration with session management
- **Real-time Interaction**: AJAX-based chat interface with enhanced user experience
- **Personalized Examples**: User-specific query suggestions based on role and data

## 🔧 Development Status

### ✅ Completed Features
- Authentication-aware chatbot system with comprehensive user data integration
- Ollama/Llama 3.2 integration with enhanced context awareness
- Advanced role-based response system with full data access
- Complete policy integration and comprehensive FAQ support
- Personalized series recommendation engine
- Comprehensive admin dashboard with real-time data analytics
- Advanced security integration with data access control
- Enhanced frontend UI with improved markdown rendering
- Complete user context service with full data aggregation
- Admin user service with system-wide data access and reporting

### 🚧 Future Enhancements
- Enhanced machine learning models for better recommendations
- Advanced natural language processing with conversation memory
- Integration with external library systems and databases
- Predictive analytics for library management
- Multi-language support for diverse user base

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
