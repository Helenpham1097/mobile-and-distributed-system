package com.example.demo.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "helen_books")
public class Books {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String bookTitle;

    @Column(name = "published_year")
    private String publishedYear;

    @Column(name = "price")
    private double price;

    @Column(name = "image")
    private String image;

    @ManyToMany(mappedBy = "books",cascade = CascadeType.ALL)
    private Set<Authors> authors = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Books setBookTitle(String title) {
        this.bookTitle = title;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Books setImage(String image) {
        this.image = image;
        return this;
    }

    public String getPublishedYear() {
        return publishedYear;
    }

    public Books setPublishedYear(String publishedYear) {
        this.publishedYear = publishedYear;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Books setPrice(double price) {
        this.price = price;
        return this;
    }

    public Set<Authors> getAuthors() {
        return authors;
    }

    public Books setAuthors(Set<Authors> authors) {
        this.authors = authors;
        return this;
    }
}
