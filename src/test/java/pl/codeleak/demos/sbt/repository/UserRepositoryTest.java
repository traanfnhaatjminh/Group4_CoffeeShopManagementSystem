package pl.codeleak.demos.sbt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pl.codeleak.demos.sbt.model.Users;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Đảm bảo không có dữ liệu cũ trong cơ sở dữ liệu

        // Thêm dữ liệu mẫu
        userRepository.insertUser("tuan minh", "2003-10-15", "minhdeptrai15102003@gmail.com", "1234567890", "Hanoi", "avatar.png", "tuanminh", "123", 1, 1);
        userRepository.insertUser("nhat minh", "2003-05-15", "minhtn@gmail.com", "0987654321", "Tp HCM", "avatar2.png", "minh05", "123", 2, 0);
    }

    @Test
    void testFindByUsername() {
        Users user = userRepository.findByUsername("tuanminh");
        assertThat(user).isNotNull();
        assertThat(user.getFullname()).isEqualTo("tuan minh");
    }

    @Test
    void testFindByEmail() {
        Users user = userRepository.findByEmail("minhtn@gmail.com");
        assertThat(user).isNotNull();
        assertThat(user.getPhone()).isEqualTo("0987654321");
    }

    @Test
    void testFindByPhone() {
        Users user = userRepository.findByPhone("1234567890");
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("minhdeptrai15102003@gmail.com");
    }

    @Test
    void testDeleteById() {
        userRepository.insertUser("the manh", "2003-01-01", "manhnt@gmail.com", "1234567890", "Hola", "avatar.png", "themanh", "123", 1, 1);

        // Xác nhận dữ liệu đã được thêm
        Users user = userRepository.findById(1).orElseThrow();
        assertThat(user).isNotNull();

        // Xóa người dùng với ID 1
        userRepository.deleteById(1);
        Optional<Users> deletedUser = userRepository.findById(1);

        // Kiểm tra rằng người dùng đã bị xóa
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    void testFindByRole() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> usersPage = userRepository.findByRole(1, pageable);
        assertThat(usersPage.getTotalElements()).isEqualTo(1);
        assertThat(usersPage.getContent().get(0).getUsername()).isEqualTo("tuanminh");
    }

    @Test
    void testSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> usersPage = userRepository.search("nhat", pageable);
        assertThat(usersPage.getTotalElements()).isEqualTo(1);
        assertThat(usersPage.getContent().get(0).getEmail()).isEqualTo("minhtn@gmail.com");
    }

    @Test
    void testSearchByKeywordAndRole() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> usersPage = userRepository.searchByKeywordAndRole("tuan", 1, pageable);
        assertThat(usersPage.getTotalElements()).isEqualTo(1);
        assertThat(usersPage.getContent().get(0).getUsername()).isEqualTo("tuanminh");
    }
}
