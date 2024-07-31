package pl.codeleak.demos.sbt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "Bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private int billId;
    @Column(name="phone")
    private String phone;
    @Column(name="address")
    private String address;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "number_of_guest")
    private int numberOfGuest;
    @Column(name = "total_cost")
    private float totalCost;
    @Column(name = "table_id")
    private int tableId;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "status")
    private int status;
    @Column(name = "type")
    private int type;

    public Bill() {
    }

    public Bill(String phone, String address, Date createdTime, int numberOfGuest, float totalCost, int tableId, int userId, int status, int type) {
        this.phone = phone;
        this.address = address;
        this.createdTime = createdTime;
        this.numberOfGuest = numberOfGuest;
        this.totalCost = totalCost;
        this.tableId = tableId;
        this.userId = userId;
        this.status = status;
        this.type = type;
    }

    public Bill(int billId) {
        this.billId = billId;
    }
}
