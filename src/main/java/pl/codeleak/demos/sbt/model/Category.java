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
@Table(name = "Category")
public class Category {
    @Id
    private int cid;
    @Column(name = "[group]")
    private String groupName;
    @Column(name = "category_name")
    private String categoryName;

    public Category() {
    }

    public Category(int cid, String groupName, String categoryName) {
        this.cid = cid;
        this.groupName = groupName;
        this.categoryName = categoryName;
    }
}
