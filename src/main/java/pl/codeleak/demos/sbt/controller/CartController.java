package pl.codeleak.demos.sbt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.codeleak.demos.sbt.model.*;
import pl.codeleak.demos.sbt.service.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private BillService billService;

    @Autowired
    private BillDetailService billDetailService;

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
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("currentPage", "cart");
        }
        return "shoppingcart";
    }

    @GetMapping("/cart/checkout")
    public String checkout(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            int userId = user.getUid();
            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(userId);
            double totalPrice = cartItemService.calculateTotalPrice(userId);  // Tính tổng tiền
            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("currentPage", "cart");
        }
        return "checkout";
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
    public String updateQuantity(@RequestParam int productId, @RequestParam int cartItemId, @RequestParam int quantity, Principal principal, Model model) {
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
            model.addAttribute("cartItems", cartItem);
        }
        return "redirect:/cart"; // Redirect back to the cart page
    }

    @PostMapping("/cart/delete")
    public String deleteCartItem(@RequestParam("cartItemId") int cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return "redirect:/cart"; // Redirect to the cart page after deletion
    }

    @PostMapping("/cart/checkout")
    public String createBill(@RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             Principal principal,
                             Model model) {

        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);

            // Get cart items and calculate total cost
            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(user.getUid());
            float totalPrice = cartItemService.calculateTotalPrice(user.getUid());
            // Create a new Bill
            Bill bill = new Bill(phone, address, new Date(), 0, totalPrice, 0, user.getUid(), 0, 0);
            billService.save(bill);

            // Create BillDetail entries for each cart item
            for (int i = 0; i < cartItems.size(); i++) {
                CartItemService.CartItemWithProduct item = cartItems.get(i);
                BillDetail billDetail = new BillDetail(bill.getBillId(), item.getProduct().getPid(), item.getCartItem().getQuantity(), item.getProduct().getPrice());
                billDetailService.save(billDetail);
                logger.info("BillDetail saved for billId: {}, productId: {}, quantity: {}, price: {}", bill.getBillId(), item.getProduct().getPid(), item.getCartItem().getQuantity(), item.getProduct().getPrice());
            }

            // Clear the cart
            cartItemService.clearCart();
            logger.info("Cart cleared");
            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("currentPage", "cart");
            model.addAttribute("successMessage", "Thanh toán đơn hàng thành công.");
        }

        return "checkout";
    }

}
