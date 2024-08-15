package pl.codeleak.demos.sbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.BillDetail;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Integer> {
    List<BillDetail> findByIdBillId(int billId);

    @Query("SELECT SUM(bd.quantity) FROM BillDetail bd")
    Long findTotalNumberOfProducts();
}
