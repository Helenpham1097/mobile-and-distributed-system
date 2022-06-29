package com.example.demo.dao;

import com.example.demo.model.Authors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AuthorsDAO {
    @PersistenceContext(unitName = "BookStorePU")
    private EntityManager entityManager;

    public List<Authors> getAllAuthors() {
        return entityManager.createQuery("SELECT a FROM Authors a", Authors.class)
                .getResultList();
    }

    public Authors getAuthorByName(String authorName) {
        try {
            return (Authors) entityManager.createQuery("SELECT a from Authors a where a.authorName = :authorName")
                    .setParameter("authorName", authorName).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public void save(Authors author) {
        entityManager.persist(author);
    }

    public void update(Authors author) {
        entityManager.merge(author);
    }
}
