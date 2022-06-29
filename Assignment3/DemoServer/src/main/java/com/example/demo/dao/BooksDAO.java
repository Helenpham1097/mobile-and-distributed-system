package com.example.demo.dao;

import com.example.demo.model.Books;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class BooksDAO {
    @PersistenceContext(unitName="BookStorePU")
    private EntityManager entityManager;

    public List<Books> getAllBooks(){
        return entityManager.createQuery("SELECT b FROM Books b", Books.class)
                .getResultList();
    }

    public Books getBookByTitle(String title) {
        return (Books) entityManager.createQuery("SELECT b from Books b where b.bookTitle = :title")
                .setParameter("title", title)
                .getSingleResult();
    }

    @Transactional
    public void save(Books book) {
        entityManager.persist(book);
    }

    public void delete(Books book) {
        entityManager.remove(book);
    }
}
