package dao;

import model.Customer;
import utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

public class CustomerDao {

    private final EntityManager entityManager;
    private final EntityManagerFactory entityManagerFactory;

    public CustomerDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        entityManager = entityManagerFactory.createEntityManager();
    }

    public List<Customer> getAllCustomers() {
        return entityManagerFactory.createEntityManager().createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();
    }

    public void save(Customer customer) {
        JPAUtils.execute(
                    entityManager,
                    entityManager::persist,
                    customer);
    }

    public void update(Customer customer) {
        JPAUtils.execute(
                entityManager,
                entityManager::merge,
                customer);
    }

    public Customer getCustomerByPhone(String phoneNumber) {
        try {
            return (Customer) entityManagerFactory.createEntityManager().createQuery("SELECT c from Customer c where c.customerPhone = :phoneNumber")
                    .setParameter("phoneNumber", phoneNumber).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
