package pl.codeleak.demos.sbt.controller;

import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.codeleak.demos.sbt.model.*;
import pl.codeleak.demos.sbt.service.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ManagementController.class);

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

    @Autowired
    private TableService tableService;

    @GetMapping("/management")
    public String management(@RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Product> productPage = productService.getProducts(currentPage - 1, pageSize);

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("tables", tableService.getAllTables());
        return "management";
    }

    @GetMapping("/management/products/{cid}")
    public String productByCategory(@PathVariable int cid, Model model,
                                    @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size,
                                    Principal principal) {

        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        int currentPage = page.orElse(1);

        List<Integer> pageNumbers = IntStream.rangeClosed(1, 1)
                .boxed()
                .collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);

        Iterable<Product> listP = productService.getProductsByCategory(cid);
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("products", listP);
        model.addAttribute("categories", listC);
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("tables", tableService.getAllTables());
        return "management";
    }

    @GetMapping("/management/products/{pid}/addtocart")
    public String addToCart(@PathVariable int pid, @RequestParam(value = "quantity", defaultValue = "1") int quantity, Model model) {
        Optional<Product> productOptional = productService.getProductById(pid);
        if (productOptional.isPresent()) {
            cartService.addToCart(productOptional.get(), quantity);
        } else {
            model.addAttribute("error", "Product not found");
        }
        return "redirect:/management";
    }

    @PostMapping("/createBill")
    public String createBill(@RequestParam("numberOfGuest") int numberOfGuest,
                             @RequestParam("tableId") int tableId,
                             Principal principal,
                             Model model,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size) {

        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Product> productPage = productService.getProducts(currentPage - 1, pageSize);

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        logger.info("createBill called with numberOfGuest: {}, tableId: {}", numberOfGuest, tableId);

        // Get the logged-in user's ID
        String username = principal.getName();
        int userId = userService.getUserByUsername(username).getUid();

        // Get cart items and calculate total cost
        List<CartItem> cartItems = cartService.getCartItems();
        float totalCost = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            totalCost += item.getProduct().getPrice() * item.getQuantity();
        }

        // Create a new Bill
        Bill bill = new Bill("","",new Date(), numberOfGuest, totalCost, tableId, userId, 0, 1);
        billService.save(bill);
        tableService.updateTableStatus(tableId,1);
        logger.info("Bill saved with ID: {}", bill.getBillId());

        // Create BillDetail entries for each cart item
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            BillDetail billDetail = new BillDetail(bill.getBillId(), item.getProduct().getPid(), item.getQuantity(), item.getProduct().getPrice());
            billDetailService.save(billDetail);
            logger.info("BillDetail saved for billId: {}, productId: {}, quantity: {}, price: {}", bill.getBillId(), item.getProduct().getPid(), item.getQuantity(), item.getProduct().getPrice());
        }

        // Clear the cart
        cartService.clearCart();
        logger.info("Cart cleared");

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("successMessage", "Bill created successfully!");
        return "management";
    }

    @GetMapping("/management/allbill")
    public String allBill(@RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size,
                          @RequestParam(value = "createdTime", required = false) String createdTime,
                          Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Bill> billsPage;
        boolean noBills = false;
        if (createdTime != null && !createdTime.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(createdTime, formatter);
            Date dateValue = java.sql.Date.valueOf(date);
            billsPage = billService.searchByCreatedTime(dateValue, pageable);
            noBills = billsPage.isEmpty();
        }else{
            billsPage = billService.getAllBills(pageable);
        }
//        Iterable<Bill> listBill = billService.getAllBills();
        model.addAttribute("bills", billsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", billsPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("createdTime", createdTime);
        model.addAttribute("noBills", noBills);
        return "allbill-cashier";
    }

}
