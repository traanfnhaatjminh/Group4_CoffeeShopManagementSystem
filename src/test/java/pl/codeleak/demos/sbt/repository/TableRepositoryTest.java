package pl.codeleak.demos.sbt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.codeleak.demos.sbt.model.Tables;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TableRepositoryTest {

    @Autowired
    private TableRepository tableRepository;

    @BeforeEach
    void setUp() {
        tableRepository.deleteAll(); // Đảm bảo không có dữ liệu cũ trong cơ sở dữ liệu

        // Thêm dữ liệu mẫu
        tableRepository.save(new Tables(1, 4, 1));
        tableRepository.save(new Tables(2, 2, 0));
        tableRepository.save(new Tables(3, 6, 1));
    }

    @Test
    void testFindById() {
        Optional<Tables> table = tableRepository.findById(1);
        assertThat(table).isPresent();
        assertThat(table.get().getNumberOfChair()).isEqualTo(4);
    }

    @Test
    void testSave() {
        Tables table = new Tables(4, 8, 1);
        Tables savedTable = tableRepository.save(table);
        assertThat(savedTable.getTid()).isEqualTo(4);
        assertThat(savedTable.getNumberOfChair()).isEqualTo(8);
        assertThat(savedTable.getStatus()).isEqualTo(1);
    }

    @Test
    void testDelete() {
        tableRepository.deleteById(1);
        Optional<Tables> table = tableRepository.findById(1);
        assertThat(table).isNotPresent();
    }

    @Test
    void testFindAll() {
        Iterable<Tables> tables = tableRepository.findAll();
        assertThat(tables).hasSize(3);
    }

    @Test
    void testFindByIdPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tables> tablePage = tableRepository.findAll(pageable);
        assertThat(tablePage.getTotalElements()).isEqualTo(3);
    }
}
