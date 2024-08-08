package pl.codeleak.demos.sbt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.codeleak.demos.sbt.model.Cart;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    public void setUp() {
        // Xóa tất cả dữ liệu trước mỗi bài kiểm tra
        cartRepository.deleteAll();
    }

    @Test
    public void testFindByUid() {
        // Tạo và lưu dữ liệu thử nghiệm
        Cart cart1 = new Cart(101, 2, 50000.0f, 1);
        cartRepository.save(cart1);

        Cart cart2 = new Cart(102, 3, 75000.0f, 1);
        cartRepository.save(cart2);

        // Kiểm tra phương thức repository
        Iterable<Cart> carts = cartRepository.findByUid(1);

        // Xác nhận kết quả
        assertThat(carts).hasSize(2);
        assertThat(carts).contains(cart1, cart2);
    }

    @Test
    public void testFindByPidAndUid() {
        // Tạo và lưu dữ liệu thử nghiệm
        Cart cart = new Cart(101, 2, 50000.0f, 1);
        cartRepository.save(cart);

        // Kiểm tra phương thức repository
        Optional<Cart> foundCart = cartRepository.findByPidAndUid(101, 1);

        // Xác nhận kết quả
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get()).isEqualTo(cart);
    }

    @Test
    public void testFindByPidAndUid_NotFound() {
        // Kiểm tra phương thức repository với dữ liệu không tồn tại
        Optional<Cart> foundCart = cartRepository.findByPidAndUid(999, 1);

        // Xác nhận kết quả
        assertThat(foundCart).isNotPresent();
    }
}
