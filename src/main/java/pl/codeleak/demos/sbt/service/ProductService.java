//package pl.codeleak.demos.sbt.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import pl.codeleak.demos.sbt.model.Product;
//import pl.codeleak.demos.sbt.repository.CategoryRepository;
//import pl.codeleak.demos.sbt.repository.ProductRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ProductService {
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//
//    public Optional<Product> getProductById(int id) {
//        return productRepository.findById(id);
//    }
//
//    public Iterable<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    public Iterable<Product> getProductsByCategory(int cid) {
//        return productRepository.findByCategoryId(cid);
//    }
//
//    public void updateProduct(Product product) {
//        productRepository.save(product);
//    }
//
//    public List<Product> getLastestProducts() {
//        return productRepository.getTop3Products();
//    }
//    public Page<Product> getProducts(int page, int size){
//        return productRepository.findAll(PageRequest.of(page, size));
//    }
//    public  Page<Product> getProductByCategories(int page, int size, int categoryId){
//        return productRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
//    }
//    //Product Detail
//    public Product getProductByPid(Integer pid){
//        return productRepository.findById(pid).orElse(null);
////        return repo.findById(pid).get();
//    }
//}


package pl.codeleak.demos.sbt.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.repository.CategoryRepository;
import pl.codeleak.demos.sbt.repository.ProductRepository;

import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public void deleteProductById(int id) {
        productRepository.deleteById(id);
    }
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> getProductById(int id) {
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(this::setCategoryName);
        return product;
    }

    public Iterable<Product> getAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        products.forEach(this::setCategoryName);
        return products;
    }

    public Iterable<Product> getProductsByCategory(int cid) {
        Iterable<Product> products = productRepository.findByCategoryId(cid);
        products.forEach(this::setCategoryName);
        return products;
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    public List<Product> getLastestProducts() {
        List<Product> products = productRepository.getTop3Products();
        products.forEach(this::setCategoryName);
        return products;
    }

    public Page<Product> getProducts(int page, int size) {
        Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size));
        productPage.forEach(this::setCategoryName);
        return productPage;
    }

    public Page<Product> getProductByCategories(int page, int size, int categoryId) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
        productPage.forEach(this::setCategoryName);
        return productPage;
    }

    public Product getProductByPid(Integer pid) {
        Product product = productRepository.findById(pid).orElse(null);
        if (product != null) {
            setCategoryName(product);
        }
        return product;
    }

    private void setCategoryName(Product product) {
        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        if (category != null) {
            product.setCategoryName(category.getCategoryName());
        } else {
            product.setCategoryName("Unknown");
        }
    }

    public Page<Product> getProductsManagement(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }
    public Page<Product> searchProducts(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.findByPnameContainingIgnoreCase(query, pageable);
        productPage.forEach(this::setCategoryName);
        return productPage;
    }

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void deleteProductsByCategoryId(int categoryId) {
        Iterable<Product> products = productRepository.findByCategoryId(categoryId);
        for (Product product : products) {
            productRepository.delete(product);
        }
    }

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    public Page<Product> getProductsByCategory(int categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    public Page<Product> searchProducts2(String keyword, Pageable pageable) {
        return productRepository.findByPnameContaining(keyword, pageable);
    }
    public void updateProductWithImage(MultipartFile file, Product product) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new ValidationException("Not a valid file");
            }
            product.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        }

        validateProductInput(product.getPname(), product.getUnit(), product.getQuantity(), product.getPrice(), product.getCategoryId());

        productRepository.save(product);
    }

    public void  saveProductToDB(MultipartFile file,String name,String description,String unit, int quantity
            ,float price, int categoryId)
    {

        validateProductInput(name, unit, quantity, price, categoryId);
        Product p = new Product();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if(fileName.contains(".."))
        {
            System.out.println("not a a valid file");
        }



        try {
            p.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.setDescription(description);

        p.setPname(name);
        p.setDescription(description);
        p.setUnit(unit);
        p.setQuantity(quantity);
        p.setPrice(price);
        p.setCategoryId(categoryId);



        productRepository.save(p);
    }
    public List<Product> getAllProduct()
    {
        return productRepository.findAll();
    }


    private void validateProductInput(String name, String unit, int quantity, float price, int categoryId) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Tên sản phẩm không được để trống");
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new ValidationException("Đơn vị không được để trống");
        }
        if (quantity < 0) {
            throw new ValidationException("Số lượng phải là số nguyên không âm");
        }
        if (price < 0) {
            throw new ValidationException("Giá phải là số không âm");
        }
        if (categoryId <= 0) {
            throw new ValidationException("Danh mục không được để trống");
        }
    }

}
