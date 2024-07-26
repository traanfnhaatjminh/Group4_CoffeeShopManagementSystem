package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private static final String UPLOAD_DIR = "uploads/";
    @Autowired
    private UserRepository userRepository;

    public Optional<Users> getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public void saveUser(Users user) {
        user.setRole_id(1);
        user.setAvatar("abc");


        if (user.getUsername() == null || user.getPass() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Username, password, and email cannot be null");
        }

        userRepository.insertUser(user.getFullname(), user.getDob(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.getAvatar(), user.getUsername(), user.getPass(), user.getRole_id());
    }


    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Users save(Users user) {
        return userRepository.save(user);
    }
}



