package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.repository.BillRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

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

}