package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.repository.BillDetailRepository;
import pl.codeleak.demos.sbt.repository.BillRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {
    @Autowired
    private BillDetailRepository billDetailRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BillDetailService billDetailService;

    public void save(Bill bill) {
        billRepository.save(bill);
    }

    public Iterable<Bill> getAllBills(PageRequest pageRequest) {
        return billRepository.findAll();
    }

    public Page<Bill> getAllBills(Pageable pageable) {
        return billRepository.findAll(pageable);
    }

    public Page<Bill> searchByCreatedTime(Date date, Pageable pageable) {
        // Setting up the date range for the day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();

        return billRepository.findByCreatedTimeBetween(startDate, endDate, pageable);
    }

    public Bill findById(int billId) {
        return billRepository.findById(billId).orElse(null); // Assuming findById from JpaRepository
    }

    public List<BillDetail> getBillDetails(int billId) {
        return billDetailService.findByBillId(billId);
    }

    public float calculateTotalCost(int billId) {
        List<BillDetail> billDetails = getBillDetails(billId);
        return (float) billDetails.stream()
                .mapToDouble(detail -> detail.getQuantity() * detail.getPrice())
                .sum();
    }

    public void updateBillStatus(int billId, Integer status) {
        Optional<Bill> optionalBill = billRepository.findById(billId);
        if (optionalBill.isPresent()) {
            Bill bill = optionalBill.get();
            bill.setStatus(status);
            billRepository.save(bill);
        }
    }

    public Page<Bill> getBillsByUserId(int userId, Pageable pageable) {
        return billRepository.findByUserId(userId, pageable);
    }
}