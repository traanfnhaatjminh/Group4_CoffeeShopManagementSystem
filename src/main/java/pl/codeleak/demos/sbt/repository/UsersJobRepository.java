package pl.codeleak.demos.sbt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codeleak.demos.sbt.model.UsersJob;

@Repository
public interface UsersJobRepository extends JpaRepository<UsersJob, Integer> {
    UsersJob findByUsername(String username);
}
