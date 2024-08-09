package pl.codeleak.demos.sbt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.codeleak.demos.sbt.model.Category;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.repository.CategoryRepository;
import pl.codeleak.demos.sbt.repository.ProductRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testSaveProduct() {
        Product product = new Product();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.saveProduct(product);

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDeleteProductById() {
        productService.deleteProductById(1);

        verify(productRepository, times(1)).deleteById(1);
    }


    @Test
    void testSaveProductToDB() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.jpg");
        when(file.getBytes()).thenReturn("imageBytes".getBytes());

        Product product = new Product();
        product.setImage(Base64.getEncoder().encodeToString("imageBytes".getBytes()));
        product.setPname("Product");
        product.setDescription("Description");
        product.setUnit("Unit");
        product.setQuantity(10);
        product.setPrice(100.0f);
        product.setCategoryId(1);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.saveProductToDB(file, "Product", "Description", "Unit", 10, 100.0f, 1);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProductsByCategoryId() {
        Product product1 = new Product();
        Product product2 = new Product();
        Iterable<Product> products = List.of(product1, product2);

        when(productRepository.findByCategoryId(anyInt())).thenReturn(products);

        productService.deleteProductsByCategoryId(1);

        verify(productRepository, times(1)).delete(product1);
        verify(productRepository, times(1)).delete(product2);
    }
}
