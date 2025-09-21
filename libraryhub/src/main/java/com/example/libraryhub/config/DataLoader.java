package com.example.libraryhub.config;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(BookService bookService) {
        return args -> {
            if (bookService.getAllBooks().isEmpty()) {
                bookService.saveBook(new Book("To Kill a Mockingbird", "Harper Lee", "Fiction"));
                bookService.saveBook(new Book("1984", "George Orwell", "Dystopian"));
                bookService.saveBook(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Classic"));
                bookService.saveBook(new Book("Pride and Prejudice", "Jane Austen", "Romance"));
                bookService.saveBook(new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy"));
                bookService.saveBook(new Book("Moby Dick", "Herman Melville", "Adventure"));
                bookService.saveBook(new Book("The Catcher in the Rye", "J.D. Salinger", "Fiction"));
                bookService.saveBook(new Book("War and Peace", "Leo Tolstoy", "Historical"));
                bookService.saveBook(new Book("The Odyssey", "Homer", "Epic"));
                bookService.saveBook(new Book("Brave New World", "Aldous Huxley", "Dystopian"));
                bookService.saveBook(new Book("The Lord of the Rings", "J.R.R. Tolkien", "Fantasy"));
                bookService.saveBook(new Book("Harry Potter and the Sorcerer’s Stone", "J.K. Rowling", "Fantasy"));
                bookService.saveBook(new Book("Crime and Punishment", "Fyodor Dostoevsky", "Psychological"));
                bookService.saveBook(new Book("Jane Eyre", "Charlotte Brontë", "Gothic"));
                bookService.saveBook(new Book("Wuthering Heights", "Emily Brontë", "Romance"));
                bookService.saveBook(new Book("The Divine Comedy", "Dante Alighieri", "Poetry"));
                bookService.saveBook(new Book("Frankenstein", "Mary Shelley", "Horror"));
                bookService.saveBook(new Book("The Alchemist", "Paulo Coelho", "Philosophical"));
                bookService.saveBook(new Book("Don Quixote", "Miguel de Cervantes", "Classic"));
                bookService.saveBook(new Book("The Brothers Karamazov", "Fyodor Dostoevsky", "Philosophical"));
                bookService.saveBook(new Book("Dracula", "Bram Stoker", "Horror"));
                bookService.saveBook(new Book("Les Misérables", "Victor Hugo", "Historical"));
                bookService.saveBook(new Book("Anna Karenina", "Leo Tolstoy", "Romance"));
                bookService.saveBook(new Book("Fahrenheit 451", "Ray Bradbury", "Dystopian"));
                bookService.saveBook(new Book("The Kite Runner", "Khaled Hosseini", "Drama"));
            }
        };
    }
}
