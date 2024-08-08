package pl.codeleak.demos.sbt.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.codeleak.demos.sbt.model.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        productRepository.save(new Product("Product1", "Description1", "Unit1", 10, 100000.0f, "image1.png", 1));
        productRepository.save(new Product("Product2", "Description2", "Unit2", 20, 200000.0f, "image2.png", 1));
        productRepository.save(new Product("Product3", "Description3", "Unit3", 30, 300000.0f, "image3.png", 1));
        productRepository.save(new Product("Product4", "Description4", "Unit4", 40, 400000.0f, "image4.png", 2));
        productRepository.save(new Product("Product5", "Description5", "Unit5", 50, 500000.0f, "image5.png", 2));
    }

    @Test
    void testFindByCategoryId() {
        List<Product> products = (List<Product>) productRepository.findByCategoryId(1);
        assertThat(products).hasSize(3);
        assertThat(products.get(0).getPname()).isEqualTo("Product1");
    }

    @Test
    void testFindByCategoryIdWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> productPage = productRepository.findByCategoryId(1, pageable);
        assertThat(productPage.getContent()).hasSize(2);
        assertThat(productPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void testGetTop3Products() {
        List<Product> topProducts = productRepository.getTop3Products();
        assertThat(topProducts).hasSize(3);
        assertThat(topProducts.get(0).getPname()).isEqualTo("Product5");
    }

    @Test
    void testFindByPnameContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = productRepository.findByPnameContainingIgnoreCase("product", pageable);
        assertThat(productPage.getContent()).hasSize(5);
    }

    @Test
    void testFindByPnameContaining() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = productRepository.findByPnameContaining("Product1", pageable);
        assertThat(productPage.getContent()).hasSize(1);
        assertThat(productPage.getContent().get(0).getPname()).isEqualTo("Product1");
    }
}
