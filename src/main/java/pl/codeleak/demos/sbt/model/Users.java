package pl.codeleak.demos.sbt.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Data
@Entity
@Table(name="Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "uid")
    private Long uid;

    @Column(name="fullname")
    private String fullname;

    @Column(name="dob")
    private String dob;

    @Column(name="email")
    private String email;

    @Column(name="phone")
    private String phone;

    @Column(name="address")
    private String address;


    @Column(name="avatar")
    private String avatar = "abc";

    @Column(name="username")
    private String username;

    @Column(name="pass")
    private String pass;

    @Column(name="role")
    private int role;



    public Users() {

    }

    public Users(Long uid, String fullname, String dob, String email, String phone, String address, String avatar, String username, String pass, int role) {
        this.uid = uid;
        this.fullname = fullname;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.username = username;
        this.pass = pass;
        this.role = role;
    }

}
