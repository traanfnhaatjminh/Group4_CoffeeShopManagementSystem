package pl.codeleak.demos.sbt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
       Iterable<Product> findByCategoryId(int cid);
}
