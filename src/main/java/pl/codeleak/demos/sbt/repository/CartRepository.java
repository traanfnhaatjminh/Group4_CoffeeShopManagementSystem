package pl.codeleak.demos.sbt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Cart;

@Repository
public interface CartRepository extends CrudRepository<Cart, Integer> {
    Iterable<Cart> findByUid(int uid);
}
