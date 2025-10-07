package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.Review;
import com.example.libraryhub.model.User;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.ReviewRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.WaitingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserContextService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private WaitingListRepository waitingListRepository;
    
    /**
     * Get comprehensive user context for AI chatbot personalization
     */
    public UserContext getUserContext(String username) {
        if (username == null) {
            return null;
        }
        
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                return null;
            }
            
            // Gather all user-specific data
            List<Reservation> reservations = reservationRepository.findWithBookByUser_Username(username);
            List<Review> userReviews = reviewRepository.findByBookId(null); // We'll need to add a method for this
            List<Book> reviewedBooks = reviewRepository.findRecentHighRatedBooksByUser(username);
            List<WaitingList> waitingListEntries = getWaitingListForUser(user);
            
            return new UserContext(user, reservations, userReviews, reviewedBooks, waitingListEntries);
        } catch (Exception e) {
            // Return null if there's any error fetching user data
            return null;
        }
    }
    
    /**
     * Generate a formatted summary of user's library activity for AI context
     */
    public String generateUserSummary(String username) {
        UserContext context = getUserContext(username);
        if (context == null) {
            return "User information not available.";
        }
        
        StringBuilder summary = new StringBuilder();
        User user = context.getUser();
        
        // Basic user info
        summary.append("User: ").append(user.getUsername())
               .append(" (").append(user.getRole().name()).append(")");
        summary.append(", Member since: ").append(user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM yyyy")));
        summary.append(". ");
        
        // Current reservations
        List<Reservation> reservations = context.getReservations();
        if (!reservations.isEmpty()) {
            summary.append("Currently borrowed: ");
            summary.append(reservations.stream()
                    .map(r -> "'" + r.getBook().getTitle() + "' by " + r.getBook().getAuthor())
                    .collect(Collectors.joining(", ")));
            summary.append(". ");
        } else {
            summary.append("No books currently borrowed. ");
        }
        
        // Reading history and preferences
        List<Book> reviewedBooks = context.getReviewedBooks();
        if (!reviewedBooks.isEmpty()) {
            summary.append("Recently enjoyed books: ");
            summary.append(reviewedBooks.stream()
                    .limit(3)
                    .map(book -> "'" + book.getTitle() + "' by " + book.getAuthor() + " (" + book.getGenre() + ")")
                    .collect(Collectors.joining(", ")));
            summary.append(". ");
            
            // Preferred genres from reviews
            Map<String, Long> genreCounts = reviewedBooks.stream()
                    .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
            if (!genreCounts.isEmpty()) {
                String preferredGenres = genreCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(2)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "));
                summary.append("Preferred genres: ").append(preferredGenres).append(". ");
            }
        }
        
        // Waiting list
        List<WaitingList> waitingList = context.getWaitingList();
        if (!waitingList.isEmpty()) {
            summary.append("Waiting for: ");
            summary.append(waitingList.stream()
                    .map(w -> "'" + w.getBook().getTitle() + "' by " + w.getBook().getAuthor())
                    .collect(Collectors.joining(", ")));
            summary.append(". ");
        }
        
        return summary.toString();
    }
    
    /**
     * Handle user-specific queries about their library activity
     */
    public String handleUserQuery(String query, String username) {
        UserContext context = getUserContext(username);
        if (context == null) {
            return "I couldn't access your user information. Please make sure you're logged in.";
        }
        
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("my books") || lowerQuery.contains("what do i have") || lowerQuery.contains("currently borrowed")) {
            return generateCurrentBooksResponse(context);
        } else if (lowerQuery.contains("my reviews") || lowerQuery.contains("books i rated") || lowerQuery.contains("what did i review")) {
            return generateReviewHistoryResponse(context);
        } else if (lowerQuery.contains("waiting") || lowerQuery.contains("queue") || lowerQuery.contains("waitlist")) {
            return generateWaitingListResponse(context);
        } else if (lowerQuery.contains("my history") || lowerQuery.contains("reading history") || lowerQuery.contains("books i read")) {
            return generateReadingHistoryResponse(context);
        } else if (lowerQuery.contains("my profile") || lowerQuery.contains("account info") || lowerQuery.contains("my details")) {
            return generateProfileResponse(context);
        } else if (lowerQuery.contains("recommend") && (lowerQuery.contains("me") || lowerQuery.contains("my"))) {
            return generatePersonalRecommendationResponse(context);
        }
        
        return null; // Let other handlers process the query
    }
    
    // Helper methods for specific response types
    private String generateCurrentBooksResponse(UserContext context) {
        List<Reservation> reservations = context.getReservations();
        if (reservations.isEmpty()) {
            return "📚 **Your Current Books:**\n\nYou don't have any books currently borrowed. Visit our catalog to find something interesting to read!";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("📚 **Your Current Books:**\n\n");
        
        for (Reservation reservation : reservations) {
            Book book = reservation.getBook();
            response.append("• **").append(book.getTitle()).append("** by ").append(book.getAuthor()).append("\n");
            response.append("  Genre: ").append(book.getGenre()).append("\n");
            response.append("  Borrowed on: ").append(reservation.getReservationDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("\n\n");
        }
        
        response.append("💡 Don't forget to return your books on time to avoid late fees!");
        return response.toString();
    }
    
    private String generateReviewHistoryResponse(UserContext context) {
        List<Book> reviewedBooks = context.getReviewedBooks();
        if (reviewedBooks.isEmpty()) {
            return "📝 **Your Reviews:**\n\nYou haven't reviewed any books yet. After reading a book, consider leaving a review to help other readers!";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("📝 **Your Recent Reviews:**\n\n");
        
        reviewedBooks.stream().limit(5).forEach(book -> {
            response.append("⭐ **").append(book.getTitle()).append("** by ").append(book.getAuthor()).append("\n");
            response.append("   Genre: ").append(book.getGenre()).append("\n\n");
        });
        
        response.append("Thanks for contributing to our community with your reviews! 🎉");
        return response.toString();
    }
    
    private String generateWaitingListResponse(UserContext context) {
        List<WaitingList> waitingList = context.getWaitingList();
        if (waitingList.isEmpty()) {
            return "⏳ **Your Waiting List:**\n\nYou're not currently waiting for any books. When a book you want isn't available, you can join its waiting list!";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("⏳ **Your Waiting List:**\n\n");
        
        for (WaitingList entry : waitingList) {
            Book book = entry.getBook();
            response.append("• **").append(book.getTitle()).append("** by ").append(book.getAuthor()).append("\n");
            response.append("  Joined waitlist: ").append(entry.getJoinDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("\n\n");
        }
        
        response.append("📧 We'll notify you when these books become available!");
        return response.toString();
    }
    
    private String generateReadingHistoryResponse(UserContext context) {
        List<Book> reviewedBooks = context.getReviewedBooks();
        if (reviewedBooks.isEmpty()) {
            return "📖 **Your Reading History:**\n\nYour reading history will appear here as you borrow and review books. Start exploring our catalog!";
        }
        
        // Analyze reading patterns
        Map<String, Long> genreCounts = reviewedBooks.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
        
        StringBuilder response = new StringBuilder();
        response.append("📖 **Your Reading History:**\n\n");
        response.append("**Books you've enjoyed:**\n");
        
        reviewedBooks.stream().limit(8).forEach(book -> {
            response.append("• ").append(book.getTitle()).append(" by ").append(book.getAuthor())
                    .append(" (").append(book.getGenre()).append(")\n");
        });
        
        if (!genreCounts.isEmpty()) {
            response.append("\n📊 **Your favorite genres:**\n");
            genreCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .forEach(entry -> response.append("• ").append(entry.getKey())
                            .append(" (").append(entry.getValue()).append(" books)\n"));
        }
        
        return response.toString();
    }
    
    private String generateProfileResponse(UserContext context) {
        User user = context.getUser();
        StringBuilder response = new StringBuilder();
        
        response.append("👤 **Your Profile:**\n\n");
        response.append("**Username:** ").append(user.getUsername()).append("\n");
        response.append("**Email:** ").append(user.getEmail()).append("\n");
        response.append("**Role:** ").append(user.getRole().name()).append("\n");
        response.append("**Member since:** ").append(user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("\n");
        response.append("**Account status:** ").append(user.isActive() ? "Active ✅" : "Inactive ❌").append("\n\n");
        
        // Activity summary
        response.append("📊 **Activity Summary:**\n");
        response.append("• Currently borrowed books: ").append(context.getReservations().size()).append("\n");
        response.append("• Books reviewed: ").append(context.getReviewedBooks().size()).append("\n");
        response.append("• Books in waiting list: ").append(context.getWaitingList().size()).append("\n");
        
        return response.toString();
    }
    
    private String generatePersonalRecommendationResponse(UserContext context) {
        List<Book> reviewedBooks = context.getReviewedBooks();
        if (reviewedBooks.isEmpty()) {
            return "🎯 **Personal Recommendations:**\n\nTo get personalized recommendations, start by reviewing some books you've read! This helps me understand your preferences better.";
        }
        
        // Analyze user preferences
        Map<String, Long> genrePreferences = reviewedBooks.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
        
        StringBuilder response = new StringBuilder();
        response.append("🎯 **Personal Recommendations based on your reading history:**\n\n");
        
        if (!genrePreferences.isEmpty()) {
            String topGenre = genrePreferences.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .findFirst().orElse("Fiction");
            
            response.append("Since you enjoy **").append(topGenre).append("** books, here are some suggestions:\n");
            response.append("• Look for more books in the ").append(topGenre).append(" section\n");
            response.append("• Try books by authors you haven't read yet in this genre\n");
            response.append("• Consider book series in ").append(topGenre).append("\n\n");
        }
        
        response.append("💡 For specific book recommendations, try asking: 'Recommend me a fantasy series' or 'Find me books similar to [book title]'");
        
        return response.toString();
    }
    
    // Helper method to get waiting list entries for a user
    private List<WaitingList> getWaitingListForUser(User user) {
        try {
            // This is a simplified approach - in a real implementation you'd add a proper query method
            return waitingListRepository.findAll().stream()
                    .filter(entry -> entry.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
    
    // Data holder class for user context
    public static class UserContext {
        private final User user;
        private final List<Reservation> reservations;
        private final List<Review> reviews;
        private final List<Book> reviewedBooks;
        private final List<WaitingList> waitingList;
        
        public UserContext(User user, List<Reservation> reservations, List<Review> reviews, 
                          List<Book> reviewedBooks, List<WaitingList> waitingList) {
            this.user = user;
            this.reservations = reservations != null ? reservations : List.of();
            this.reviews = reviews != null ? reviews : List.of();
            this.reviewedBooks = reviewedBooks != null ? reviewedBooks : List.of();
            this.waitingList = waitingList != null ? waitingList : List.of();
        }
        
        // Getters
        public User getUser() { return user; }
        public List<Reservation> getReservations() { return reservations; }
        public List<Review> getReviews() { return reviews; }
        public List<Book> getReviewedBooks() { return reviewedBooks; }
        public List<WaitingList> getWaitingList() { return waitingList; }
        
        public boolean hasActivity() {
            return !reservations.isEmpty() || !reviewedBooks.isEmpty() || !waitingList.isEmpty();
        }
    }
}