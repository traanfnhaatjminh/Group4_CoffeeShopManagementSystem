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
@Table(name = "Dinnertable")
public class Tables {
    @Id
    @Column(name = "table_id")
    private int tid;
    @Column(name = "number_of_chair")
    private int numberOfChair;
    @Column(name = "status")
    private int status;

    public Tables() {
    }

    public Tables(int tid, int numberOfChair, int status) {
        this.tid = tid;
        this.numberOfChair = numberOfChair;
        this.status = status;
    }

}
