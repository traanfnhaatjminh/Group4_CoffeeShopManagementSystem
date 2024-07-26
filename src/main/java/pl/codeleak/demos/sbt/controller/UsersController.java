package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;


@Controller
@RequestMapping("/user")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping("/{uid}")
    public String viewUserProfile(@PathVariable("uid") int uid, Model model) {
        Users user = userService.getUserById(uid);
        if (user == null) {
            return "error"; // or redirect to an error page
        }
        model.addAttribute("user", user);
        return "profile"; // Thymeleaf template name
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateUserProfile(@ModelAttribute Users user) {
        try {
            userService.updateUser(user);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
}