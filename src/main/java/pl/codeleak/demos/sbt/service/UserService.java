package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUser(Users user) {
        if (isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        if (isPhoneTaken(user.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }
        if (isUsernameTaken(user.getUsername())) {
            throw new IllegalArgumentException("Username đã được sử dụng");
        }

        user.setRole_id(1);
        user.setAvatar("abc");
        user.setStatus(1);

        if (user.getUsername() == null || user.getPass() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Username, password, and email cannot be null");
        }

        user.setPass(passwordEncoder.encode(user.getPass()));

        userRepository.insertUser(user.getFullname(), user.getDob(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.getAvatar(), user.getUsername(), user.getPass(), user.getRole_id(), user.getStatus());
    }

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Users save(Users user) {
        user.setPass(passwordEncoder.encode(user.getPass()));
        return userRepository.save(user);
    }

    public boolean checkPassword(Users user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPass());
    }

    public void updatePassword(Users user, String newPassword) {
        user.setPass(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean isPhoneTaken(String phone) {
        return userRepository.findByPhone(phone) != null;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username) != null;
    }
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }

}
