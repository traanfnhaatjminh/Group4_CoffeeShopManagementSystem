package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.UsersJob;
import pl.codeleak.demos.sbt.repository.UsersJobRepository;

@Service
public class UsersJobService {

    @Autowired
    private UsersJobRepository usersJobRepository;

    public UsersJob findByUsername(String username) {
        return usersJobRepository.findByUsername(username);
    }


}
