package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

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
    public String editStaffProfile(@RequestParam("fullname") String fullname,
                                   @RequestParam("dob") String dob,
                                   @RequestParam("address") String address,
                                   @RequestParam("email") String email,
                                   @RequestParam("phone") String phone,
                                   @RequestParam("avatar") MultipartFile avatarFile,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Users existingUser = userService.findByUsername(username);
        boolean hasErrors = false;
        // Validation
        if (!email.equals(existingUser.getEmail()) && userService.isEmailTaken(email)) {
            redirectAttributes.addFlashAttribute("emailError", "Email đã được sử dụng");
            hasErrors = true;
        }
        if (!phone.equals(existingUser.getPhone()) && userService.isPhoneTaken(phone)) {
            redirectAttributes.addFlashAttribute("phoneError", "Số điện thoại đã được sử dụng");
            hasErrors = true;
        }
        // Update if no errors
        if (!hasErrors) {
            existingUser.setFullname(fullname);
            existingUser.setDob(dob);
            existingUser.setAddress(address);
            existingUser.setEmail(email);
            existingUser.setPhone(phone);
            if (!avatarFile.isEmpty()) {
                try {
                    String avatarBase64 = Base64.getEncoder().encodeToString(avatarFile.getBytes());
                    existingUser.setAvatar(avatarBase64);
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "Upload avatar thất bại");
                    return "redirect:/staffProfile";
                }
            }
            userService.save1(existingUser);
            redirectAttributes.addFlashAttribute("message", "Cập nhật profile thành công!");
            return "redirect:/staffProfile";
        }
        return "redirect:/staffProfile?error=true";

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
