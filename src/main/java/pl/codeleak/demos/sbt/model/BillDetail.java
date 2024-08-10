package pl.codeleak.demos.sbt.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Billdetail")
public class BillDetail {

    @EmbeddedId
    private BillDetailId id;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "price")
    private float price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pid", insertable = false, updatable = false)
    private Product product;

    public BillDetail() {
    }

    public BillDetail(int billId, int pid, int quantity, float price) {
        this.id = new BillDetailId(billId, pid);
        this.quantity = quantity;
        this.price = price;
    }
}
