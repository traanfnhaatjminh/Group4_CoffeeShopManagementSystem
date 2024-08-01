package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.codeleak.demos.sbt.model.Cart;
import pl.codeleak.demos.sbt.model.CartItem;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.*;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            int userId = user.getUid();
            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
            double totalPrice = cartItemService.calculateTotalPrice(userId);  // Tính tổng tiền
            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);  // Thêm tổng tiền vào model
        }
        return "cart";
    }


    @PostMapping("/cart/add")
    public String addCartItem(@RequestParam("productId") int productId,
                              @RequestParam("quantity") int quantity,
                              @RequestParam("customerId") int customerId) {
        float totalCost = productService.getProductById(productId).get().getPrice() * quantity;
        Cart cartItem = new Cart(productId, quantity, totalCost, customerId);
        cartItemService.addCartItem(cartItem);
        return "redirect:/cart";
    }

    @PostMapping("/cart/updateQuantity")
    public String updateQuantity(@RequestParam int productId, @RequestParam int cartItemId, @RequestParam int quantity, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            int userId = user.getUid();

            // Update the cart item in the database
            Cart cartItem = cartItemService.getCartById(cartItemId).orElseThrow(() -> new RuntimeException("Cart item not found"));
            if (cartItem.getUid() == userId && cartItem.getPid() == productId) {
                cartItem.setQuantity(quantity);
                cartItemService.addCartItem(cartItem);
            }
        }
        return "redirect:/cart"; // Redirect back to the cart page
    }


}
