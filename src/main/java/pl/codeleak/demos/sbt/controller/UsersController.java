package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        Users user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/edit")
    public String editProfile(@ModelAttribute Users user, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Users existingUser = userService.findByUsername(username);

        // Update user details
        existingUser.setFullname(user.getFullname());
        existingUser.setDob(user.getDob());
        existingUser.setAddress(user.getAddress());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());

        userService.save(existingUser);

        redirectAttributes.addFlashAttribute("message", "Edit Successfully!");
        return "redirect:/profile";
    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Users user = userService.findByUsername(username);

        // Verify current password
        if (!user.getPass().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/profile";
        }

        // Check if new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match");
            return "redirect:/profile";
        }
        // Check if new password is the same as the current password
        if (user.getPass().equals(newPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password cannot be the same as the current password");
            return "redirect:/profile";
        }
        // Update password
        user.setPass(newPassword);
        userService.save(user);

        redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
        return "redirect:/profile";
    }
}

