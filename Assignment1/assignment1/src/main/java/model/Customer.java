package model;



import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "trang_customers")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone")
    private String customerPhone;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer", orphanRemoval = true)
    private List<Orders> orders = new ArrayList<>();

    public void addOrder(Orders order) {
        this.orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Orders order) {
        this.orders.remove(order);
        order.setCustomer(null);
    }

    public void removeOrders() {
        Iterator<Orders> iterator = this.orders.iterator();
        while (iterator.hasNext()) {
            Orders order = iterator.next();
            order.setCustomer(null);
            iterator.remove();
        }
    }

    public int getId() {
        return id;
    }

    public Customer setId(int id) {
        this.id = id;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Customer setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Customer setGender(Gender gender) {
        this.gender = gender;
        return this;
    }


    public String getCustomerPhone() {
        return customerPhone;
    }

    public Customer setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        return this;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public Customer setOrders(List<Orders> orders) {
        this.orders = orders;
        return this;
    }
}