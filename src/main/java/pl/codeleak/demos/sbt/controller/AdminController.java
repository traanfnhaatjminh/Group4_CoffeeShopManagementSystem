package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(name = "keyword", required = false) String keyword,
                            Model model) {
        int pageSize = 4; // Số phần tử trên một trang

        Page<Users> page;
        if (keyword != null && !keyword.isEmpty()) {
            page = userService.search(keyword, pageNo, pageSize);
            model.addAttribute("keyword", keyword);
        } else {
            page = userService.findPaginated(pageNo, pageSize);
        }

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("users", page.getContent());
        return "user_list";
    }

    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable("id") int id, Model model) {
        Users user = userService.findById(id);
        model.addAttribute("user", user);
        return "user_view";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") int id, @RequestParam("status") int status, RedirectAttributes redirectAttributes) {
        Users user = userService.findById(id);
        if (user != null) {
            user.setStatus(status);
            userService.save(user);
            redirectAttributes.addFlashAttribute("message", "Status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found!");
        }
        return "redirect:/admin/users/view/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/admin/users";
    }
}
