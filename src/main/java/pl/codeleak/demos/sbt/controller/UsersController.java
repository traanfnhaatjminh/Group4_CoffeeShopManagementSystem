package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.BillDetailService;
import pl.codeleak.demos.sbt.service.BillService;
import pl.codeleak.demos.sbt.service.CartItemService;
import pl.codeleak.demos.sbt.service.UserService;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private BillService billService;

    @Autowired
    private BillDetailService billDetailService;

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        String username = principal.getName();
        Users user = userService.findByUsername(username);
        int userId = user.getUid();
        List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "profile");
        return "profile";
    }

    @PostMapping("/edit")
    public String editProfile(@RequestParam("fullname") String fullname,
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
                    return "redirect:/profile";
                }
            }
            userService.save1(existingUser);
            redirectAttributes.addFlashAttribute("message", "Cập nhật profile thành công!");
            return "redirect:/profile";
        }
        return "redirect:/profile?error=true";

    }

    @GetMapping("/changePassword")
    public String showChangePasswordForm() {
        return "changePassword"; // Ensure this view exists
    }


    @PostMapping("/changePassword")
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
            return "redirect:/profile/changePassword";
        }

        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
            return "redirect:/profile/changePassword";
        }

        // Check if the new password is not the same as the current password
        if (newPassword.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password cannot be the same as the current password.");
            return "redirect:/profile/changePassword";
        }

        // Update the password
        userService.updatePassword(user, newPassword);
        redirectAttributes.addFlashAttribute("message", "Password changed successfully!");

        return "redirect:/profile";
    }

    @GetMapping("/history")
    public String getUserBills(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String phone,
                               Model model, Principal principal) {
        String username = principal.getName();
        Users user = userService.findByUsername(username);
        int userId = user.getUid();
        List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("user", user);
        model.addAttribute("page", "profile");

        PageRequest pageable = PageRequest.of(page, 5); // 5 bills per page
        Page<Bill> bills = null;
        String errorMessage = null;

        if (phone != null && !phone.isEmpty()) {
            bills = billService.searchBillsByPhoneAndUserId(phone, userId, pageable);
            if (bills.isEmpty()) {
                errorMessage = "Số điện thoại không tồn tại!";
            }
            model.addAttribute("searchQuery", phone);
        } else {
            bills = billService.getBillsByUserId(userId, pageable);
        }

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        model.addAttribute("bills", bills);
        model.addAttribute("currentPage", page);
        return "orderhistory";
    }

    @GetMapping("/bill/details/{billId}")
    @ResponseBody
    public List<BillDetail> getBillDetails(@PathVariable int billId) {
        return billDetailService.findByBillId(billId);
    }

}
