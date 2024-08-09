package pl.codeleak.demos.sbt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.codeleak.demos.sbt.model.Cart;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.service.CartItemService;
import pl.codeleak.demos.sbt.service.CartItemService.CartItemWithProduct;
import pl.codeleak.demos.sbt.repository.CartRepository;
import pl.codeleak.demos.sbt.service.ProductService;

import java.util.Arrays;
import java.util.Optional;

public class CartItemServiceTest {

    private CartItemService cartItemService;
    private CartRepository cartRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        productService = mock(ProductService.class);
        cartItemService = new CartItemService();
        cartItemService.setCartRepository(cartRepository); // Assuming setter is available
        cartItemService.setProductService(productService); // Assuming setter is available
    }

    @Test
    void testGetCartItemsByCustomerId() {
        int customerId = 1;
        Cart cartItem1 = new Cart(1, 2, 20.0f, customerId);
        Cart cartItem2 = new Cart(2, 3, 30.0f, customerId);
        Product product1 = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        Product product2 = new Product("Product2", "Description2", "Unit2", 5, 20.0f, "image.jpg", 2);
        product1.setPid(1);
        product2.setPid(2);

        when(cartRepository.findByUid(customerId)).thenReturn(Arrays.asList(cartItem1, cartItem2));
        when(productService.getProductByPid(cartItem1.getPid())).thenReturn(product1);
        when(productService.getProductByPid(cartItem2.getPid())).thenReturn(product2);

        // Testing getCartItemsByCustomerId
        var result = cartItemService.getCartItemsByCustomerId(customerId);

        assertEquals(2, result.size());
        assertEquals(product1, result.get(0).getProduct());
        assertEquals(cartItem1, result.get(0).getCartItem());
        assertEquals(product2, result.get(1).getProduct());
        assertEquals(cartItem2, result.get(1).getCartItem());
    }

    @Test
    void testCalculateTotalPrice() {
        int customerId = 1;
        Cart cartItem1 = new Cart(1, 2, 20.0f, customerId);
        Cart cartItem2 = new Cart(2, 3, 30.0f, customerId);
        Product product1 = new Product("Product1", "Description1", "Unit1", 10, 10.0f, "image.jpg", 1);
        Product product2 = new Product("Product2", "Description2", "Unit2", 5, 20.0f, "image.jpg", 2);
        product1.setPid(1);
        product2.setPid(2);

        when(cartRepository.findByUid(customerId)).thenReturn(Arrays.asList(cartItem1, cartItem2));
        when(productService.getProductByPid(cartItem1.getPid())).thenReturn(product1);
        when(productService.getProductByPid(cartItem2.getPid())).thenReturn(product2);

        // Calculate total price
        float totalPrice = cartItemService.calculateTotalPrice(customerId);

        // Assert total price calculation
        assertEquals((cartItem1.getQuantity() * product1.getPrice()) + (cartItem2.getQuantity() * product2.getPrice()), totalPrice, 0.01f);
    }

    @Test
    void testAddCartItem() {
        Cart newCartItem = new Cart(1, 5, 50.0f, 1);
        Product product = new Product("Product", "Description", "Unit", 10, 10.0f, "image.jpg", 1);
        product.setPid(1);

        when(productService.getProductByPid(newCartItem.getPid())).thenReturn(product);
        when(cartRepository.findByPidAndUid(newCartItem.getPid(), newCartItem.getUid())).thenReturn(Optional.empty());

        cartItemService.addCartItem(newCartItem);

        verify(cartRepository, times(1)).save(newCartItem);
    }

    @Test
    void testUpdateCartItemQuantity() {
        int cartItemId = 1;
        int newQuantity = 10;
        Cart existingCartItem = new Cart(1, 5, 50.0f, 1);
        Product product = new Product("Product", "Description", "Unit", 10, 10.0f, "image.jpg", 1);
        product.setPid(1);

        when(cartRepository.findById(cartItemId)).thenReturn(Optional.of(existingCartItem));
        when(productService.getProductByPid(existingCartItem.getPid())).thenReturn(product);

        cartItemService.updateCartItemQuantity(cartItemId, newQuantity);

        verify(cartRepository, times(1)).save(existingCartItem);
        assertEquals(newQuantity, existingCartItem.getQuantity());
        assertEquals(newQuantity * product.getPrice(), existingCartItem.getTotalCost());
    }

    @Test
    void testDeleteCartItem() {
        int cartItemId = 1;

        cartItemService.deleteCartItem(cartItemId);

        verify(cartRepository, times(1)).deleteById(cartItemId);
    }

    @Test
    void testClearCart() {
        cartItemService.clearCart();

        verify(cartRepository, times(1)).deleteAll();
    }
}
