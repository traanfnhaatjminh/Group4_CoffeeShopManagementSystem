package pl.codeleak.demos.sbt.repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.model.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BillDetailRepositoryTest {

    @Autowired
    private BillDetailRepository billDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        // Tạo và lưu các đối tượng Product vào cơ sở dữ liệu
        Product product1 = new Product("Product 1", "Description 1", "Unit 1", 100, 10.0f, "image1.png", 1);
        Product product2 = new Product("Product 2", "Description 2", "Unit 2", 200, 20.0f, "image2.png", 2);

        productRepository.save(product1);
        productRepository.save(product2);

        // Tạo và lưu các đối tượng BillDetail vào cơ sở dữ liệu
        BillDetail billDetail1 = new BillDetail(100, product1.getPid(), 5, 10.0f);
        BillDetail billDetail2 = new BillDetail(100, product2.getPid(), 2, 20.0f);
        BillDetail billDetail3 = new BillDetail(200, product1.getPid(), 1, 15.0f);

        billDetailRepository.save(billDetail1);
        billDetailRepository.save(billDetail2);
        billDetailRepository.save(billDetail3);
    }

    @Test
    public void testFindByIdBillId_withExistingBillId() {
        // Khi billId tồn tại
        List<BillDetail> details = billDetailRepository.findByIdBillId(100);
        assertEquals(2, details.size(), "There should be 2 details for billId 100.");

        // Kiểm tra thông tin chi tiết
        assertTrue(details.stream().anyMatch(detail -> detail.getId().getBillId() == 100 && detail.getId().getPid() == 1), "Should contain detail for product with PID 1.");
        assertTrue(details.stream().anyMatch(detail -> detail.getId().getBillId() == 100 && detail.getId().getPid() == 2), "Should contain detail for product with PID 2.");
    }

    @Test
    public void testFindByIdBillId_withNonExistingBillId() {
        // Khi billId không tồn tại
        List<BillDetail> details = billDetailRepository.findByIdBillId(999);
        assertTrue(details.isEmpty(), "There should be no details for billId 999.");
    }

    @Test
    public void testFindByIdBillId_withEmptyDatabase() {
        // Xóa dữ liệu và kiểm tra
        billDetailRepository.deleteAll();
        productRepository.deleteAll();

        List<BillDetail> details = billDetailRepository.findByIdBillId(100);
        assertTrue(details.isEmpty(), "There should be no details when the database is empty.");
    }
}