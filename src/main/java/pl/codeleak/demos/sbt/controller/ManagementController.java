package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.service.CartService;
import pl.codeleak.demos.sbt.service.CategoryService;
import pl.codeleak.demos.sbt.service.ProductService;

import java.util.Optional;

@Controller
public class ManagementController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @GetMapping("/management")
    public String management(Model model) {
        Iterable<Product> listP = productService.getAllProducts();
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("categories", listC);
        model.addAttribute("products", listP);
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "management";
    }

    @GetMapping("/management/products/{cid}")
    public String productByCategory(@PathVariable int cid, Model model) {
        Iterable<Product> listP = productService.getProductsByCategory(cid);
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("products", listP);
        model.addAttribute("categories", listC);
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "management";
    }

    @GetMapping("/management/products/{pid}/addtocart")
    public String addToCart(@PathVariable int pid, Model model) {
        Optional<Product> productOptional = productService.getProductById(pid);
        if (productOptional.isPresent()) {
            cartService.addToCart(productOptional.get());
        } else {
            // Handle the case where the product is not found
            model.addAttribute("error", "Product not found");
        }
        return "redirect:/management";
    }

    @GetMapping("/management/createbill")
    public String createBill(Model model) {
        // Implement the logic for creating a bill here
        cartService.clearCart();
        return "redirect:/management";
    }
}
