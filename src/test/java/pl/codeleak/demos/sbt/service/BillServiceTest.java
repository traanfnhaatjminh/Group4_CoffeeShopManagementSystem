package pl.codeleak.demos.sbt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.model.BillDetailId;
import pl.codeleak.demos.sbt.repository.BillDetailRepository;
import pl.codeleak.demos.sbt.repository.BillRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BillServiceTest {

    @Mock
    private BillDetailRepository billDetailRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillDetailService billDetailService;

    @InjectMocks
    private BillService billService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        Bill bill = new Bill("123456789", "Some Address", new Date(), 4, 100.0f, 1, 2, 1, 1);

        billService.save(bill);

        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testGetAllBillsWithoutPageable() {
        List<Bill> bills = new ArrayList<>();
        when(billRepository.findAll()).thenReturn(bills); // List can be used here as Iterable

        Iterable<Bill> result = billService.getAllBills(PageRequest.of(0, 10));

        verify(billRepository, times(1)).findAll();
        assertEquals(bills, result);
    }

    @Test
    void testGetAllBillsWithPageable() {
        List<Bill> bills = new ArrayList<>();
        Page<Bill> page = new PageImpl<>(bills);
        Pageable pageable = PageRequest.of(0, 10);
        when(billRepository.findAll(pageable)).thenReturn(page);

        Page<Bill> result = billService.getAllBills(pageable);

        verify(billRepository, times(1)).findAll(pageable);
        assertEquals(page, result);
    }

    @Test
    void testSearchByCreatedTime() {
        Date date = new Date();
        List<Bill> bills = new ArrayList<>();
        Page<Bill> page = new PageImpl<>(bills);
        Pageable pageable = PageRequest.of(0, 10);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();

        when(billRepository.findByCreatedTimeBetween(startDate, endDate, pageable)).thenReturn(page);

        Page<Bill> result = billService.searchByCreatedTime(date, pageable);

        verify(billRepository, times(1)).findByCreatedTimeBetween(startDate, endDate, pageable);
        assertEquals(page, result);
    }

    @Test
    void testFindById() {
        int billId = 1;
        Bill bill = new Bill(billId);
        when(billRepository.findById(billId)).thenReturn(Optional.of(bill));

        Bill result = billService.findById(billId);

        verify(billRepository, times(1)).findById(billId);
        assertEquals(bill, result);
    }

    @Test
    void testGetBillDetails() {
        int billId = 1;
        List<BillDetail> billDetails = Arrays.asList(
                new BillDetail(billId, 1, 5, 10.0f),
                new BillDetail(billId, 2, 3, 20.0f)
        );
        when(billDetailService.findByBillId(billId)).thenReturn(billDetails);

        List<BillDetail> result = billService.getBillDetails(billId);

        verify(billDetailService, times(1)).findByBillId(billId);
        assertEquals(billDetails, result);
    }

    @Test
    void testCalculateTotalCost() {
        int billId = 1;
        List<BillDetail> billDetails = Arrays.asList(
                new BillDetail(billId, 1, 2, 10.0f), // 2 * 10.0 = 20.0
                new BillDetail(billId, 2, 1, 20.0f)  // 1 * 20.0 = 20.0
        );
        when(billDetailService.findByBillId(billId)).thenReturn(billDetails);

        float totalCost = billService.calculateTotalCost(billId);

        assertEquals(40.0, totalCost, 0.01);
    }

    @Test
    void testUpdateBillStatus() {
        int billId = 1;
        Bill bill = new Bill(billId);
        bill.setStatus(1);  // Initial status
        when(billRepository.findById(billId)).thenReturn(Optional.of(bill));

        billService.updateBillStatus(billId, 2);

        verify(billRepository, times(1)).findById(billId);
        verify(billRepository, times(1)).save(bill);
        assertEquals(2, bill.getStatus());
    }
}
