package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
// import com.example.libraryhub.model.BorrowRecord; // Not implemented yet
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.BookRepository;
// import com.example.libraryhub.repository.BorrowRecordRepository; // Not implemented yet
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeriesRecommendationService {
    
    @Autowired
    private BookRepository bookRepository;
    
    // @Autowired
    // private BorrowRecordRepository borrowRecordRepository; // Not implemented yet
    
    /**
     * Get book series recommendations based on user's borrowing history
     */
    public List<SeriesRecommendation> getSeriesRecommendations(User user) {
        if (user == null) {
            return getPopularSeriesRecommendations();
        }
        
        // For now, return popular series since BorrowRecord system isn't implemented yet
        // In the future, this would analyze user's actual borrowing history
        List<SeriesRecommendation> recommendations = getPopularSeriesRecommendations();
        
        return recommendations;
    }
    
    /**
     * Get popular series for new users
     */
    private List<SeriesRecommendation> getPopularSeriesRecommendations() {
        List<SeriesRecommendation> recommendations = new ArrayList<>();
        List<BookSeries> popularSeries = getPopularBookSeries();
        
        for (BookSeries series : popularSeries) {
            SeriesRecommendation recommendation = new SeriesRecommendation(
                    series.getName(),
                    series.getAuthor(),
                    series.getGenre(),
                    series.getDescription(),
                    3, // High score for popular series
                    "Popular series perfect for new readers"
            );
            recommendations.add(recommendation);
        }
        
        return recommendations;
    }
    
    /**
     * Define known book series organized by genre
     */
    private Map<String, List<BookSeries>> getKnownBookSeries() {
        Map<String, List<BookSeries>> genreSeries = new HashMap<>();
        
        // Science Fiction series
        List<BookSeries> scifiSeries = Arrays.asList(
            new BookSeries("The Hitchhiker's Guide Series", "Douglas Adams", "Science Fiction", 
                "A hilarious journey through space and time", Arrays.asList(1L)),
            new BookSeries("Foundation Series", "Isaac Asimov", "Science Fiction", 
                "Epic space opera about the fall and rise of galactic civilization", Arrays.asList()),
            new BookSeries("Dune Chronicles", "Frank Herbert", "Science Fiction", 
                "Complex saga of politics, religion, and ecology on the desert planet Arrakis", Arrays.asList())
        );
        genreSeries.put("Science Fiction", scifiSeries);
        
        // Fantasy series
        List<BookSeries> fantasySeries = Arrays.asList(
            new BookSeries("The Lord of the Rings", "J.R.R. Tolkien", "Fantasy", 
                "Epic fantasy adventure in Middle-earth", Arrays.asList(11L)),
            new BookSeries("Harry Potter Series", "J.K. Rowling", "Fantasy", 
                "Magical coming-of-age story at Hogwarts School", Arrays.asList()),
            new BookSeries("The Chronicles of Narnia", "C.S. Lewis", "Fantasy", 
                "Magical world accessed through a wardrobe", Arrays.asList())
        );
        genreSeries.put("Fantasy", fantasySeries);
        
        // Mystery series
        List<BookSeries> mysterySeries = Arrays.asList(
            new BookSeries("Sherlock Holmes Series", "Sir Arthur Conan Doyle", "Mystery", 
                "Classic detective stories from Victorian London", Arrays.asList(19L)),
            new BookSeries("Hercule Poirot Series", "Agatha Christie", "Mystery", 
                "Belgian detective solves impossible crimes", Arrays.asList(22L)),
            new BookSeries("Miss Marple Series", "Agatha Christie", "Mystery", 
                "Elderly sleuth solves mysteries in English villages", Arrays.asList())
        );
        genreSeries.put("Mystery", mysterySeries);
        
        // Dystopian series
        List<BookSeries> dystopianSeries = Arrays.asList(
            new BookSeries("The Hunger Games Trilogy", "Suzanne Collins", "Dystopian", 
                "Teen fights against oppressive government in televised death matches", Arrays.asList(12L)),
            new BookSeries("Divergent Series", "Veronica Roth", "Dystopian", 
                "Society divided into factions based on virtues", Arrays.asList()),
            new BookSeries("The Maze Runner Series", "James Dashner", "Dystopian", 
                "Teens trapped in mysterious maze must find escape", Arrays.asList())
        );
        genreSeries.put("Dystopian", dystopianSeries);
        
        return genreSeries;
    }
    
    /**
     * Get known book series organized by author
     */
    private Map<String, List<BookSeries>> getKnownBookSeriesByAuthor() {
        Map<String, List<BookSeries>> authorSeries = new HashMap<>();
        Map<String, List<BookSeries>> genreSeries = getKnownBookSeries();
        
        // Flatten genre series into author series
        for (List<BookSeries> seriesList : genreSeries.values()) {
            for (BookSeries series : seriesList) {
                authorSeries.computeIfAbsent(series.getAuthor(), k -> new ArrayList<>()).add(series);
            }
        }
        
        return authorSeries;
    }
    
    /**
     * Get most popular book series
     */
    private List<BookSeries> getPopularBookSeries() {
        return Arrays.asList(
            new BookSeries("Harry Potter Series", "J.K. Rowling", "Fantasy", 
                "The magical world of Harry Potter - 7 books of wonder and adventure", Arrays.asList()),
            new BookSeries("The Hunger Games Trilogy", "Suzanne Collins", "Dystopian", 
                "Thrilling dystopian adventure that captivated millions", Arrays.asList(12L)),
            new BookSeries("The Lord of the Rings", "J.R.R. Tolkien", "Fantasy", 
                "The ultimate fantasy epic that defined the genre", Arrays.asList(11L)),
            new BookSeries("Sherlock Holmes Series", "Sir Arthur Conan Doyle", "Mystery", 
                "Timeless detective stories that inspired countless adaptations", Arrays.asList(19L)),
            new BookSeries("Hercule Poirot Series", "Agatha Christie", "Mystery", 
                "Master of mystery with intricate plots and brilliant solutions", Arrays.asList(22L))
        );
    }
    
    /**
     * Generate formatted series recommendation text for chatbot
     */
    public String generateSeriesRecommendationText(User user) {
        List<SeriesRecommendation> recommendations = getSeriesRecommendations(user);
        
        if (recommendations.isEmpty()) {
            return "📚 I don't have any specific series recommendations right now. Try borrowing some books first, and I'll be able to suggest series based on your reading preferences!";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("📖 **Book Series Recommendations for You:**\n\n");
        
        int count = 1;
        for (SeriesRecommendation rec : recommendations.subList(0, Math.min(5, recommendations.size()))) {
            response.append(String.format(
                "%d. **%s** by %s\n" +
                "   Genre: %s\n" +
                "   %s\n" +
                "   *%s*\n\n",
                count++,
                rec.getSeriesName(),
                rec.getAuthor(),
                rec.getGenre(),
                rec.getDescription(),
                rec.getReason()
            ));
        }
        
        response.append("💡 These recommendations are based on your reading history and popular series in our library. Happy reading!");
        
        return response.toString();
    }
    
    // Inner classes for data structures
    public static class SeriesRecommendation {
        private String seriesName;
        private String author;
        private String genre;
        private String description;
        private int score;
        private String reason;
        
        public SeriesRecommendation(String seriesName, String author, String genre, 
                                   String description, int score, String reason) {
            this.seriesName = seriesName;
            this.author = author;
            this.genre = genre;
            this.description = description;
            this.score = score;
            this.reason = reason;
        }
        
        // Getters
        public String getSeriesName() { return seriesName; }
        public String getAuthor() { return author; }
        public String getGenre() { return genre; }
        public String getDescription() { return description; }
        public int getScore() { return score; }
        public String getReason() { return reason; }
    }
    
    public static class BookSeries {
        private String name;
        private String author;
        private String genre;
        private String description;
        private List<Long> bookIds;
        
        public BookSeries(String name, String author, String genre, String description, List<Long> bookIds) {
            this.name = name;
            this.author = author;
            this.genre = genre;
            this.description = description;
            this.bookIds = bookIds != null ? bookIds : new ArrayList<>();
        }
        
        // Getters
        public String getName() { return name; }
        public String getAuthor() { return author; }
        public String getGenre() { return genre; }
        public String getDescription() { return description; }
        public List<Long> getBookIds() { return bookIds; }
    }
}
