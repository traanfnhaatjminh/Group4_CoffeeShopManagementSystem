//
//
//
//package pl.codeleak.demos.sbt.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import pl.codeleak.demos.sbt.model.Users;
//import pl.codeleak.demos.sbt.service.UserService;
//
//@Controller
//@RequestMapping("/admin/users")
//public class AdminController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public String listUsers(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
//                            @RequestParam(name = "keyword", required = false) String keyword,
//                            @RequestParam(name = "role", required = false) Integer role,
//                            Model model) {
//        int pageSize = 5;
//
//        Page<Users> page;
//        if (keyword != null && !keyword.isEmpty()) {
//            page = userService.search(keyword, role, pageNo, pageSize);
//            model.addAttribute("keyword", keyword);
//        } else if (role != null) {
//            page = userService.findByRole(role, pageNo, pageSize);
//        } else {
//            page = userService.findPaginated(pageNo, pageSize);
//        }
//
//        model.addAttribute("totalPage", page.getTotalPages());
//        model.addAttribute("currentPage", pageNo);
//        model.addAttribute("users", page.getContent());
//        return "user_list";
//    }
//
//    @PostMapping("/update")
//    public String saveUpdatedUser(@ModelAttribute("user") Users user, RedirectAttributes redirectAttributes) {
//        Users existingUser = userService.findById(user.getUid());
//        if (existingUser != null) {
//            existingUser.setStatus(user.getStatus());
//            existingUser.setRole_id(user.getRole_id());
//            userService.save(existingUser);
//            redirectAttributes.addFlashAttribute("message", "User status and role updated successfully!");
//        } else {
//            redirectAttributes.addFlashAttribute("error", "User not found!");
//        }
//        return "redirect:/admin/users";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
//        userService.deleteById(id);
//        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
//        return "redirect:/admin/users";
//    }
//
//    @PostMapping("/add")
//    public String addUser(@ModelAttribute Users user, RedirectAttributes redirectAttributes) {
//        user.setAvatar("abc");
//        userService.save(user);
//        redirectAttributes.addFlashAttribute("message", "User added successfully!");
//        return "redirect:/admin/users";
//    }
//}



package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(name = "keyword", required = false) String keyword,
                            @RequestParam(name = "role", required = false) Integer role,
                            Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        int pageSize = 5; // Số phần tử trên một trang

        Page<Users> page;
        if (keyword != null && !keyword.isEmpty()) {
            page = userService.search(keyword, role, pageNo, pageSize);
            model.addAttribute("keyword", keyword);
        } else if (role != null) {
            page = userService.findByRole(role, pageNo, pageSize);
        } else {
            page = userService.findPaginated(pageNo, pageSize);
        }

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("users", page.getContent());
        model.addAttribute("role", role);  // Thêm role vào model
        return "user_list";
    }
    @GetMapping("/filter")
    public String filterUsersByRole(@RequestParam(name = "role", required = false) Integer role,
                                    @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                    Model model) {
        int pageSize = 5;

        Page<Users> page;
        if (role != null) {
            page = userService.findByRole(role, pageNo, pageSize);
            model.addAttribute("role", role);
        } else {
            page = userService.findPaginated(pageNo, pageSize);
        }

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("users", page.getContent());
        return "user_list";
    }


    @PostMapping("/update")
    public String saveUpdatedUser(@ModelAttribute("user") Users user, RedirectAttributes redirectAttributes) {
        Users existingUser = userService.findById(user.getUid());
        if (existingUser != null) {
            existingUser.setStatus(user.getStatus());
            existingUser.setRole_id(user.getRole_id());
            userService.save1(existingUser);
            redirectAttributes.addFlashAttribute("message", "User status and role updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute Users user, RedirectAttributes redirectAttributes) {
        return "redirect:/register2";
    }
}
