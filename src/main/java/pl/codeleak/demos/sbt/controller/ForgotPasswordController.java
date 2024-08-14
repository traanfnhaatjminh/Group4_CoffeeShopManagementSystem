package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.repository.UserRepository;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller

public class ForgotPasswordController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Users user = userRepository.findByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("messageErr", "Your email address does not exist!");
            return "redirect:/forgot-password";
        }


        String token = UUID.randomUUID().toString();

        // Store the token in memory, associating it with the user's email
        tokenStore.put(token, user.getEmail());

        String resetPasswordLink = "http://localhost:8080/reset-password?token=" + token;
        sendEmail(user.getEmail(), resetPasswordLink);

        redirectAttributes.addFlashAttribute("message", "We have sent a reset password link to your email.");
        return "redirect:/forgot-password";
    }

    private void sendEmail(String recipientEmail, String link) {
        String subject = "Here's the link to reset your password";
        String content = "Click the link below to reset your password:\n" + link;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token) {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("messageErr", "Passwords do not match");
            return "redirect:/reset-password?token=" + token;
        }

        // Validate the token
        String email = tokenStore.get(token);
        if (email == null) {
            redirectAttributes.addFlashAttribute("messageErr", "Invalid or expired token");
            return "redirect:/forgot-password";
        }

        Users user = userRepository.findByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("messageErr", "Invalid user");
            return "redirect:/forgot-password";
        }
//        user.setPass(newPassword);
        user.setPass(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove the token after successful reset
        tokenStore.remove(token);

        redirectAttributes.addFlashAttribute("message", "Your password has been successfully reset.");
        return "redirect:/login";
    }
}