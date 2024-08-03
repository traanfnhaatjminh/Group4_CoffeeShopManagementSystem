package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.CategoryService;
import pl.codeleak.demos.sbt.service.ProductService;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping("/products")
    public String products(Model model,
                           @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                           @RequestParam(required = false) Integer categoryId,
                           @RequestParam(required = false) String keyword,
                           Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        Page<Product> productPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchProducts(keyword, PageRequest.of(pageNo - 1, 10));
        } else if (categoryId != null) {
            productPage = productService.getProductsByCategory(categoryId, PageRequest.of(pageNo - 1, 10));
        } else {
            productPage = productService.getProducts(PageRequest.of(pageNo - 1, 10));
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPage", productPage.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        return "homepage";
    }

    @GetMapping("/products/update/{pid}")
    public String updateProduct(@PathVariable int pid, Model model) {
        Optional<Product> product = productService.getProductById(pid);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "updateproductdemo";
        } else {
            return "redirect:/products";
        }
    }

    @PostMapping("/products/update")
    public String saveUpdatedProduct(@ModelAttribute("product") Product product) {
        Optional<Product> existingProduct = productService.getProductById(product.getPid());
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setPname(product.getPname());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setUnit(product.getUnit());
            updatedProduct.setQuantity(product.getQuantity());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setImage(product.getImage());
            updatedProduct.setCategoryId(product.getCategoryId());
            productService.updateProduct(updatedProduct);
            return "redirect:/products";
        } else {
            return "redirect:/products";
        }
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "addproduct";
    }

    @PostMapping("/products/add")
    public String saveNewProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{pid}")
    public String deleteProduct(@PathVariable int pid) {
        productService.deleteProductById(pid);
        return "redirect:/products";
    }
}
