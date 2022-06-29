package com.example.demo.service;

import com.example.demo.dto.AuthorsDTO;
import com.example.demo.dto.BooksDTO;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ServiceInterface {
    void createNewBookAndAuthor(String title, String year, double price, String image, List<AuthorsDTO> authors);
    List<BooksDTO> getAllBooks();
    List<BooksDTO> findBooksByAuthor(String authorName);
    List<AuthorsDTO> findAuthorsOfBooks(String title);
    void updateBook(String title, String publishedYear, double price, String image, List<AuthorsDTO> authors);
    void deleteBook(String title);
}
