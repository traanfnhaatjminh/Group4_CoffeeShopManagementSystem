package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.codeleak.demos.sbt.model.Cart;
import pl.codeleak.demos.sbt.service.*;

import java.security.Principal;

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
            model.addAttribute("username", username);
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

}
