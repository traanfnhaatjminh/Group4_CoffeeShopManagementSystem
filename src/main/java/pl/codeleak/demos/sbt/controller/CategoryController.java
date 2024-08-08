////package pl.codeleak.demos.sbt.controller;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Controller;
////import org.springframework.ui.Model;
////import org.springframework.web.bind.annotation.*;
////import pl.codeleak.demos.sbt.model.Category;
////import pl.codeleak.demos.sbt.service.CategoryService;
////
////import java.util.Optional;
////
////@Controller
////public class CategoryController {
////
////    @Autowired
////    private CategoryService categoryService;
////
////    @GetMapping("/categories")
////    public String categories(Model model) {
////        Iterable<Category> listC = categoryService.getAllCategories();
////        model.addAttribute("categories", listC);
////        return "categories";
////    }
////
////    @GetMapping("/categories/add")
////    public String addCategoryForm(Model model) {
////        model.addAttribute("category", new Category());
////        return "addcategory";
////    }
////
////    @PostMapping("/categories/add")
////    public String saveNewCategory(@ModelAttribute("category") Category category) {
////        categoryService.saveCategory(category);
////        return "redirect:/categories";
////    }
////
////    @GetMapping("/categories/{cid}")
////    @ResponseBody
////    public Category getCategoryById(@PathVariable int cid) {
////        return categoryService.getCategoryById(cid);
////    }
////
////    @PostMapping("/categories/update")
////    public String saveUpdatedCategory(@ModelAttribute("category") Category category) {
////        Category existingCategory = categoryService.getCategoryById(category.getCid());
////        if (existingCategory != null) {
////            existingCategory.setGroupName(category.getGroupName());
////            existingCategory.setCategoryName(category.getCategoryName());
////            existingCategory.setDescribe(category.getDescribe());
////            categoryService.updateCategory(existingCategory);
////            return "redirect:/categories";
////        } else {
////            return "redirect:/categories";
////        }
////    }
////
////    @GetMapping("/categories/delete/{cid}")
////    public String deleteCategory(@PathVariable int cid) {
////        categoryService.deleteCategoryById(cid);
////        return "redirect:/categories";
////    }
////}
////
////
//
//package pl.codeleak.demos.sbt.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import pl.codeleak.demos.sbt.model.Category;
//import pl.codeleak.demos.sbt.service.CategoryService;
//import pl.codeleak.demos.sbt.service.UserService;
//
//
//@Controller
//@RequestMapping("/categories")
//public class CategoryController {
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping
//    public String categories(Model model) {
//        Iterable<Category> listC = categoryService.getAllCategories();
//        String username = userService.getCurrentUsername();
//        model.addAttribute("categories", listC);
//        model.addAttribute("username", username);
//        return "categories";
//    }
//
//    @GetMapping("/add")
//    public String addCategoryForm(Model model) {
//        model.addAttribute("category", new Category());
//        return "addcategory";
//    }
//
//    @PostMapping("/add")
//    public String saveNewCategory(@ModelAttribute("category") Category category, RedirectAttributes redirectAttributes) {
//        categoryService.saveCategory(category);
//        redirectAttributes.addFlashAttribute("message", "Category Added Successfully");
//        return "redirect:/categories";
//    }
//
//    @GetMapping("/{cid}")
//    @ResponseBody
//    public Category getCategoryById(@PathVariable int cid) {
//        return categoryService.getCategoryById(cid);
//    }
//
//    @PostMapping("/update")
//    public String saveUpdatedCategory(@ModelAttribute("category") Category category, RedirectAttributes redirectAttributes) {
//        Category existingCategory = categoryService.getCategoryById(category.getCid());
//        if (existingCategory != null) {
//            existingCategory.setGroupName(category.getGroupName());
//            existingCategory.setCategoryName(category.getCategoryName());
//            existingCategory.setDescribe(category.getDescribe());
//            categoryService.updateCategory(existingCategory);
//            redirectAttributes.addFlashAttribute("message", "Update Successful");
//            return "redirect:/categories";
//        } else {
//            redirectAttributes.addFlashAttribute("message", "Update Failed: Category not found");
//            return "redirect:/categories";
//        }
//    }
//
//    @GetMapping("/delete/{cid}")
//    public String deleteCategory(@PathVariable int cid, RedirectAttributes redirectAttributes) {
//        categoryService.deleteCategoryById(cid);
//        redirectAttributes.addFlashAttribute("message", "Category Deleted Successfully");
//        return "redirect:/categories";
//    }
//
//
//
//}



package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public String listCategories(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
            model.addAttribute("categories", categoryService.getAllCategories());
        }
        return "categories"; // Đảm bảo tên trang phù hợp với tệp HTML
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
        categoryService.deleteById(cid);
        redirectAttributes.addFlashAttribute("message", "Category deleted successfully!");
        return "redirect:/categories";
    }
}
