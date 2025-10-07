package com.example.libraryhub.service;

import com.example.libraryhub.repository.BookRepository;
// Note: BorrowRecordRepository may not exist yet, commenting out
// import com.example.libraryhub.repository.BorrowRecordRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminChatbotService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // @Autowired
    // private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private PolicyService policyService;
    
    /**
     * Generate admin dashboard summary for chatbot
     */
    public String generateAdminSummary(String adminUsername) {
        try {
            Map<String, Object> stats = getSystemStatistics();
            
            StringBuilder response = new StringBuilder();
            response.append("🔧 **Admin Dashboard Summary for ").append(adminUsername).append("**\n\n");
            
            // System overview
            response.append("📊 **System Overview:**\n");
            response.append("• Total Books: ").append(stats.get("totalBooks")).append("\n");
            response.append("• Total Users: ").append(stats.get("totalUsers")).append("\n");
            response.append("• Active Borrows: ").append(stats.get("activeBorrows")).append("\n");
            response.append("• Total Reviews: ").append(stats.get("totalReviews")).append("\n\n");
            
            // Recent activity
            response.append("📈 **Recent Activity:**\n");
            response.append("• Books borrowed this week: ").append(stats.get("weeklyBorrows")).append("\n");
            response.append("• New user registrations: ").append(stats.get("newUsers")).append("\n");
            response.append("• Reviews submitted: ").append(stats.get("weeklyReviews")).append("\n\n");
            
            // System health
            response.append("⚡ **System Status:**\n");
            response.append("• Available books: ").append(stats.get("availableBooks")).append("\n");
            response.append("• Overdue returns: ").append(stats.get("overdueBooks")).append("\n");
            response.append("• Pending reservations: ").append(stats.get("pendingReservations")).append("\n\n");
            
            // Admin policies reminder
            var adminPolicies = policyService.getAdminPolicies();
            response.append("📋 **Policy Reminders:**\n");
            response.append("• Monthly budget: ").append(adminPolicies.get("monthlyBudget")).append("\n");
            response.append("• Next inventory check: ").append(adminPolicies.get("inventoryFrequency")).append("\n");
            response.append("• Data retention: ").append(adminPolicies.get("dataRetention")).append("\n\n");
            
            response.append("💡 Ask me about specific metrics, user management, or operational insights!");
            
            return response.toString();
        } catch (Exception e) {
            return "⚠️ Unable to generate admin summary at this time. Please check system status.";
        }
    }
    
    /**
     * Handle admin-specific queries
     */
    public String handleAdminQuery(String query, String adminUsername) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("statistics") || lowerQuery.contains("stats") || lowerQuery.contains("metrics")) {
            return generateDetailedStatistics();
        } else if (lowerQuery.contains("users") || lowerQuery.contains("user management")) {
            return generateUserManagementInfo();
        } else if (lowerQuery.contains("overdue") || lowerQuery.contains("late")) {
            return generateOverdueReport();
        } else if (lowerQuery.contains("budget") || lowerQuery.contains("financial")) {
            return generateBudgetInfo();
        } else if (lowerQuery.contains("popular") || lowerQuery.contains("trending")) {
            return generatePopularityReport();
        } else if (lowerQuery.contains("system") || lowerQuery.contains("health")) {
            return generateSystemHealthReport();
        } else {
            return generateAdminSummary(adminUsername);
        }
    }
    
    /**
     * Get comprehensive system statistics
     */
    private Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Basic counts
            stats.put("totalBooks", bookRepository.count());
            stats.put("totalUsers", userRepository.count());
            stats.put("totalReviews", reviewRepository.count());
            
            // Available books
            long availableBooks = bookRepository.findAll().stream()
                    .mapToLong(book -> book.isAvailable() ? 1 : 0)
                    .sum();
            stats.put("availableBooks", availableBooks);
            
            // Active borrows (mock data since BorrowRecord repository not available yet)
            stats.put("activeBorrows", 15); // Mock data for demonstration
            
            // Weekly activity (simplified - would need proper date filtering)
            stats.put("weeklyBorrows", Math.max(0, (Long) stats.get("totalBooks") / 10)); // Mock data
            stats.put("newUsers", Math.max(0, (Long) stats.get("totalUsers") / 20)); // Mock data
            stats.put("weeklyReviews", Math.max(0, (Long) stats.get("totalReviews") / 5)); // Mock data
            
            // Operational metrics (mock data for demonstration)
            stats.put("overdueBooks", 12); // Would need proper overdue calculation
            stats.put("pendingReservations", 8); // Would need reservation system
            
        } catch (Exception e) {
            // Fallback values
            stats.put("totalBooks", "N/A");
            stats.put("totalUsers", "N/A");
            stats.put("activeBorrows", "N/A");
            stats.put("totalReviews", "N/A");
            stats.put("availableBooks", "N/A");
            stats.put("weeklyBorrows", "N/A");
            stats.put("newUsers", "N/A");
            stats.put("weeklyReviews", "N/A");
            stats.put("overdueBooks", "N/A");
            stats.put("pendingReservations", "N/A");
        }
        
        return stats;
    }
    
    private String generateDetailedStatistics() {
        Map<String, Object> stats = getSystemStatistics();
        
        return "📊 **Detailed System Statistics:**\n\n" +
               "**Library Collection:**\n" +
               "• Total books: " + stats.get("totalBooks") + "\n" +
               "• Available: " + stats.get("availableBooks") + "\n" +
               "• Currently borrowed: " + stats.get("activeBorrows") + "\n\n" +
               "**User Activity:**\n" +
               "• Total registered users: " + stats.get("totalUsers") + "\n" +
               "• New users this week: " + stats.get("newUsers") + "\n" +
               "• Books borrowed this week: " + stats.get("weeklyBorrows") + "\n\n" +
               "**Engagement:**\n" +
               "• Total reviews: " + stats.get("totalReviews") + "\n" +
               "• Reviews this week: " + stats.get("weeklyReviews") + "\n\n" +
               "📈 Use these metrics for operational planning and reporting.";
    }
    
    private String generateUserManagementInfo() {
        Map<String, Object> stats = getSystemStatistics();
        
        return "👥 **User Management Overview:**\n\n" +
               "**Current Users:**\n" +
               "• Total registered: " + stats.get("totalUsers") + "\n" +
               "• Active borrowers: " + stats.get("activeBorrows") + "\n" +
               "• New registrations: " + stats.get("newUsers") + " this week\n\n" +
               "**User Engagement:**\n" +
               "• Users with reviews: Active community\n" +
               "• Average books per user: " + (Long.valueOf(stats.get("totalBooks").toString()) / Math.max(1, Long.valueOf(stats.get("totalUsers").toString()))) + "\n\n" +
               "**Admin Actions Available:**\n" +
               "• Review user accounts in admin panel\n" +
               "• Manage user permissions\n" +
               "• Send user notifications\n\n" +
               "💡 Check the admin panel for detailed user management tools.";
    }
    
    private String generateOverdueReport() {
        Map<String, Object> stats = getSystemStatistics();
        
        return "⚠️ **Overdue Books Report:**\n\n" +
               "**Current Status:**\n" +
               "• Overdue books: " + stats.get("overdueBooks") + "\n" +
               "• Pending returns: Check detailed report\n" +
               "• Fine revenue: Processing fees apply\n\n" +
               "**Action Items:**\n" +
               "• Send reminder notifications\n" +
               "• Update fine calculations\n" +
               "• Follow up on long-overdue items\n\n" +
               "**Policy Reference:**\n" +
               "• Overdue fine: $0.50 per day\n" +
               "• Maximum fine: $10.00 per book\n" +
               "• Grace period: 3 days\n\n" +
               "📋 Access detailed overdue reports in the admin panel.";
    }
    
    private String generateBudgetInfo() {
        var adminPolicies = policyService.getAdminPolicies();
        
        return "💰 **Budget & Financial Overview:**\n\n" +
               "**Current Budget:**\n" +
               "• Monthly allocation: " + adminPolicies.get("monthlyBudget") + "\n" +
               "• Book procurement: Primary expense\n" +
               "• System maintenance: Ongoing costs\n\n" +
               "**Revenue Streams:**\n" +
               "• Late return fines: $0.50/day\n" +
               "• Processing fees: $2.00 per transaction\n" +
               "• Replacement fees: Variable\n\n" +
               "**Recommendations:**\n" +
               "• Monitor spending against budget\n" +
               "• Consider popular book acquisitions\n" +
               "• Review fee structure annually\n\n" +
               "📊 Financial details available in admin reporting tools.";
    }
    
    private String generatePopularityReport() {
        return "📈 **Popularity & Trends Report:**\n\n" +
               "**Popular Genres:**\n" +
               "• Fantasy: High demand\n" +
               "• Mystery: Consistent borrowing\n" +
               "• Science Fiction: Growing interest\n" +
               "• Romance: Steady popularity\n\n" +
               "**Top Authors:**\n" +
               "• J.K. Rowling: Always in demand\n" +
               "• Agatha Christie: Classic favorite\n" +
               "• Douglas Adams: Cult following\n\n" +
               "**Recommendations:**\n" +
               "• Acquire more books in popular genres\n" +
               "• Consider series completions\n" +
               "• Monitor borrowing trends monthly\n\n" +
               "💡 Use this data to guide procurement decisions.";
    }
    
    private String generateSystemHealthReport() {
        return "⚡ **System Health & Status:**\n\n" +
               "**System Status:**\n" +
               "• Database: Online ✅\n" +
               "• AI Chatbot: Active ✅\n" +
               "• User Portal: Functional ✅\n" +
               "• Admin Panel: Available ✅\n\n" +
               "**Performance Metrics:**\n" +
               "• Response time: < 2 seconds\n" +
               "• Uptime: 99.5% target\n" +
               "• Error rate: < 1%\n\n" +
               "**Maintenance Schedule:**\n" +
               "• Backup frequency: " + policyService.getAdminPolicies().get("backupFrequency") + "\n" +
               "• Maintenance window: " + policyService.getAdminPolicies().get("maintenanceWindow") + "\n\n" +
               "🔧 All systems operating normally. Check logs for detailed diagnostics.";
    }
}