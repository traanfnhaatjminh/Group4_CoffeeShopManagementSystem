package pl.codeleak.demos.sbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.codeleak.demos.sbt.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);
    Users findByEmail(String email);
    Users findByPhone(String phone);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Users (fullname, dob, email, phone, address, avatar, username, pass, role_id) " +
            "VALUES (:fullname, :dob, :email, :phone, :address, :avatar, :username, :pass, :role_id)", nativeQuery = true)
    void insertUser(@Param("fullname") String fullname,
                    @Param("dob") String dob,
                    @Param("email") String email,
                    @Param("phone") String phone,
                    @Param("address") String address,
                    @Param("avatar") String avatar,
                    @Param("username") String username,
                    @Param("pass") String pass,
                    @Param("role_id") int role_id);
}



