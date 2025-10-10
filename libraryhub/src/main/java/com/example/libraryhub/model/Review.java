package com.example.libraryhub.model;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String comment;
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    // Constructors
    public Review() {}
    public Review(String username, String comment, Integer rating, Book book) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.book = book;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}
