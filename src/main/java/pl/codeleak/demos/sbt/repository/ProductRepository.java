package pl.codeleak.demos.sbt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
       Iterable<Product> findByCategoryId(int cid);
       Page<Product> findByCategoryId(int categoryId, Pageable pageable);

       @Query(value = "SELECT TOP 3 * FROM Product ORDER BY pid DESC;", nativeQuery = true)
       List<Product> getTop3Products();
       Page<Product> findByPnameContainingIgnoreCase(String pname, Pageable pageable);

}
