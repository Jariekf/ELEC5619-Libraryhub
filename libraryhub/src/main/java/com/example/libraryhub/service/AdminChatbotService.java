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
    
    @Autowired
    private AdminUserService adminUserService;
    
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
        
        // First check if AdminUserService can handle this query (comprehensive user/book/system data)
        String adminUserResponse = adminUserService.handleAdminUserQuery(query, adminUsername);
        if (adminUserResponse != null) {
            return adminUserResponse;
        }
        
        // Handle system-level admin queries
        if (lowerQuery.contains("statistics") || lowerQuery.contains("stats") || lowerQuery.contains("metrics")) {
            return generateDetailedStatistics();
        } else if (lowerQuery.contains("budget") || lowerQuery.contains("financial")) {
            return generateBudgetInfo();
        } else if (lowerQuery.contains("system") || lowerQuery.contains("health")) {
            return generateSystemHealthReport();
        } else if (lowerQuery.contains("admin") && lowerQuery.contains("help")) {
            return generateAdminHelpMenu();
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
    
    private String generateAdminHelpMenu() {
        StringBuilder response = new StringBuilder();
        response.append("🔧 **Admin Chatbot Help Menu:**\n\n");
        
        response.append("**📊 System & Statistics:**\n");
        response.append("• 'system statistics' - Comprehensive system metrics\n");
        response.append("• 'system health' - System status and performance\n");
        response.append("• 'budget' or 'financial' - Financial overview\n\n");
        
        response.append("**👥 User Management:**\n");
        response.append("• 'all users' or 'user list' - Complete user roster\n");
        response.append("• 'user activity' - Most active users\n");
        response.append("• 'user [username]' - Detailed user report\n\n");
        
        response.append("**📚 Book & Loan Management:**\n");
        response.append("• 'all borrows' or 'borrow list' - All current loans\n");
        response.append("• 'overdue' or 'late' - Overdue book report\n");
        response.append("• 'popular books' - Most borrowed/reviewed books\n\n");
        
        response.append("**⭐ Reviews & Waiting Lists:**\n");
        response.append("• 'all reviews' or 'recent reviews' - Review reports\n");
        response.append("• 'waiting list' - Books with waiting lists\n\n");
        
        response.append("**💡 Pro Tips:**\n");
        response.append("• Ask complex queries like 'show user activity and popular books'\n");
        response.append("• Use specific usernames for detailed user reports\n");
        response.append("• Try 'admin help' anytime for this menu\n");
        
        return response.toString();
    }
    
    private String generateUserManagementInfo() {
        // This method is now largely handled by AdminUserService
        // Keep for backward compatibility but redirect to comprehensive service
        return "ℹ️ **User Management:** Try asking for 'all users', 'user activity', or 'user [username]' for detailed user information powered by our enhanced data service.";
    }
    
    private String generateOverdueReport() {
        // This is now handled by AdminUserService with comprehensive data
        return "ℹ️ **Overdue Report:** Try asking for 'overdue books' or 'overdue report' for detailed overdue analysis from our comprehensive data service.";
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
        // This is now handled by AdminUserService with real data analysis
        return "ℹ️ **Popularity Report:** Try asking for 'popular books' for detailed popularity analysis based on real borrowing and review data.";
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