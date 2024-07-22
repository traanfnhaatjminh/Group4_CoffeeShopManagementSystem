package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(Users user) {
        user.setRole(1);
        user.setAvatar("abc");

      
        if (user.getUsername() == null || user.getPass() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Username, password, and email cannot be null");
        }

        userRepository.insertUser(user.getFullname(), user.getDob(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.getAvatar(), user.getUsername(), user.getPass(), user.getRole());
    }
}
