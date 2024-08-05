package pl.codeleak.demos.sbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Tables;

import javax.persistence.Table;

@Repository
public interface TableRepository extends JpaRepository<Tables, Integer> {
}
