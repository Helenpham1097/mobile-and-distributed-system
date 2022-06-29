package com.example.demo.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Table(name = "helen_authors")
public class Authors {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "author_name")
    private String authorName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "helen_authors_books",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Books> books = new HashSet<>();

    public void addBook(Books book) {
        this.books.add(book);
        book.getAuthors().add(this);
    }

    public void addBooks(Set<Books> books){
        this.books.addAll(books);
        books.forEach(book -> book.getAuthors().add(this));
    }

    public void removeBook(Books book) {
        this.books.remove(book);
        book.getAuthors().remove(this);
    }

    public void removeBooks() {
        Iterator<Books> iterator = this.books.iterator();
        while (iterator.hasNext()) {
            Books book = iterator.next();
            book.getAuthors().remove(this);
            iterator.remove();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Authors setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public Set<Books> getBooks() {
        return books;
    }

    public Authors setBooks(Set<Books> books) {
        this.books = books;
        return this;
    }
}
