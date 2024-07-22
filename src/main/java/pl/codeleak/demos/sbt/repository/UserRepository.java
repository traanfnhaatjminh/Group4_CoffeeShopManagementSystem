package pl.codeleak.demos.sbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.codeleak.demos.sbt.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Users (fullname, dob, email, phone, address, avatar, username, pass, role) " +
            "VALUES (:fullname, :dob, :email, :phone, :address, :avatar, :username, :pass, :role)", nativeQuery = true)
    void insertUser(@Param("fullname") String fullname,
                    @Param("dob") String dob,
                    @Param("email") String email,
                    @Param("phone") String phone,
                    @Param("address") String address,
                    @Param("avatar") String avatar,
                    @Param("username") String username,
                    @Param("pass") String pass,
                    @Param("role") int role);
}
