package com.example.libraryhub.service;

import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeriesRecommendationServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private SeriesRecommendationService seriesRecommendationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new Customer(1L, "testuser", "test@test.com", "hash");
    }

    @Test
    void testGetSeriesRecommendationsWithUser() {
        List<SeriesRecommendationService.SeriesRecommendation> recommendations = 
            seriesRecommendationService.getSeriesRecommendations(testUser);

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        assertTrue(recommendations.size() <= 5);
    }

    @Test
    void testGetSeriesRecommendationsWithoutUser() {
        List<SeriesRecommendationService.SeriesRecommendation> recommendations = 
            seriesRecommendationService.getSeriesRecommendations(null);

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
    }

    @Test
    void testGenerateSeriesRecommendationText() {
        String text = seriesRecommendationService.generateSeriesRecommendationText(testUser);

        assertNotNull(text);
        assertTrue(text.contains("Book Series Recommendations"));
        assertTrue(text.contains("Harry Potter") || text.contains("Hunger Games") || text.contains("Lord of the Rings"));
    }

    @Test
    void testGenerateSeriesRecommendationTextWithoutUser() {
        String text = seriesRecommendationService.generateSeriesRecommendationText(null);

        assertNotNull(text);
        assertTrue(text.contains("Book Series Recommendations"));
    }

    @Test
    void testSeriesRecommendationStructure() {
        List<SeriesRecommendationService.SeriesRecommendation> recommendations = 
            seriesRecommendationService.getSeriesRecommendations(testUser);

        for (SeriesRecommendationService.SeriesRecommendation rec : recommendations) {
            assertNotNull(rec.getSeriesName());
            assertNotNull(rec.getAuthor());
            assertNotNull(rec.getGenre());
            assertNotNull(rec.getDescription());
            assertNotNull(rec.getReason());
            assertTrue(rec.getScore() > 0);
        }
    }

    @Test
    void testSeriesRecommendationLimit() {
        String text = seriesRecommendationService.generateSeriesRecommendationText(testUser);
        
        // Should not recommend more than 5 series
        int seriesCount = text.split("\\d+\\.\\s\\*\\*").length - 1;
        assertTrue(seriesCount <= 5);
    }

    @Test
    void testGetKnownBookSeriesReflection() throws Exception {
        // Use reflection to test private method getKnownBookSeries
        Method getKnownBookSeriesMethod = SeriesRecommendationService.class.getDeclaredMethod("getKnownBookSeries");
        getKnownBookSeriesMethod.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        Map<String, List<?>> genreSeries = (Map<String, List<?>>) getKnownBookSeriesMethod.invoke(seriesRecommendationService);
        
        assertNotNull(genreSeries);
        assertTrue(genreSeries.containsKey("Science Fiction"));
        assertTrue(genreSeries.containsKey("Fantasy"));
        assertTrue(genreSeries.containsKey("Mystery"));
        assertTrue(genreSeries.containsKey("Dystopian"));
        
        // Verify each genre has series
        assertTrue(genreSeries.get("Science Fiction").size() >= 3);
        assertTrue(genreSeries.get("Fantasy").size() >= 3);
        assertTrue(genreSeries.get("Mystery").size() >= 3);
        assertTrue(genreSeries.get("Dystopian").size() >= 3);
    }

    @Test
    void testGetKnownBookSeriesByAuthorReflection() throws Exception {
        // Use reflection to test private method getKnownBookSeriesByAuthor
        Method getKnownBookSeriesByAuthorMethod = SeriesRecommendationService.class.getDeclaredMethod("getKnownBookSeriesByAuthor");
        getKnownBookSeriesByAuthorMethod.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        Map<String, List<?>> authorSeries = (Map<String, List<?>>) getKnownBookSeriesByAuthorMethod.invoke(seriesRecommendationService);
        
        assertNotNull(authorSeries);
        assertTrue(authorSeries.containsKey("Douglas Adams"));
        assertTrue(authorSeries.containsKey("J.R.R. Tolkien"));
        assertTrue(authorSeries.containsKey("Agatha Christie"));
        assertTrue(authorSeries.containsKey("Suzanne Collins"));
        
        // Agatha Christie has multiple series
        assertTrue(authorSeries.get("Agatha Christie").size() >= 2);
    }

    @Test
    void testGetPopularBookSeriesReflection() throws Exception {
        // Use reflection to test private method getPopularBookSeries
        Method getPopularBookSeriesMethod = SeriesRecommendationService.class.getDeclaredMethod("getPopularBookSeries");
        getPopularBookSeriesMethod.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        List<?> popularSeries = (List<?>) getPopularBookSeriesMethod.invoke(seriesRecommendationService);
        
        assertNotNull(popularSeries);
        assertFalse(popularSeries.isEmpty());
    }
}
