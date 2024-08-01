package pl.codeleak.demos.sbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Cart;
import pl.codeleak.demos.sbt.repository.CartRepository;

@Service
public class CartItemService {
    @Autowired
    private CartRepository cartRepository;

    public Iterable<Cart> getCartItems() {
        return cartRepository.findAll();
    }

    public void deleteCartItem(int id) {
        cartRepository.deleteById(id);
    }

    public void addCartItem(Cart cartItem) {
        cartRepository.save(cartItem);
    }
}
