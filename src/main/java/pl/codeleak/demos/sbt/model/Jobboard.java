package pl.codeleak.demos.sbt.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Jobboard")
public class Jobboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid")
    private UsersJob user;

    @Column(name = "shift")
    private int shift;

    @Column(name = "presents")
    private int presents;

    @Column(name = "absents")
    private int absents;

    // No need for explicit getters and setters due to Lombok @Data
}
