package com.example.libraryhub.config;

import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Book;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(BookService bookService, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            seedBooks(bookService);
            upsertAdminUser(userRepository, passwordEncoder);
        };
    }

    private void seedBooks(BookService bookService) {
        if (bookService.getAllBooks().isEmpty()) {
            List.of(
                    new Book("To Kill a Mockingbird", "Harper Lee", "Fiction"),
                    new Book("1984", "George Orwell", "Dystopian"),
                    new Book("The Great Gatsby", "F. Scott Fitzgerald", "Classic"),
                    new Book("Pride and Prejudice", "Jane Austen", "Romance"),
                    new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy"),
                    new Book("Moby Dick", "Herman Melville", "Adventure"),
                    new Book("The Catcher in the Rye", "J.D. Salinger", "Fiction"),
                    new Book("War and Peace", "Leo Tolstoy", "Historical"),
                    new Book("The Odyssey", "Homer", "Epic"),
                    new Book("Brave New World", "Aldous Huxley", "Dystopian"),
                    new Book("The Lord of the Rings", "J.R.R. Tolkien", "Fantasy"),
                    new Book("Harry Potter and the Sorcerer’s Stone", "J.K. Rowling", "Fantasy"),
                    new Book("Crime and Punishment", "Fyodor Dostoevsky", "Psychological"),
                    new Book("Jane Eyre", "Charlotte Brontë", "Gothic"),
                    new Book("Wuthering Heights", "Emily Brontë", "Romance"),
                    new Book("The Divine Comedy", "Dante Alighieri", "Poetry"),
                    new Book("Frankenstein", "Mary Shelley", "Horror"),
                    new Book("The Alchemist", "Paulo Coelho", "Philosophical"),
                    new Book("Don Quixote", "Miguel de Cervantes", "Classic"),
                    new Book("The Brothers Karamazov", "Fyodor Dostoevsky", "Philosophical"),
                    new Book("Dracula", "Bram Stoker", "Horror"),
                    new Book("Les Misérables", "Victor Hugo", "Historical"),
                    new Book("Anna Karenina", "Leo Tolstoy", "Romance"),
                    new Book("Fahrenheit 451", "Ray Bradbury", "Dystopian"),
                    new Book("The Kite Runner", "Khaled Hosseini", "Drama")).forEach(bookService::saveBook);
        }
    }

    private void upsertAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> new Admin(null, "admin", "admin@library.com", null));
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        userRepository.save(admin);
    }
}
