package pl.codeleak.demos.sbt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Category;

@Repository

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoriesByCategoryName(String name);
    Page<Category> findByCategoryNameContainingIgnoreCase(String keyword, Pageable pageable);
}
