package com.example.libraryhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyService {
    
    @Autowired
    private Environment environment;
    
    /**
     * Get comprehensive library policy information for AI chatbot
     */
    public Map<String, Object> getAllPolicies() {
        Map<String, Object> policies = new HashMap<>();
        
        // Borrowing policies
        Map<String, Object> borrowing = new HashMap<>();
        borrowing.put("maxBooks", environment.getProperty("borrowing.max_books_per_user", "5"));
        borrowing.put("loanPeriod", environment.getProperty("borrowing.loan_period_days", "14") + " days");
        borrowing.put("renewalPeriod", environment.getProperty("borrowing.renewal_period_days", "7") + " days");
        borrowing.put("maxRenewals", environment.getProperty("borrowing.max_renewals", "2"));
        borrowing.put("gracePeriod", environment.getProperty("borrowing.grace_period_days", "3") + " days");
        policies.put("borrowing", borrowing);
        
        // Reservation policies
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("holdPeriod", environment.getProperty("reservation.hold_period_days", "7") + " days");
        reservation.put("maxReservations", environment.getProperty("reservation.max_reservations_per_user", "3"));
        reservation.put("notificationMethods", environment.getProperty("reservation.notification_methods", "email,sms"));
        policies.put("reservation", reservation);
        
        // Fine policies
        Map<String, Object> fines = new HashMap<>();
        fines.put("overduePerDay", "$" + environment.getProperty("fines.overdue_fine_per_day", "0.50"));
        fines.put("maxFinePerBook", "$" + environment.getProperty("fines.max_fine_per_book", "10.00"));
        fines.put("lostBookFee", "$" + environment.getProperty("fines.lost_book_replacement_fee", "25.00"));
        fines.put("processingFee", "$" + environment.getProperty("fines.processing_fee", "2.00"));
        policies.put("fines", fines);
        
        // Library hours
        Map<String, String> hours = new HashMap<>();
        hours.put("monday", environment.getProperty("hours.monday", "9:00-20:00"));
        hours.put("tuesday", environment.getProperty("hours.tuesday", "9:00-20:00"));
        hours.put("wednesday", environment.getProperty("hours.wednesday", "9:00-20:00"));
        hours.put("thursday", environment.getProperty("hours.thursday", "9:00-20:00"));
        hours.put("friday", environment.getProperty("hours.friday", "9:00-18:00"));
        hours.put("saturday", environment.getProperty("hours.saturday", "10:00-16:00"));
        hours.put("sunday", environment.getProperty("hours.sunday", "12:00-17:00"));
        policies.put("hours", hours);
        
        // Contact information
        Map<String, String> contact = new HashMap<>();
        contact.put("phone", environment.getProperty("contact.main_phone", "+61-2-9999-1234"));
        contact.put("email", environment.getProperty("contact.email", "info@libraryhub.edu.au"));
        contact.put("address", environment.getProperty("contact.address", "123 University Drive, Sydney NSW 2006"));
        policies.put("contact", contact);
        
        return policies;
    }
    
    /**
     * Get admin-specific policy information
     */
    public Map<String, Object> getAdminPolicies() {
        Map<String, Object> adminPolicies = new HashMap<>();
        
        adminPolicies.put("monthlyBudget", "$" + environment.getProperty("admin.book_procurement_budget_monthly", "2000.00"));
        adminPolicies.put("staffLevels", environment.getProperty("admin.staff_access_levels", "librarian,assistant,manager"));
        adminPolicies.put("inventoryFrequency", environment.getProperty("admin.inventory_check_frequency", "monthly"));
        adminPolicies.put("dataRetention", environment.getProperty("admin.user_data_retention_years", "7") + " years");
        adminPolicies.put("backupFrequency", environment.getProperty("admin.backup_frequency", "daily"));
        adminPolicies.put("maintenanceWindow", environment.getProperty("admin.system_maintenance_window", "Sunday 2:00-4:00 AM"));
        
        return adminPolicies;
    }
    
    /**
     * Generate policy FAQ response based on question type
     */
    public String getPolicyFAQResponse(String question) {
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("borrow") || lowerQuestion.contains("loan")) {
            return getBorrowingPolicyResponse();
        } else if (lowerQuestion.contains("fine") || lowerQuestion.contains("overdue") || lowerQuestion.contains("late")) {
            return getFinesPolicyResponse();
        } else if (lowerQuestion.contains("hour") || lowerQuestion.contains("open") || lowerQuestion.contains("close")) {
            return getHoursPolicyResponse();
        } else if (lowerQuestion.contains("reserve") || lowerQuestion.contains("hold")) {
            return getReservationPolicyResponse();
        } else if (lowerQuestion.contains("contact") || lowerQuestion.contains("phone") || lowerQuestion.contains("email")) {
            return getContactResponse();
        } else if (lowerQuestion.contains("membership") || lowerQuestion.contains("register") || lowerQuestion.contains("join")) {
            return getMembershipResponse();
        } else {
            return getGeneralPolicyResponse();
        }
    }
    
    private String getBorrowingPolicyResponse() {
        Map<String, Object> policies = getAllPolicies();
        Map<String, Object> borrowing = (Map<String, Object>) policies.get("borrowing");
        
        return String.format(
            "📚 **Borrowing Policies:**\n" +
            "• Maximum books per user: %s books\n" +
            "• Loan period: %s\n" +
            "• Renewal period: %s (up to %s renewals)\n" +
            "• Grace period: %s\n\n" +
            "You can manage your loans through your account dashboard or ask our staff for assistance!",
            borrowing.get("maxBooks"),
            borrowing.get("loanPeriod"),
            borrowing.get("renewalPeriod"),
            borrowing.get("maxRenewals"),
            borrowing.get("gracePeriod")
        );
    }
    
    private String getFinesPolicyResponse() {
        Map<String, Object> policies = getAllPolicies();
        Map<String, Object> fines = (Map<String, Object>) policies.get("fines");
        
        return String.format(
            "💰 **Fine Policies:**\n" +
            "• Overdue fine: %s per day\n" +
            "• Maximum fine per book: %s\n" +
            "• Lost book replacement: %s\n" +
            "• Processing fee: %s\n\n" +
            "Pro tip: Return books on time to avoid fines! You can check due dates in your account.",
            fines.get("overduePerDay"),
            fines.get("maxFinePerBook"),
            fines.get("lostBookFee"),
            fines.get("processingFee")
        );
    }
    
    private String getHoursPolicyResponse() {
        Map<String, Object> policies = getAllPolicies();
        Map<String, String> hours = (Map<String, String>) policies.get("hours");
        
        return String.format(
            "🕒 **Library Hours:**\n" +
            "• Monday: %s\n" +
            "• Tuesday: %s\n" +
            "• Wednesday: %s\n" +
            "• Thursday: %s\n" +
            "• Friday: %s\n" +
            "• Saturday: %s\n" +
            "• Sunday: %s\n\n" +
            "Note: We're closed on public holidays. Times are in AEST.",
            hours.get("monday"), hours.get("tuesday"), hours.get("wednesday"),
            hours.get("thursday"), hours.get("friday"), hours.get("saturday"),
            hours.get("sunday")
        );
    }
    
    private String getReservationPolicyResponse() {
        Map<String, Object> policies = getAllPolicies();
        Map<String, Object> reservation = (Map<String, Object>) policies.get("reservation");
        
        return String.format(
            "📋 **Reservation Policies:**\n" +
            "• Hold period: %s\n" +
            "• Maximum reservations: %s per user\n" +
            "• Notifications: %s\n\n" +
            "We'll notify you when your reserved books are ready for pickup!",
            reservation.get("holdPeriod"),
            reservation.get("maxReservations"),
            reservation.get("notificationMethods")
        );
    }
    
    private String getContactResponse() {
        Map<String, Object> policies = getAllPolicies();
        Map<String, String> contact = (Map<String, String>) policies.get("contact");
        
        return String.format(
            "📞 **Contact Information:**\n" +
            "• Phone: %s\n" +
            "• Email: %s\n" +
            "• Address: %s\n\n" +
            "Our friendly staff are here to help!",
            contact.get("phone"),
            contact.get("email"),
            contact.get("address")
        );
    }
    
    private String getMembershipResponse() {
        return "👥 **Membership Information:**\n" +
               "• Free registration required\n" +
               "• ID verification needed\n" +
               "• Student discount: 20% off fines\n" +
               "• Senior discount: 15% off fines\n" +
               "• Family memberships available (up to 4 members)\n\n" +
               "Sign up today to start borrowing books!";
    }
    
    private String getGeneralPolicyResponse() {
        return "ℹ️ **Library Policies Available:**\n" +
               "• Borrowing rules and loan periods\n" +
               "• Fine and fee structure\n" +
               "• Library opening hours\n" +
               "• Reservation and hold policies\n" +
               "• Membership information\n" +
               "• Contact details\n\n" +
               "Ask me about any specific policy you'd like to know more about!";
    }
}