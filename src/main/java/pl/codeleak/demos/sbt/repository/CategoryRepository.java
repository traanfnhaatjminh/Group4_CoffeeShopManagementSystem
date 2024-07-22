package pl.codeleak.demos.sbt.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

}
