package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.CartItemService;
import pl.codeleak.demos.sbt.service.CategoryService;
import pl.codeleak.demos.sbt.service.ProductService;
import pl.codeleak.demos.sbt.service.UserService;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
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

    @Autowired
    private CartItemService cartItemService;

    @Value("${file.upload-dir}")
    private String uploadDir;

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
            productPage = productService.searchProducts2(keyword, PageRequest.of(pageNo - 1, 10));
        } else if (categoryId != null) {
            productPage = productService.getProductsByCategory(categoryId, PageRequest.of(pageNo - 1, 10));
        } else {
            productPage = productService.getProducts(PageRequest.of(pageNo - 1, 10));
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPage", productPage.getTotalPages());  // Đảm bảo rằng bạn đã thêm thuộc tính này
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        return "homepage";
    }

    @GetMapping("/homepage")
    public String homepage(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            int userId = user.getUid();
            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
            model.addAttribute("cartItems", cartItems);
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

//    @PostMapping("/products/update")
//    public String saveUpdatedProduct(@ModelAttribute("product") Product product) {
//        Optional<Product> existingProduct = productService.getProductById(product.getPid());
//        if (existingProduct.isPresent()) {
//            Product updatedProduct = existingProduct.get();
//            updatedProduct.setPname(product.getPname());
//            updatedProduct.setDescription(product.getDescription());
//            updatedProduct.setUnit(product.getUnit());
//            updatedProduct.setQuantity(product.getQuantity());
//            updatedProduct.setPrice(product.getPrice());
//            updatedProduct.setImage(product.getImage());
//            updatedProduct.setCategoryId(product.getCategoryId());
//            productService.updateProduct(updatedProduct);
//            return "redirect:/products";
//        } else {
//            return "redirect:/products";
//        }
//    }


    @PostMapping("/products/update")
    public String saveUpdatedProduct(@RequestParam("pid") int pid,
                                     @RequestParam("pname") String pname,
                                     @RequestParam("description") String description,
                                     @RequestParam("unit") String unit,
                                     @RequestParam("quantity") int quantity,
                                     @RequestParam("price") float price,
                                     @RequestParam("categoryId") int categoryId,
                                     @RequestParam("file") MultipartFile file,
                                     RedirectAttributes redirectAttributes) {
        Optional<Product> existingProduct = productService.getProductById(pid);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setPname(pname);
            updatedProduct.setDescription(description);
            updatedProduct.setUnit(unit);
            updatedProduct.setQuantity(quantity);
            updatedProduct.setPrice(price);
            updatedProduct.setCategoryId(categoryId);

            if (!file.isEmpty()) {
                try {
                    updatedProduct.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "Failed to upload image");
                    return "redirect:/products";
                }
            }
            productService.updateProduct(updatedProduct);
          //  productService.saveProductToDB(file, pname, description, unit, quantity, price, categoryId);
            redirectAttributes.addFlashAttribute("message", "Product updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("message", "Product not found");
        }
        return "redirect:/products";
    }



    @PostMapping("/products/add")
    public String saveNewProduct(//@ModelAttribute("product") Product product,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam("pname") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("unit") String unit,
                                 @RequestParam("quantity") int quantity,
                                 @RequestParam("price") float price,
                                 @RequestParam("categoryId") int categoryId,
                                 RedirectAttributes redirectAttributes
    ) {

        //   productService.saveProduct(product);
        productService.saveProductToDB(file, name, description, unit, quantity, price, categoryId);
        redirectAttributes.addFlashAttribute("message", "Product added successfully");

        return "redirect:/products";
    }



    @GetMapping("/menu")
    public String showMenu(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) Integer categoryId,
                           @RequestParam(required = false) String search,
                           Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            int userId = user.getUid();
            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
            model.addAttribute("cartItems", cartItems);
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
            Users user = userService.getUserByUsername(username);
            int userId = user.getUid();
            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("user", user);
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

//    @PostMapping("/products/add")
//    public String saveNewProduct(@ModelAttribute("product") Product product) {
//        productService.saveProduct(product);
//        return "redirect:/products";
//    }



}
