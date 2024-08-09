package pl.codeleak.demos.sbt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.Jobboard;

@Repository
public interface JobboardRepository extends JpaRepository<Jobboard, Integer> {
    Page<Jobboard> findByUserFullnameContaining(String fullname, Pageable pageable);
}
