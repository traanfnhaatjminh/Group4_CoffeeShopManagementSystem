



package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.CategoryService;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

//    @GetMapping
//    public String listCategories(Model model, Principal principal) {
//        if (principal != null) {
//            String username = principal.getName();
//            Users user = userService.findByUsername(username);
//            model.addAttribute("user", user);
//            model.addAttribute("categories", categoryService.getAllCategories());
//        }
//        return "categories";
//    }

    @GetMapping
    public String listCategories(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(name = "keyword", required = false) String keyword,
                                 Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        Page<Category> page;
        if (keyword != null && !keyword.isEmpty()) {
            page = categoryService.searchByName(keyword, pageNo, pageSize);
            model.addAttribute("keyword", keyword);
        } else {
            page = categoryService.findPaginated(pageNo, pageSize);
        }

        model.addAttribute("categories", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("pageSize", pageSize);

        return "categories";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Category added successfully!");
        return "redirect:/categories";
    }

    @PostMapping("/update")
    public String updateCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.update(category);
        redirectAttributes.addFlashAttribute("message", "Category updated successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/delete/{cid}")
    public String deleteCategory(@PathVariable int cid, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(cid);
            redirectAttributes.addFlashAttribute("message", "Category deleted successfully!");
        } catch (DataIntegrityViolationException e) {
            // Xử lý ngoại lệ nếu danh mục còn đang được sử dụng trong bill
            redirectAttributes.addFlashAttribute("error", "Sản phẩm còn được đang sử dụng trong bill");
        }
        return "redirect:/categories";
    }
}
