package servletListener;

import dao.CustomerDao;
import dao.OrderDao;
import service.OrderService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class MyContextListener implements ServletContextListener {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("CustomerOrdersManagement");

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        try{
            CustomerDao customerDao = new CustomerDao(emf);
            OrderDao orderDao = new OrderDao(emf);
            OrderService orderService = new OrderService(customerDao,orderDao);
            context.setAttribute("customerDao", customerDao);
            context.setAttribute("orderDao", orderDao);
            context.setAttribute("orderService", orderService);
        } catch (Exception e){
            System.out.println("Could not create database DAO "+ e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.printf("Deregistering jdbc driver: %s%n", driver);
            } catch (SQLException e) {
                System.out.printf("Error Deregistering driver %s%n", driver);
                System.out.println(e);
            }
        }
        System.out.println("Assignment1 context destroyed");

    }
}
