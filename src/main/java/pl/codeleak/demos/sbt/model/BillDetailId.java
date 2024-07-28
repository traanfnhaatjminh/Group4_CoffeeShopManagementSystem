package pl.codeleak.demos.sbt.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class BillDetailId implements Serializable {
    @Column(name = "bill_id")
    private int billId;

    @Column(name = "pid")
    private int pid;

    public BillDetailId() {
    }

    public BillDetailId(int billId, int pid) {
        this.billId = billId;
        this.pid = pid;
    }
}
