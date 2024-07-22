package pl.codeleak.demos.sbt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "number_of_guest")
    private int numberOfGuest;
    @Column(name = "total_cost")
    private float totalCost;
    @Column(name = "table_id")
    private int tableId;
    @Column(name = "user_id")
    private int customerId;

    public Bill() {
    }

    public Bill(Date createdTime, int numberOfGuest, float totalCost, int tableId, int customerId) {
        this.createdTime = createdTime;
        this.numberOfGuest = numberOfGuest;
        this.totalCost = totalCost;
        this.tableId = tableId;
        this.customerId = customerId;
    }

    public Bill(int billId) {
        this.billId = billId;
    }
}
