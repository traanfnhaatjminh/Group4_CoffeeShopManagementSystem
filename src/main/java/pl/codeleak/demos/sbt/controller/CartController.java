package pl.codeleak.demos.sbt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.*;
import pl.codeleak.demos.sbt.service.*;

import javax.servlet.http.HttpSession;
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
            model.addAttribute("user", user);

            // `cartItems` được lấy từ `RedirectAttributes` nếu tồn tại
            if (!model.containsAttribute("cartItems")) {
                List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(user.getUid());
                double totalPrice = cartItemService.calculateTotalPrice(user.getUid());
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalPrice", totalPrice);
            }

            model.addAttribute("currentPage", "cart");
        }
        return "checkout";
    }


    @PostMapping("/cart/add")
    public String addCartItem(@RequestParam("productId") int productId,
                              @RequestParam("quantity") int quantity,
                              @RequestParam("customerId") int customerId) {
        Product product = productService.getProductByPid(productId);
        if (product != null) {
            float totalCost = product.getPrice() * quantity;
            Cart cartItem = new Cart(productId, quantity, totalCost, customerId);
            cartItemService.addCartItem(cartItem);

        }
        return "redirect:/cart";
    }


    @PostMapping("/cart/updateQuantity")
    public String updateQuantity(@RequestParam int productId, @RequestParam int cartItemId, @RequestParam int quantity, Principal principal, Model model) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            int userId = user.getUid();

            // Update the cart item quantity in the database
            Cart cartItem = cartItemService.getCartById(cartItemId).orElseThrow(() -> new RuntimeException("Cart item not found"));
            if (cartItem.getUid() == userId && cartItem.getPid() == productId) {
                cartItemService.updateCartItemQuantity(cartItemId, quantity);
            }
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
                             @RequestParam("paymentMethod") String paymentMethod,
                             Principal principal,
                             Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);

            List<CartItemService.CartItemWithProduct> cartItems = cartItemService.getCartItemsByCustomerId(user.getUid());
            float totalPrice = cartItemService.calculateTotalPrice(user.getUid());
            int status = paymentMethod.equals("cash") ? 0 : 1;
            Bill bill = new Bill(phone, address, new Date(), 0, totalPrice, 0, user.getUid(), status, 0);
            billService.save(bill);

            for (int i = 0; i < cartItems.size(); i++) {
                CartItemService.CartItemWithProduct item = cartItems.get(i);
                BillDetail billDetail = new BillDetail(bill.getBillId(), item.getProduct().getPid(), item.getCartItem().getQuantity(), item.getProduct().getPrice());
                billDetailService.save(billDetail);
                logger.info("BillDetail saved for billId: {}, productId: {}, quantity: {}, price: {}", bill.getBillId(), item.getProduct().getPid(), item.getCartItem().getQuantity(), item.getProduct().getPrice());
            }

            cartItemService.clearCart();
            redirectAttributes.addFlashAttribute("cartItems", cartItems);
            redirectAttributes.addFlashAttribute("totalPrice", totalPrice);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán đơn hàng thành công.");
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("address", address);
            redirectAttributes.addFlashAttribute("paymentMethod", paymentMethod);

        }

        return "redirect:/cart/checkout";
    }

}
