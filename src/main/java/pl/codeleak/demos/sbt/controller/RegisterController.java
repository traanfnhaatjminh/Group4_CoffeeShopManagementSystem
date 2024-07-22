package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping
    public String registerUser(Users user, RedirectAttributes redirectAttributes) {

        user.setRole(1);
        user.setAvatar("abc");


        if (user.getUsername() == null || user.getPass() == null || user.getEmail() == null) {
            redirectAttributes.addFlashAttribute("error", "Username, password, and email cannot be null");
            return "redirect:/register";
        }

        try {
            userService.saveUser(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to register user: " + e.getMessage());
            return "redirect:/register";
        }

        return "redirect:/login";
    }
}
