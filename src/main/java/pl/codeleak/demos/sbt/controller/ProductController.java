package pl.codeleak.demos.sbt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.repository.ProductRepository;
import pl.codeleak.demos.sbt.service.CategoryService;
import pl.codeleak.demos.sbt.service.ProductService;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String products(Model model) {
        Iterable<Product> listP = productService.getAllProducts();
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("products", listP);
        model.addAttribute("categories", listC);
        return "homepage";
    }

    @GetMapping("/products/{cid}")
    public String productByCategory(@PathVariable int cid, Model model) {
        Iterable<Product> listP = productService.getProductsByCategory(cid);
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("products", listP);
        model.addAttribute("categories", listC);
        return "homepage";
    }

    @GetMapping("/homepage")
    public String homepage(Model model) {
        List<Product> listP = productService.getLastestProducts();
        Iterable<Category> listC = categoryService.getAllCategories();
        model.addAttribute("categories", listC);
        model.addAttribute("products", listP);
        return "home";
    }

    @GetMapping("/products/update/{pid}")
    public String updateProduct(@PathVariable int pid, Model model) {
        Optional<Product> product = productService.getProductById(pid);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "updateproductdemo";
        } else {
            // Handle the case when the product is not found
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
            // Handle the case when the product is not found
            return "redirect:/products";
        }
    }

}
