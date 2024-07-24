package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.service.*;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Controller
public class ManagementController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BillService billService;

    @Autowired
    private BillDetailService billDetailService;

    @Autowired
    private UserService userService;

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
            cartService.setQuantity(productOptional.get());
        } else {
            // Handle the case where the product is not found
            model.addAttribute("error", "Product not found");
        }
        return "redirect:/management";
    }

    @PostMapping("/createBill")
    public String createBill(@RequestParam("customerPhone") String customerPhone,
                             @RequestParam("customerName") String customerName,
                             @RequestParam("numberOfGuest") int numberOfGuest,
                             @RequestParam("tableId") int tableId,
                             Principal principal,
                             Model model) {
        // Get the logged-in user's ID
        String username = principal.getName();
        int userId = userService.findByUsername(username).getId();

        // Create a new Bill
        Bill bill = new Bill(new Date(), numberOfGuest, cartService.getTotalPrice(), tableId, userId);
        billService.save(bill);

        // Create BillDetail entries for each cart item
        cartService.getCartItems().forEach(item -> {
            BillDetail billDetail = new BillDetail(bill.getBillId(), item.getPid(), item.getQuantity(), item.getPrice());
            billDetailService.save(billDetail);
        });

        // Clear the cart
        cartService.clearCart();

        model.addAttribute("successMessage", "Bill created successfully!");
        return "redirect:/management";
    }
}
