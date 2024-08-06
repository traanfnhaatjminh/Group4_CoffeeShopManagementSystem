package pl.codeleak.demos.sbt.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Cart;
import pl.codeleak.demos.sbt.model.Product;
import pl.codeleak.demos.sbt.repository.CartRepository;
import pl.codeleak.demos.sbt.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductService productService;

    public List<CartItemWithProduct> getCartItemsByCustomerId(int customerId) {
        Iterable<Cart> cartItems = cartRepository.findByUid(customerId);
        List<CartItemWithProduct> cartItemWithProducts = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            Product product = productService.getProductByPid(cartItem.getPid());
            cartItemWithProducts.add(new CartItemWithProduct(cartItem, product));
        }
        return cartItemWithProducts;
    }

    public void deleteCartItem(int id) {
        cartRepository.deleteById(id);
    }

    public Optional<Cart> getCartById(int id) {
        return cartRepository.findById(id);
    }

    public void addCartItem(Cart cartItem) {
        cartRepository.save(cartItem);
    }

    public void clearCart() {
        cartRepository.deleteAll();
    }

    public float calculateTotalPrice(int customerId) {
        List<CartItemWithProduct> cartItems = getCartItemsByCustomerId(customerId);
        float total = 0.0F;
        for (CartItemWithProduct cartItemWithProduct : cartItems) {
            total += cartItemWithProduct.getProduct().getPrice() * cartItemWithProduct.getCartItem().getQuantity();
        }
        return total;
    }

    @Getter
    public static class CartItemWithProduct {
        private Cart cartItem;
        private Product product;

        public CartItemWithProduct(Cart cartItem, Product product) {
            this.cartItem = cartItem;
            this.product = product;
        }

    }
}

