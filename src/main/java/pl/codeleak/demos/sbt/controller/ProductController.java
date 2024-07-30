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
import java.util.List;
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
    public String products(Model model) {
        Iterable<Product> listP = productService.getAllProducts();
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("products", listP);
        model.addAttribute("categories", listC);
       // model.addAttribute("category", new Category());
        return "homepage";
    }



    @GetMapping("/categories/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "addcategory";
    }

    @PostMapping("/categories/add")
    public String saveNewCategory(@ModelAttribute("category") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/products";
    }

    @GetMapping("/products/{cid}")
    public String productByCategory(@PathVariable int cid, Model model) {
        Iterable<Product> listP = productService.getProductsByCategory(cid);
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("products", listP);
        model.addAttribute("categories", listC);
        model.addAttribute("selectedCategoryId", cid);
        return "homepage";
    }

    @GetMapping("/homepage")
    public String homepage(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        List<Product> listP = productService.getLastestProducts();
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("categories", listC);
        model.addAttribute("products", listP);
        model.addAttribute("currentPage", "home");
        return "home";
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

    @GetMapping("/menu")
    public String showMenu(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) Integer categoryId,
                           @RequestParam(required = false) String search,
                           Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        Page<Product> productPage;
        if (search != null && !search.trim().isEmpty()) {
            productPage = productService.searchProducts(search, PageRequest.of(page, 5));
        } else if (categoryId != null) {
            productPage = productService.getProductByCategories(page, 5, categoryId);
        } else {
            productPage = productService.getProducts(page, 5);
        }
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("searchQuery", search);
        model.addAttribute("currentPage1", "menu");
        return "menu";
    }

    @GetMapping("/product/{pid}")
    public String viewProductDetails(@PathVariable("pid") Integer pid, Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        Product product = productService.getProductByPid(pid);
        if (product == null) {
            return "error/404";
        }
        model.addAttribute("product", product);
        model.addAttribute("categoryName", product.getCategoryName());
        model.addAttribute("currentPage", "menu");
        return "productdetail";
    }

    @GetMapping("/products/delete/{pid}")
    public String deleteProduct(@PathVariable int pid) {
        productService.deleteProductById(pid);
        return "redirect:/products";
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

}
