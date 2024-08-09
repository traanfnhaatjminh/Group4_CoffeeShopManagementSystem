package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Jobboard;
import pl.codeleak.demos.sbt.repository.JobboardRepository;

@Service
public class JobboardService {

    @Autowired
    private JobboardRepository jobboardRepository;
    // Method to get all job boards with pagination
    public Page<Jobboard> getAllJobboards(Pageable pageable) {
        return jobboardRepository.findAll(pageable);
    }

    // Method to search job boards with pagination
    public Page<Jobboard> searchJobboards(String fullname, Pageable pageable) {
        if (fullname == null || fullname.isEmpty()) {
            return getAllJobboards(pageable); // Return paginated list if no search term is provided
        }
        return jobboardRepository.findByUserFullnameContaining(fullname, pageable);
    }
    public void incrementPresents(int jobboardId) {
        Jobboard jobboard = jobboardRepository.findById(jobboardId).orElseThrow(() -> new RuntimeException("Jobboard not found"));
        jobboard.setPresents(jobboard.getPresents() + 1);
        jobboardRepository.save(jobboard);
    }

    public void incrementAbsents(int jobboardId) {
        Jobboard jobboard = jobboardRepository.findById(jobboardId).orElseThrow(() -> new RuntimeException("Jobboard not found"));
        jobboard.setAbsents(jobboard.getAbsents() + 1);
        jobboardRepository.save(jobboard);
    }
    public void updateShift(int jobboardId, int shift) {
        Jobboard jobboard = jobboardRepository.findById(jobboardId)
                .orElseThrow(() -> new RuntimeException("Jobboard not found"));
        jobboard.setShift(shift);
        jobboardRepository.save(jobboard);
    }
}