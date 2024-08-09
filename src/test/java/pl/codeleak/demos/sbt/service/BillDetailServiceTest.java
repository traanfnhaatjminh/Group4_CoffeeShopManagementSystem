package pl.codeleak.demos.sbt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.repository.BillDetailRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BillDetailServiceTest {

    @Mock
    private BillDetailRepository billDetailRepository;

    @InjectMocks
    private BillDetailService billDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        // Arrange
        BillDetail billDetail = new BillDetail();

        // Act
        billDetailService.save(billDetail);

        // Assert
        verify(billDetailRepository, times(1)).save(billDetail);
    }

    @Test
    void testFindByBillId() {
        // Arrange
        int billId = 1;
        List<BillDetail> expectedBillDetails = new ArrayList<>();
        when(billDetailRepository.findByIdBillId(billId)).thenReturn(expectedBillDetails);

        // Act
        List<BillDetail> actualBillDetails = billDetailService.findByBillId(billId);

        // Assert
        verify(billDetailRepository, times(1)).findByIdBillId(billId);
        assertEquals(expectedBillDetails, actualBillDetails);
    }
}
