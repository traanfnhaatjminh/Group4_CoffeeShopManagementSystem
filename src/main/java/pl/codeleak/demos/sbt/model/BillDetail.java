package pl.codeleak.demos.sbt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "BillDetail")
public class BillDetail {

    @Id
    @Column(name = "bill_id")
    private int billId;

    @Column(name = "pid")
    private int pid;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private int price;

    public BillDetail() {
    }

    public BillDetail(int billId, int pid, int quantity, int price) {
        this.billId = billId;
        this.pid = pid;
        this.quantity = quantity;
        this.price = price;
    }

}
