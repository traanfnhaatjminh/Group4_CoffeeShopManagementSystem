package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/staffProfile")
public class StaffProfileController {
    @Autowired
    private UserService userService;
    @GetMapping
    public String viewStaffProfile(Model model, Principal principal){
        String username = principal.getName();
        Users user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "staffProfile";
    }

    @PostMapping("/edit")
    public String editStaffProfile(@ModelAttribute Users user, Principal principal, RedirectAttributes redirectAttributes) {
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
        return "redirect:/staffProfile";
    }

    @GetMapping("/staffChangePassword")
    public String showChangePasswordForm() {
        return "staffChangePassword"; // Ensure this view exists
    }


    @PostMapping("/staffChangePassword")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Users user = userService.findByUsername(username); // Get the current user

        // Check if the current password is correct
        if (!userService.checkPassword(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/staffProfile/staffChangePassword";
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
            return "redirect:/staffProfile/staffChangePassword";
        }

        // Check if the new password is not the same as the current password
        if (newPassword.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password cannot be the same as the current password.");
            return "redirect:/staffProfile/staffChangePassword";
        }

        // Update the password
        userService.updatePassword(user, newPassword);
        redirectAttributes.addFlashAttribute("message", "Password changed successfully!");

        return "redirect:/staffProfile";
    }

}
