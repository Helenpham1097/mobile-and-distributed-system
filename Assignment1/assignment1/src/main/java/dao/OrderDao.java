package dao;

import model.Orders;
import servlet.Result;
import utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class OrderDao {

    private EntityManager entityManager;

    public OrderDao(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public Orders getOrderByOrderReference(String orderReference) {
        return (Orders) entityManager.createQuery("select o from Orders o where o.orderReference =: orderReference")
                .setParameter("orderReference", orderReference)
                .getSingleResult();
    }

    public List<Result> getAllOrdersFromADate(Date date) {
        return entityManager.createQuery(
                        "select o.orderReference, c.customerName,c.customerPhone, o.totalBill, o.dateOrder from Orders o JOIN o.customer c where o.dateOrder = :date",
                        Tuple.class)
                .setParameter("date", date)
                .getResultList()
                .stream()
                .map(tuple -> new Result((String)tuple.get(0), (String) tuple.get(1),(String)tuple.get(2),(Double) tuple.get(3),(Date) tuple.get(4)))
                .collect(Collectors.toList());
    }
    public void save(Orders order) {
        JPAUtils.execute(
                entityManager,
                entityManager::persist,
                order);
    }

    public void delete(Orders order) {
        JPAUtils.execute(
                entityManager,
                entityManager::remove,
                order);
    }
}
