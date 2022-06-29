package service;

import dao.CustomerDao;
import dao.OrderDao;
import exception.OrderNotFoundException;
import model.Customer;
import model.Gender;
import model.Orders;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;


public class OrderService {

    private CustomerDao customerDao;
    private OrderDao orderDao;

    public OrderService(CustomerDao customerDao, OrderDao orderDao) {
        this.customerDao = customerDao;
        this.orderDao = orderDao;
    }

    public String addNewOrder(String customerName,
                            String customerPhone,
                            Gender gender,
                            double totalBill,
                            Date dateOrder) {
        Customer customer = customerDao.getCustomerByPhone(customerPhone);
        String orderReference = RandomStringUtils.randomAlphabetic(10);
        Orders order = new Orders();
        order.setOrderReference(orderReference)
                .setTotalBill(totalBill)
                .setDateOrder(dateOrder);

        if (customer == null) {
            customer = new Customer();
            customer.setCustomerName(customerName)
                    .setCustomerPhone(customerPhone)
                    .setGender(gender)
                    .addOrder(order);
            customerDao.save(customer);
        } else {
            customer.addOrder(order);
            customerDao.update(customer);
        }

        return orderReference;
    }

    public void upDateOrderByOrderReference(String orderReference, double totalBill, Date dateOrder) throws OrderNotFoundException {
        Orders order = orderDao.getOrderByOrderReference(orderReference);
        if (order == null) {
            throw new OrderNotFoundException("This order reference is not available");
        }
        order.setDateOrder(dateOrder)
                .setTotalBill(totalBill);
        orderDao.save(order);
    }

    public void deleteOrderByReference(String orderReference) throws OrderNotFoundException {
        Orders order = orderDao.getOrderByOrderReference(orderReference);
        if (order == null) {
            throw new OrderNotFoundException("This order reference is not available");
        }
        orderDao.delete(order);
    }
}

