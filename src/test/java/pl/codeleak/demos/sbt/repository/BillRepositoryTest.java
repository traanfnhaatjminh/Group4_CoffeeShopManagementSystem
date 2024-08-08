package pl.codeleak.demos.sbt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pl.codeleak.demos.sbt.model.Bill;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BillRepositoryTest {

    @Autowired
    private BillRepository billRepository;

    @BeforeEach
    public void setUp() {
        // Clear any existing data if necessary
        billRepository.deleteAll();
    }

    @Test
    public void testFindByCreatedTimeBetween() {
        // Tạo và lưu dữ liệu thử nghiệm
        Bill bill1 = new Bill("1234567890", "Hanoi",
                Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                2, 100000.0f, 1, 1, 1, 1);
        billRepository.save(bill1);

        Bill bill2 = new Bill("0987654321", "Tp HCM",
                Date.from(LocalDate.of(2024, 1, 15).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                4, 200000.0f, 2, 2, 2, 2);
        billRepository.save(bill2);

        // Định nghĩa khoảng thời gian
        Date startDate = Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.of(2024, 1, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Kiểm tra phương thức repository
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bill> resultPage = billRepository.findByCreatedTimeBetween(startDate, endDate, pageable);

        // Xác nhận kết quả
        List<Bill> resultBills = resultPage.getContent();
        assertThat(resultBills).hasSize(2);
        assertThat(resultBills).contains(bill1, bill2);
    }

    @Test
    public void testFindByCreatedTimeBetween_NoData() {
        // Định nghĩa khoảng thời gian không có dữ liệu
        Date startDate = Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.of(2024, 1, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Kiểm tra phương thức repository
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bill> resultPage = billRepository.findByCreatedTimeBetween(startDate, endDate, pageable);

        // Xác nhận kết quả
        List<Bill> resultBills = resultPage.getContent();
        assertThat(resultBills).isEmpty();
    }

    @Test
    public void testFindByCreatedTimeBetween_InvalidDateRange() {
        // Định nghĩa khoảng thời gian ngược
        Date startDate = Date.from(LocalDate.of(2024, 1, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Kiểm tra phương thức repository
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bill> resultPage = billRepository.findByCreatedTimeBetween(startDate, endDate, pageable);

        // Xác nhận kết quả
        List<Bill> resultBills = resultPage.getContent();
        assertThat(resultBills).isEmpty();
    }
}
