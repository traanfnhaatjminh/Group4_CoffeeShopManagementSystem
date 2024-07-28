package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.repository.BillDetailRepository;

@Service
public class BillDetailService {

    @Autowired
    private BillDetailRepository billDetailRepository;

    public void save(BillDetail billDetail) {
        billDetailRepository.save(billDetail);
    }
}
