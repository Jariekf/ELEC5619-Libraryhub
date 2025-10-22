package com.example.libraryhub.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ChatbotIntent enum
 */
class ChatbotIntentTest {

    @Test
    void testAllEnumValues() {
        ChatbotIntent[] intents = ChatbotIntent.values();
        assertEquals(5, intents.length);
        assertTrue(java.util.Arrays.asList(intents).contains(ChatbotIntent.FAQ));
        assertTrue(java.util.Arrays.asList(intents).contains(ChatbotIntent.SERIES_RECOMMENDATION));
        assertTrue(java.util.Arrays.asList(intents).contains(ChatbotIntent.BOOK_SEARCH));
        assertTrue(java.util.Arrays.asList(intents).contains(ChatbotIntent.GENERAL_CONVERSATION));
        assertTrue(java.util.Arrays.asList(intents).contains(ChatbotIntent.UNKNOWN));
    }

    @Test
    void testValueOf() {
        assertEquals(ChatbotIntent.FAQ, ChatbotIntent.valueOf("FAQ"));
        assertEquals(ChatbotIntent.SERIES_RECOMMENDATION, ChatbotIntent.valueOf("SERIES_RECOMMENDATION"));
        assertEquals(ChatbotIntent.BOOK_SEARCH, ChatbotIntent.valueOf("BOOK_SEARCH"));
        assertEquals(ChatbotIntent.GENERAL_CONVERSATION, ChatbotIntent.valueOf("GENERAL_CONVERSATION"));
        assertEquals(ChatbotIntent.UNKNOWN, ChatbotIntent.valueOf("UNKNOWN"));
    }

    @Test
    void testInvalidValueOfThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ChatbotIntent.valueOf("INVALID"));
    }

    @Test
    void testEnumEquality() {
        assertEquals(ChatbotIntent.FAQ, ChatbotIntent.FAQ);
        assertNotEquals(ChatbotIntent.FAQ, ChatbotIntent.UNKNOWN);
    }

    @Test
    void testEnumToString() {
        assertEquals("FAQ", ChatbotIntent.FAQ.toString());
        assertEquals("SERIES_RECOMMENDATION", ChatbotIntent.SERIES_RECOMMENDATION.toString());
        assertEquals("BOOK_SEARCH", ChatbotIntent.BOOK_SEARCH.toString());
        assertEquals("GENERAL_CONVERSATION", ChatbotIntent.GENERAL_CONVERSATION.toString());
        assertEquals("UNKNOWN", ChatbotIntent.UNKNOWN.toString());
    }

    @Test
    void testEnumName() {
        assertEquals("FAQ", ChatbotIntent.FAQ.name());
        assertEquals("SERIES_RECOMMENDATION", ChatbotIntent.SERIES_RECOMMENDATION.name());
        assertEquals("BOOK_SEARCH", ChatbotIntent.BOOK_SEARCH.name());
        assertEquals("GENERAL_CONVERSATION", ChatbotIntent.GENERAL_CONVERSATION.name());
        assertEquals("UNKNOWN", ChatbotIntent.UNKNOWN.name());
    }
}
