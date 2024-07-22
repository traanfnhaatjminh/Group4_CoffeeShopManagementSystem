package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.repository.ProductRepository;

import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Iterable<Product> getProductsByCategory(int cid) {
        return productRepository.findByCategoryId(cid);
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }
}
