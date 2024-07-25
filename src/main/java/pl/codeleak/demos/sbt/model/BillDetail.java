package pl.codeleak.demos.sbt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "Billdetail")
public class BillDetail {

    @EmbeddedId
    private BillDetailId id;

    private int quantity;

    private float price;

    public BillDetail() {
    }

    public BillDetail(int billId, int pid, int quantity, float price) {
        this.id = new BillDetailId(billId, pid);
        this.quantity = quantity;
        this.price = price;
    }
}
