package pl.codeleak.demos.sbt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.codeleak.demos.sbt.model.CartItem;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.service.CartService;

import java.util.List;

public class CartServiceTest {

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService();
    }

    @Test
    void testAddToCart_NewItem() {
        Product product = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        product.setPid(1);

        cartService.addToCart(product, 5);

        List<CartItem> cartItems = cartService.getCartItems();
        assertEquals(1, cartItems.size());
        assertEquals(1, cartItems.get(0).getProduct().getPid());
        assertEquals(5, cartItems.get(0).getQuantity());
    }

    @Test
    void testAddToCart_ExistingItem() {
        Product product = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        product.setPid(1);
        cartService.addToCart(product, 5);

        Product product2 = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        product2.setPid(1);
        cartService.addToCart(product2, 3);

        List<CartItem> cartItems = cartService.getCartItems();
        assertEquals(1, cartItems.size());
        assertEquals(1, cartItems.get(0).getProduct().getPid());
        assertEquals(8, cartItems.get(0).getQuantity());
    }

    @Test
    void testClearCart() {
        Product product = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        product.setPid(1);
        cartService.addToCart(product, 5);

        cartService.clearCart();

        List<CartItem> cartItems = cartService.getCartItems();
        assertTrue(cartItems.isEmpty());
    }

    @Test
    void testGetTotalPrice() {
        Product product1 = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        product1.setPid(1);
        Product product2 = new Product("Product2", "Description2", "Unit2", 5, 20.0f, "image.jpg", 2);
        product2.setPid(2);

        cartService.addToCart(product1, 2);
        cartService.addToCart(product2, 3);

        float totalPrice = cartService.getTotalPrice();
        assertEquals(2 * 10.0f + 3 * 20.0f, totalPrice, 0.01);
    }

    @Test
    void testSetQuantity() {
        Product product = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        cartService.setQuantity(product);

        assertEquals(1, product.getQuantity());
    }
}
