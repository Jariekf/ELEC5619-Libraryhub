package com.example.libraryhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PolicyServiceTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        // Setup default environment properties
        when(environment.getProperty("borrowing.max_books_per_user", "5")).thenReturn("5");
        when(environment.getProperty("borrowing.loan_period_days", "14")).thenReturn("14");
        when(environment.getProperty("borrowing.renewal_period_days", "7")).thenReturn("7");
        when(environment.getProperty("borrowing.max_renewals", "2")).thenReturn("2");
        when(environment.getProperty("borrowing.grace_period_days", "3")).thenReturn("3");
        
        when(environment.getProperty("reservation.hold_period_days", "7")).thenReturn("7");
        when(environment.getProperty("reservation.max_reservations_per_user", "3")).thenReturn("3");
        when(environment.getProperty("reservation.notification_methods", "email,sms")).thenReturn("email,sms");
        
        when(environment.getProperty("fines.overdue_fine_per_day", "0.50")).thenReturn("0.50");
        when(environment.getProperty("fines.max_fine_per_book", "10.00")).thenReturn("10.00");
        when(environment.getProperty("fines.lost_book_replacement_fee", "25.00")).thenReturn("25.00");
        when(environment.getProperty("fines.processing_fee", "2.00")).thenReturn("2.00");
        
        when(environment.getProperty("hours.monday", "9:00-20:00")).thenReturn("9:00-20:00");
        when(environment.getProperty("hours.tuesday", "9:00-20:00")).thenReturn("9:00-20:00");
        when(environment.getProperty("hours.wednesday", "9:00-20:00")).thenReturn("9:00-20:00");
        when(environment.getProperty("hours.thursday", "9:00-20:00")).thenReturn("9:00-20:00");
        when(environment.getProperty("hours.friday", "9:00-18:00")).thenReturn("9:00-18:00");
        when(environment.getProperty("hours.saturday", "10:00-16:00")).thenReturn("10:00-16:00");
        when(environment.getProperty("hours.sunday", "12:00-17:00")).thenReturn("12:00-17:00");
        
        when(environment.getProperty("contact.main_phone", "+61-2-9999-1234")).thenReturn("+61-2-9999-1234");
        when(environment.getProperty("contact.email", "info@libraryhub.edu.au")).thenReturn("info@libraryhub.edu.au");
        when(environment.getProperty("contact.address", "123 University Drive, Sydney NSW 2006")).thenReturn("123 University Drive, Sydney NSW 2006");
        
        when(environment.getProperty("admin.book_procurement_budget_monthly", "2000.00")).thenReturn("2000.00");
        when(environment.getProperty("admin.staff_access_levels", "librarian,assistant,manager")).thenReturn("librarian,assistant,manager");
        when(environment.getProperty("admin.inventory_check_frequency", "monthly")).thenReturn("monthly");
        when(environment.getProperty("admin.user_data_retention_years", "7")).thenReturn("7");
        when(environment.getProperty("admin.backup_frequency", "daily")).thenReturn("daily");
        when(environment.getProperty("admin.system_maintenance_window", "Sunday 2:00-4:00 AM")).thenReturn("Sunday 2:00-4:00 AM");
    }

    @Test
    void testGetAllPolicies() {
        Map<String, Object> policies = policyService.getAllPolicies();
        
        assertNotNull(policies);
        assertTrue(policies.containsKey("borrowing"));
        assertTrue(policies.containsKey("reservation"));
        assertTrue(policies.containsKey("fines"));
        assertTrue(policies.containsKey("hours"));
        assertTrue(policies.containsKey("contact"));
    }

    @Test
    void testGetAllPoliciesBorrowingSection() {
        Map<String, Object> policies = policyService.getAllPolicies();
        @SuppressWarnings("unchecked")
        Map<String, Object> borrowing = (Map<String, Object>) policies.get("borrowing");
        
        assertEquals("5", borrowing.get("maxBooks"));
        assertEquals("14 days", borrowing.get("loanPeriod"));
        assertEquals("7 days", borrowing.get("renewalPeriod"));
        assertEquals("2", borrowing.get("maxRenewals"));
        assertEquals("3 days", borrowing.get("gracePeriod"));
    }

    @Test
    void testGetAllPoliciesFinesSection() {
        Map<String, Object> policies = policyService.getAllPolicies();
        @SuppressWarnings("unchecked")
        Map<String, Object> fines = (Map<String, Object>) policies.get("fines");
        
        assertEquals("$0.50", fines.get("overduePerDay"));
        assertEquals("$10.00", fines.get("maxFinePerBook"));
        assertEquals("$25.00", fines.get("lostBookFee"));
        assertEquals("$2.00", fines.get("processingFee"));
    }

    @Test
    void testGetAdminPolicies() {
        Map<String, Object> adminPolicies = policyService.getAdminPolicies();
        
        assertNotNull(adminPolicies);
        assertEquals("$2000.00", adminPolicies.get("monthlyBudget"));
        assertEquals("librarian,assistant,manager", adminPolicies.get("staffLevels"));
        assertEquals("monthly", adminPolicies.get("inventoryFrequency"));
        assertEquals("7 years", adminPolicies.get("dataRetention"));
        assertEquals("daily", adminPolicies.get("backupFrequency"));
    }

    @Test
    void testGetPolicyFAQResponseBorrowing() {
        String response = policyService.getPolicyFAQResponse("How do I borrow books?");
        
        assertNotNull(response);
        assertTrue(response.contains("Borrowing Policies"));
        assertTrue(response.contains("5 books"));
    }

    @Test
    void testGetPolicyFAQResponseFines() {
        String response = policyService.getPolicyFAQResponse("What are the fines for overdue books?");
        
        assertNotNull(response);
        assertTrue(response.contains("Fine Policies"));
        assertTrue(response.contains("$0.50"));
    }

    @Test
    void testGetPolicyFAQResponseHours() {
        String response = policyService.getPolicyFAQResponse("What are your opening hours?");
        
        assertNotNull(response);
        assertTrue(response.contains("Library Hours"));
        assertTrue(response.contains("Monday"));
    }

    @Test
    void testGetPolicyFAQResponseReservation() {
        String response = policyService.getPolicyFAQResponse("How do I reserve a book?");
        
        assertNotNull(response);
        assertTrue(response.contains("Reservation Policies"));
        assertTrue(response.contains("7 days"));
    }

    @Test
    void testGetPolicyFAQResponseContact() {
        String response = policyService.getPolicyFAQResponse("What is your phone number?");
        
        assertNotNull(response);
        assertTrue(response.contains("Contact Information"));
        assertTrue(response.contains("+61-2-9999-1234"));
    }

    @Test
    void testGetPolicyFAQResponseMembership() {
        String response = policyService.getPolicyFAQResponse("How do I register for membership?");
        
        assertNotNull(response);
        assertTrue(response.contains("Membership Information"));
        assertTrue(response.contains("Free registration"));
    }

    @Test
    void testGetPolicyFAQResponseGeneral() {
        String response = policyService.getPolicyFAQResponse("Tell me about policies");
        
        assertNotNull(response);
        assertTrue(response.contains("Library Policies Available"));
    }
}
