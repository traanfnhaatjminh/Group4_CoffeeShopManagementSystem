package pl.codeleak.demos.sbt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Bill;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    Page<Bill> findByCreatedTimeBetween(Date startDate, Date endDate, Pageable pageable);

    Page<Bill> findByUserId(int userId, Pageable pageable);

    Page<Bill> findByPhoneContainingAndUserId(String phone, int userId, Pageable pageable);

    @Query("SELECT MONTH(b.createdTime) AS month, SUM(b.totalCost) AS totalRevenue "
            + "FROM Bill b GROUP BY MONTH(b.createdTime)")
    List<Object[]> findMonthlyRevenue();

    @Query("SELECT MONTH(b.createdTime) AS month, COUNT(b.billId) AS totalOrders "
            + "FROM Bill b WHERE b.type = 0 GROUP BY MONTH(b.createdTime)")
    List<Object[]> findMonthlyOnlineOrders();

    @Query("SELECT MONTH(b.createdTime) AS month, COUNT(b.billId) AS totalOrders "
            + "FROM Bill b WHERE b.type = 1 GROUP BY MONTH(b.createdTime)")
    List<Object[]> findMonthlyOfflineOrders();
}

