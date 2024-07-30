package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.repository.BillRepository;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public void save(Bill bill) {
        billRepository.save(bill);
    }

    public Iterable<Bill> getAllBills() {
        return billRepository.findAll();
    }
}
