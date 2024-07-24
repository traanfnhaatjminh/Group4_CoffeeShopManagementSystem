package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.repository.ProductRepository;

import java.util.List;
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

    public List<Product> getLastestProducts() {
        return productRepository.getTop3Products();
    }
    public Page<Product> getProducts(int page, int size){
        return productRepository.findAll(PageRequest.of(page, size));
    }
    public  Page<Product> getProductByCategories(int page, int size, int categoryId){
        return productRepository.findByCategoryId(categoryId, PageRequest.of(page, size));
    }
    //Product Detail
    public Product getProductByPid(Integer pid){
        return productRepository.findById(pid).orElse(null);
//        return repo.findById(pid).get();
    }
}
