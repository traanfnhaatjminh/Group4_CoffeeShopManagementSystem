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
import pl.codeleak.demos.sbt.service.BillService;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BillService billService;

    @GetMapping
    public String listUsers(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(name = "keyword", required = false) String keyword,
                            @RequestParam(name = "role", required = false) Integer role,
                            @RequestParam(name = "pageSize", defaultValue = "15") int pageSize,
                            Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        // int pageSize = 5; // Số phần tử trên một trang

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
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("users", page.getContent());
        model.addAttribute("role", role);  // Thêm role vào model
        return "user_list";
    }

    @GetMapping("/filter")
    public String filterUsersByRole(@RequestParam(name = "role", required = false) Integer role,
                                    @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "15") int pageSize,
                                    Model model) {
        //  int pageSize = 5;

        Page<Users> page;
        if (role != null) {
            page = userService.findByRole(role, pageNo, pageSize);
            model.addAttribute("role", role);
        } else {
            page = userService.findPaginated(pageNo, pageSize);
        }

        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);
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

    @GetMapping("/statistic")
    public String statistic(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        // Get current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        List<Integer> months = IntStream.rangeClosed(1, currentMonth)
                .boxed()
                .collect(Collectors.toList());

        // Fetch data
        Map<Integer, Double> revenueData = billService.getMonthlyRevenue();
        Map<Integer, Long> onlineOrdersData = billService.getMonthlyOnlineOrders();
        Map<Integer, Long> offlineOrdersData = billService.getMonthlyOfflineOrders();
        List<Double> revenueList = new ArrayList<>(Collections.nCopies(12, 0.0));
        for (Map.Entry<Integer, Double> entry : revenueData.entrySet()) {
            int month = entry.getKey();
            double revenue = entry.getValue();
            revenueList.set(month - 1, revenue);
        }

        // Get values for the current month or default to zero
        Double revenue = revenueData.getOrDefault(currentMonth, 0.0);
        Long onlineOrders = onlineOrdersData.getOrDefault(currentMonth, 0L);
        Long offlineOrders = offlineOrdersData.getOrDefault(currentMonth, 0L);

        // Add to model
        model.addAttribute("revenueList", revenueList);
        model.addAttribute("months", months);
        model.addAttribute("revenue", revenue);
        model.addAttribute("onlineOrders", onlineOrders);
        model.addAttribute("offlineOrders", offlineOrders);
        model.addAttribute("selectedMonth", currentMonth);

        // Fetch total number of products
        long totalProducts = billService.getTotalNumberOfProducts();
        model.addAttribute("totalProducts", totalProducts);

        return "statistic";
    }

    @GetMapping("/statistic/getRevenue")
    public String getRevenue(Model model, Principal principal
            , @RequestParam(name = "month") int month) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        // Get current month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        List<Integer> months = IntStream.rangeClosed(1, currentMonth)
                .boxed()
                .collect(Collectors.toList());

        // Fetch data
        Map<Integer, Double> revenueData = billService.getMonthlyRevenue();
        Map<Integer, Long> onlineOrdersData = billService.getMonthlyOnlineOrders();
        Map<Integer, Long> offlineOrdersData = billService.getMonthlyOfflineOrders();
        List<Double> revenueList = new ArrayList<>(Collections.nCopies(12, 0.0));
        for (Map.Entry<Integer, Double> entry : revenueData.entrySet()) {
            int monthh = entry.getKey();
            double revenue = entry.getValue();
            revenueList.set(monthh - 1, revenue);
        }

        // Get values for the current month or default to zero
        Double revenue = revenueData.getOrDefault(month, 0.0);
        Long onlineOrders = onlineOrdersData.getOrDefault(month, 0L);
        Long offlineOrders = offlineOrdersData.getOrDefault(month, 0L);

        // Add to model
        long totalProducts = billService.getTotalNumberOfProducts();
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("months", months);
        model.addAttribute("revenue", revenue);
        model.addAttribute("onlineOrders", onlineOrders);
        model.addAttribute("offlineOrders", offlineOrders);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("revenueList", revenueList);

        return "statistic";
    }
}
