package pl.codeleak.demos.sbt.service;

import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Product;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private List<Product> cartItems = new ArrayList<>();

    public void addToCart(Product product) {
        cartItems.add(product);
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public float getTotalPrice() {
        return (float) cartItems.stream().mapToDouble(Product::getPrice).sum();
    }

    public void setQuantity(Product product) {
        product.setQuantity(1);
    }
}
