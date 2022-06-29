package model;



import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "trang_orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_reference")
    private String orderReference;

    @Column(name = "total_bill")
    private double totalBill;

    @Column(name = "date_order")
    private Date dateOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public String getOrderReference() {
        return orderReference;
    }

    public Orders setOrderReference(String orderReference) {
        this.orderReference = orderReference;
        return this;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public Orders setTotalBill(double totalBill) {
        this.totalBill = totalBill;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Orders setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public Date getDateOrder() {
        return dateOrder;
    }

    public Orders setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
        return this;
    }
}
