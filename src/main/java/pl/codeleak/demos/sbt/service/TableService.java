package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.model.Tables;
import pl.codeleak.demos.sbt.repository.TableRepository;

import javax.persistence.Table;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    public Iterable<Tables> getAllTables() {
        return tableRepository.findAll();
    }

    public void updateTableStatus(int tableId, int status) {
        Tables table = tableRepository.findById(tableId).orElseThrow(() -> new RuntimeException("Table not found"));
        table.setStatus(status);
        tableRepository.save(table);
    }
    public Tables findByTid(int tid ) {
        return tableRepository.findById(tid).orElse(null); // Assuming findById from JpaRepository
    }
    public void save(Tables table) {
        tableRepository.save(table);
    }

}
