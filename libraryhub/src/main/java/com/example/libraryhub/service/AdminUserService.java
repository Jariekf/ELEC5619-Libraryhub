package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.Review;
import com.example.libraryhub.model.User;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.ReviewRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.WaitingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    
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
    
    @Autowired
    private UserContextService userContextService;
    
    /**
     * Handle admin queries about users and system data
     */
    public String handleAdminUserQuery(String query, String adminUsername) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("user") && (lowerQuery.contains("list") || lowerQuery.contains("all"))) {
            return generateAllUsersReport();
        } else if (lowerQuery.contains("user") && lowerQuery.contains("activity")) {
            return generateUserActivityReport();
        } else if (lowerQuery.contains("borrow") && (lowerQuery.contains("list") || lowerQuery.contains("all"))) {
            return generateAllBorrowsReport();
        } else if (lowerQuery.contains("overdue") || lowerQuery.contains("late")) {
            return generateOverdueReport();
        } else if (lowerQuery.contains("popular") && lowerQuery.contains("books")) {
            return generatePopularBooksReport();
        } else if (lowerQuery.contains("review") && (lowerQuery.contains("all") || lowerQuery.contains("recent"))) {
            return generateAllReviewsReport();
        } else if (lowerQuery.contains("waiting") && lowerQuery.contains("list")) {
            return generateWaitingListReport();
        } else if (lowerQuery.matches(".*user\\s+\\w+.*")) {
            // Extract username from query like "user john" or "show user alice"
            String[] words = lowerQuery.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].equals("user")) {
                    return generateSpecificUserReport(words[i + 1]);
                }
            }
        }
        
        return null; // Let other handlers process the query
    }
    
    private String generateAllUsersReport() {
        List<User> allUsers = userRepository.findAll();
        
        StringBuilder response = new StringBuilder();
        response.append("👥 **All Library Users Report:**\n\n");
        
        // Summary stats
        long customerCount = allUsers.stream().filter(u -> u.getRole().name().equals("CUSTOMER")).count();
        long adminCount = allUsers.stream().filter(u -> u.getRole().name().equals("ADMIN")).count();
        long activeCount = allUsers.stream().filter(User::isActive).count();
        
        response.append("📊 **Summary:**\n");
        response.append("• Total users: ").append(allUsers.size()).append("\n");
        response.append("• Customers: ").append(customerCount).append("\n");
        response.append("• Admins: ").append(adminCount).append("\n");
        response.append("• Active accounts: ").append(activeCount).append("\n\n");
        
        response.append("📋 **User List:**\n");
        allUsers.stream().limit(20).forEach(user -> {
            String status = user.isActive() ? "✅ Active" : "❌ Inactive";
            response.append("• **").append(user.getUsername()).append("** (").append(user.getRole().name()).append(")\n");
            response.append("  Email: ").append(user.getEmail()).append(" | ").append(status).append("\n");
            response.append("  Member since: ").append(user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("\n\n");
        });
        
        if (allUsers.size() > 20) {
            response.append("... and ").append(allUsers.size() - 20).append(" more users.\n");
        }
        
        return response.toString();
    }
    
    private String generateUserActivityReport() {
        List<User> allUsers = userRepository.findAll();
        List<Reservation> allReservations = reservationRepository.findAllWithDetails();
        List<Review> allReviews = reviewRepository.findAll();
        
        StringBuilder response = new StringBuilder();
        response.append("📈 **User Activity Report:**\n\n");
        
        // Active borrowers
        Map<String, Long> userBorrowCounts = allReservations.stream()
                .collect(Collectors.groupingBy(r -> r.getUser().getUsername(), Collectors.counting()));
        
        response.append("🔥 **Most Active Borrowers:**\n");
        userBorrowCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> response.append("• ").append(entry.getKey())
                        .append(": ").append(entry.getValue()).append(" books\n"));
        
        response.append("\n");
        
        // Active reviewers
        Map<String, Long> userReviewCounts = allReviews.stream()
                .collect(Collectors.groupingBy(Review::getUsername, Collectors.counting()));
        
        response.append("⭐ **Most Active Reviewers:**\n");
        userReviewCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> response.append("• ").append(entry.getKey())
                        .append(": ").append(entry.getValue()).append(" reviews\n"));
        
        return response.toString();
    }
    
    private String generateAllBorrowsReport() {
        List<Reservation> allReservations = reservationRepository.findAllWithDetails();
        
        StringBuilder response = new StringBuilder();
        response.append("📚 **All Current Borrows Report:**\n\n");
        
        response.append("📊 **Summary:** ").append(allReservations.size()).append(" total active reservations\n\n");
        
        response.append("📋 **Current Borrows:**\n");
        allReservations.stream()
                .sorted((a, b) -> b.getReservationDate().compareTo(a.getReservationDate()))
                .limit(25)
                .forEach(reservation -> {
                    response.append("• **").append(reservation.getBook().getTitle()).append("**\n");
                    response.append("  Borrowed by: ").append(reservation.getUser().getUsername()).append("\n");
                    response.append("  Date: ").append(reservation.getReservationDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("\n");
                    response.append("  Author: ").append(reservation.getBook().getAuthor()).append("\n\n");
                });
        
        if (allReservations.size() > 25) {
            response.append("... and ").append(allReservations.size() - 25).append(" more reservations.\n");
        }
        
        return response.toString();
    }
    
    private String generateOverdueReport() {
        // For demonstration - in a real system you'd calculate actual overdue dates
        List<Reservation> allReservations = reservationRepository.findAllWithDetails();
        
        StringBuilder response = new StringBuilder();
        response.append("⚠️ **Overdue Books Report:**\n\n");
        response.append("📊 **Current Status:** Monitoring ").append(allReservations.size()).append(" active loans\n\n");
        
        response.append("📋 **Overdue Management:**\n");
        response.append("• Standard loan period: 14 days\n");
        response.append("• Grace period: 3 days\n");
        response.append("• Daily fine: $0.50\n");
        response.append("• Maximum fine per book: $10.00\n\n");
        
        response.append("🔍 **Recent Long-term Borrows:**\n");
        allReservations.stream()
                .sorted((a, b) -> a.getReservationDate().compareTo(b.getReservationDate()))
                .limit(10)
                .forEach(reservation -> {
                    long daysAgo = java.time.temporal.ChronoUnit.DAYS.between(
                            reservation.getReservationDate().toLocalDate(), 
                            java.time.LocalDate.now());
                    String status = daysAgo > 14 ? "⚠️ OVERDUE" : "📅 Active";
                    
                    response.append("• **").append(reservation.getBook().getTitle()).append("**\n");
                    response.append("  User: ").append(reservation.getUser().getUsername());
                    response.append(" | Days: ").append(daysAgo).append(" | ").append(status).append("\n\n");
                });
        
        return response.toString();
    }
    
    private String generatePopularBooksReport() {
        List<Reservation> allReservations = reservationRepository.findAllWithDetails();
        List<Review> allReviews = reviewRepository.findAll();
        
        StringBuilder response = new StringBuilder();
        response.append("🔥 **Popular Books Report:**\n\n");
        
        // Most borrowed books
        Map<String, Long> bookBorrowCounts = allReservations.stream()
                .collect(Collectors.groupingBy(r -> r.getBook().getTitle() + " by " + r.getBook().getAuthor(), 
                        Collectors.counting()));
        
        response.append("📚 **Most Borrowed Books:**\n");
        bookBorrowCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> response.append("• ").append(entry.getKey())
                        .append(" (").append(entry.getValue()).append(" borrows)\n"));
        
        response.append("\n");
        
        // Most reviewed books
        Map<String, Long> bookReviewCounts = allReviews.stream()
                .collect(Collectors.groupingBy(r -> r.getBook().getTitle() + " by " + r.getBook().getAuthor(), 
                        Collectors.counting()));
        
        response.append("⭐ **Most Reviewed Books:**\n");
        bookReviewCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> response.append("• ").append(entry.getKey())
                        .append(" (").append(entry.getValue()).append(" reviews)\n"));
        
        return response.toString();
    }
    
    private String generateAllReviewsReport() {
        List<Review> allReviews = reviewRepository.findAll();
        
        StringBuilder response = new StringBuilder();
        response.append("⭐ **All Reviews Report:**\n\n");
        
        response.append("📊 **Summary:** ").append(allReviews.size()).append(" total reviews\n\n");
        
        // Average rating
        double avgRating = allReviews.stream()
                .mapToInt(Review::getRating)
                .average().orElse(0.0);
        response.append("📊 **Overall average rating:** ").append(String.format("%.1f", avgRating)).append("/5\n\n");
        
        response.append("📝 **Recent Reviews:**\n");
        allReviews.stream()
                .limit(15)
                .forEach(review -> {
                    String stars = "★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());
                    response.append("• **").append(review.getBook().getTitle()).append("**\n");
                    response.append("  Reviewer: ").append(review.getUsername());
                    response.append(" | Rating: ").append(stars).append("\n");
                    if (review.getComment() != null && !review.getComment().trim().isEmpty()) {
                        response.append("  Comment: *").append(review.getComment()).append("*\n");
                    }
                    response.append("\n");
                });
        
        return response.toString();
    }
    
    private String generateWaitingListReport() {
        List<WaitingList> allWaitingList = waitingListRepository.findAll();
        
        StringBuilder response = new StringBuilder();
        response.append("⏳ **Waiting List Report:**\n\n");
        
        response.append("📊 **Summary:** ").append(allWaitingList.size()).append(" total waiting list entries\n\n");
        
        // Group by book
        Map<String, List<WaitingList>> bookWaitingMap = allWaitingList.stream()
                .collect(Collectors.groupingBy(w -> w.getBook().getTitle() + " by " + w.getBook().getAuthor()));
        
        response.append("📚 **Books with Waiting Lists:**\n");
        bookWaitingMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .limit(15)
                .forEach(entry -> {
                    String bookTitle = entry.getKey();
                    List<WaitingList> waiters = entry.getValue();
                    response.append("• **").append(bookTitle).append("**\n");
                    response.append("  ").append(waiters.size()).append(" people waiting: ");
                    response.append(waiters.stream()
                            .map(w -> w.getUser().getUsername())
                            .limit(5)
                            .collect(Collectors.joining(", ")));
                    if (waiters.size() > 5) {
                        response.append("... (").append(waiters.size() - 5).append(" more)");
                    }
                    response.append("\n\n");
                });
        
        return response.toString();
    }
    
    private String generateSpecificUserReport(String targetUsername) {
        UserContextService.UserContext context = userContextService.getUserContext(targetUsername);
        if (context == null) {
            return "❌ **User Not Found:** No user found with username '" + targetUsername + "'";
        }
        
        User user = context.getUser();
        StringBuilder response = new StringBuilder();
        
        response.append("👤 **Detailed User Report: ").append(user.getUsername()).append("**\n\n");
        
        // Basic info
        response.append("**Profile:**\n");
        response.append("• Email: ").append(user.getEmail()).append("\n");
        response.append("• Role: ").append(user.getRole().name()).append("\n");
        response.append("• Status: ").append(user.isActive() ? "Active ✅" : "Inactive ❌").append("\n");
        response.append("• Member since: ").append(user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("\n\n");
        
        // Activity summary
        response.append("**Activity Summary:**\n");
        response.append("• Currently borrowed: ").append(context.getReservations().size()).append(" books\n");
        response.append("• Total reviews: ").append(context.getReviews().size()).append(" reviews\n");
        response.append("• Waiting list entries: ").append(context.getWaitingList().size()).append(" books\n");
        response.append("• Total books reserved: ").append(context.getAllReservedBookIds().size()).append("\n\n");
        
        // Current borrows
        if (!context.getReservations().isEmpty()) {
            response.append("**Current Borrows:**\n");
            context.getReservations().forEach(reservation -> {
                response.append("• ").append(reservation.getBook().getTitle())
                        .append(" (").append(reservation.getReservationDate().format(DateTimeFormatter.ofPattern("MMM dd"))).append(")\n");
            });
            response.append("\n");
        }
        
        // Recent reviews
        if (!context.getReviews().isEmpty()) {
            response.append("**Recent Reviews:**\n");
            context.getReviews().stream().limit(5).forEach(review -> {
                String stars = "★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());
                response.append("• ").append(review.getBook().getTitle())
                        .append(" - ").append(stars).append("\n");
            });
        }
        
        return response.toString();
    }
}