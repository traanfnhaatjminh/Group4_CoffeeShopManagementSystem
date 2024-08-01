package pl.codeleak.demos.sbt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "Cartitem")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int itemId;
    @Column(name = "product_id")
    private int pid;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "total_cost")
    private float totalCost;
    @Column(name = "[user_id]")
    private int uid;

    public Cart() {
    }

    public Cart(int pid, int quantity, float totalCost, int uid) {
        this.pid = pid;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.uid = uid;
    }

}
