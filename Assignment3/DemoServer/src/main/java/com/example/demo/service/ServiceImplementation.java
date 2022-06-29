package com.example.demo.service;

import com.example.demo.dao.AuthorsDAO;
import com.example.demo.dao.BooksDAO;
import com.example.demo.dto.AuthorsDTO;
import com.example.demo.dto.BooksDTO;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.model.Authors;
import com.example.demo.model.Books;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class ServiceImplementation implements ServiceInterface {

    private AuthorsDAO authorsDAO;
    private BooksDAO booksDAO;

    @Inject
    public ServiceImplementation(AuthorsDAO authorsDAO, BooksDAO booksDAO) {
        this.authorsDAO = authorsDAO;
        this.booksDAO = booksDAO;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createNewBookAndAuthor(String title, String year, double price, String image, List<AuthorsDTO> authorsDTO) {
        Books book = new Books();
        Set<Authors> authors = authorsDTO.stream()
                .map(author -> new Authors()
                        .setAuthorName(author.getAuthorName()))
                .collect(Collectors.toSet());

        book.setBookTitle(title)
                .setPublishedYear(year)
                .setPrice(price)
                .setImage(image);

        for (Authors author : authors) {
            author.addBook(book);
        }
        booksDAO.save(book);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BooksDTO> getAllBooks() {
        return booksDAO
                .getAllBooks()
                .stream()
                .map(book -> new BooksDTO(
                        book.getBookTitle(),
                        book.getPublishedYear(),
                        book.getPrice(),
                        book.getImage(),
                        book.getAuthors().stream().map(author -> new AuthorsDTO(author.getAuthorName())).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BooksDTO> findBooksByAuthor(String authorName) {
        Authors author = authorsDAO.getAuthorByName(authorName);
        Set<Books> books = author.getBooks();
        return books
                .stream()
                .map(book -> new BooksDTO(book.getBookTitle(), book.getPublishedYear(), book.getPrice(), book.getImage()))
                .collect(Collectors.toList());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<AuthorsDTO> findAuthorsOfBooks(String title) {
        Books book = booksDAO.getBookByTitle(title);
        if (book == null) {
            throw new BookNotFoundException("The book is not available");
        }

        Set<Authors> authors = book.getAuthors();
        return authors
                .stream()
                .map(author -> new AuthorsDTO(author.getAuthorName()))
                .collect(Collectors.toList());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateBook(String title, String publishedYear, double price, String image, List<AuthorsDTO> authorsDTO) {
        Books foundBook = booksDAO.getBookByTitle(title);

        if (foundBook == null) {
            createNewBookAndAuthor(title, publishedYear, price, image, authorsDTO);

        } else {
            Set<Authors> authorsOfBook = foundBook.getAuthors();
            for (Authors author : authorsOfBook) {
                author.removeBook(foundBook);
            }
            Set<Authors> authors = authorsDTO.stream()
                    .map(author -> new Authors()
                            .setAuthorName(author.getAuthorName()))
                    .collect(Collectors.toSet());

            foundBook.setPrice(price)
                    .setPublishedYear(publishedYear)
                    .setImage(image)
                    .setAuthors(authors.stream().collect(Collectors.toSet()));

            for (Authors author : authors) {
                author.addBook(foundBook);
            }

            booksDAO.save(foundBook);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteBook(String title) {
        Books book = booksDAO.getBookByTitle(title);
        if (book == null) {
            throw new BookNotFoundException("This book is not available");
        }
        Set<Authors> authors = book.getAuthors();
        for (Authors author : authors) {
            author.removeBook(book);
        }
        booksDAO.delete(book);
    }
}
