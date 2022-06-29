package org.distributed.edu.assignment4.dao;

import org.distributed.edu.assignment4.model.LocationMessageEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class LocationDAO {

    @PersistenceContext(unitName="LocationPU")
    private EntityManager entityManager;

    public List<LocationMessageEntity> getLatestLocations(){
        return entityManager.createQuery("SELECT location FROM LocationMessageEntity location ORDER BY location.createdAt DESC", LocationMessageEntity.class)
                .setMaxResults(20)
                .getResultList();
    }

    @Transactional
    public void save(LocationMessageEntity e) {
        entityManager.persist(e);
    }

}
