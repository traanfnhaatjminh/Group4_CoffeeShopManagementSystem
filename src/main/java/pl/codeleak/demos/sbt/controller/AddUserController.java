package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

@Controller
@RequestMapping("/register2")
public class AddUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Users());
        return "register2";
    }

    @PostMapping
    public String registerUser(Users user, Model model, @RequestParam("role_id") int roleId, @RequestParam("status") int status) {

        user.setRole_id(roleId);
        user.setStatus(status);
        user.setAvatar("abc");

        boolean hasErrors = false;

        if (user.getUsername() == null || user.getPass() == null || user.getEmail() == null) {
            model.addAttribute("generalError", "Username, password, and email cannot be null");
            hasErrors = true;
        }

        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("emailError", "Email đã được sử dụng");
            hasErrors = true;
        }

        if (userService.isPhoneTaken(user.getPhone())) {
            model.addAttribute("phoneError", "Số điện thoại đã được sử dụng");
            hasErrors = true;
        }

        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("usernameError", "Username đã được sử dụng");
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("user", user);
            return "register2";
        }

        try {
            userService.saveUser2(user);
        } catch (Exception e) {
            model.addAttribute("generalError", "Failed to register user: " + e.getMessage());
            model.addAttribute("user", user);
            return "register2";
        }

        return "redirect:/admin/users";
    }
}
