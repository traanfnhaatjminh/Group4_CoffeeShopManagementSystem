package pl.codeleak.demos.sbt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.codeleak.demos.sbt.model.Tables;
import pl.codeleak.demos.sbt.repository.TableRepository;

import java.util.List; // Import List
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TableServiceTest {

    @InjectMocks
    private TableService tableService;

    @Mock
    private TableRepository tableRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTables() {
        // Tạo một danh sách các bảng giả lập
        Tables table1 = new Tables(1, 4, 1);
        Tables table2 = new Tables(2, 2, 0);
        when(tableRepository.findAll()).thenReturn(List.of(table1, table2));

        // Gọi phương thức cần kiểm tra
        Iterable<Tables> tables = tableService.getAllTables();

        // Xác nhận kết quả
        assertNotNull(tables);
        assertTrue(tables.iterator().hasNext());
        assertEquals(2, ((List<?>) tables).size());
    }

    @Test
    void testUpdateTableStatus() {
        // Tạo một bảng giả lập
        Tables table = new Tables(1, 4, 1);
        when(tableRepository.findById(anyInt())).thenReturn(Optional.of(table));

        // Gọi phương thức cần kiểm tra
        tableService.updateTableStatus(1, 0);

        // Xác nhận rằng phương thức save đã được gọi với bảng đã được cập nhật
        verify(tableRepository, times(1)).save(argThat(t -> t.getStatus() == 0));
    }

    @Test
    void testFindByTid() {
        // Tạo một bảng giả lập
        Tables table = new Tables(1, 4, 1);
        when(tableRepository.findById(anyInt())).thenReturn(Optional.of(table));

        // Gọi phương thức cần kiểm tra
        Tables result = tableService.findByTid(1);

        // Xác nhận kết quả
        assertNotNull(result);
        assertEquals(1, result.getTid());
        assertEquals(4, result.getNumberOfChair());
        assertEquals(1, result.getStatus());
    }

    @Test
    void testSave() {
        // Tạo một bảng giả lập
        Tables table = new Tables(1, 4, 1);

        // Gọi phương thức cần kiểm tra
        tableService.save(table);

        // Xác nhận rằng phương thức save đã được gọi
        verify(tableRepository, times(1)).save(table);
    }
}
