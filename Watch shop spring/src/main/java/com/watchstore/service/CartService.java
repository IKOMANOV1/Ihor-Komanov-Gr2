package com.watchstore.service;

import com.watchstore.model.Cart;
import com.watchstore.model.CartItem;
import com.watchstore.model.User;
import com.watchstore.model.Watch;
import com.watchstore.repository.CartRepository;
import com.watchstore.repository.UserRepository;
import com.watchstore.repository.WatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final WatchRepository watchRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, WatchRepository watchRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.watchRepository = watchRepository;
    }

    @Transactional
    public Cart getCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public void addToCart(String username, Long watchId, int amount) {
        Cart cart = getCart(username);
        Watch watch = watchRepository.findById(watchId)
                .orElseThrow(() -> new RuntimeException("Watch not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getWatch().getId().equals(watchId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + amount;
            if (newQuantity > watch.getInStock()) {
                throw new RuntimeException("Cannot add. Only " + watch.getInStock() + " items in stock.");
            }
            item.setQuantity(newQuantity);
        } else {
            if (amount > watch.getInStock()) {
                throw new RuntimeException("Cannot add. Only " + watch.getInStock() + " items in stock.");
            }
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setWatch(watch);
            newItem.setQuantity(amount);
            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(String username, Long watchId) {
        Cart cart = getCart(username);
        cart.getItems().removeIf(item -> item.getWatch().getId().equals(watchId));
        cartRepository.save(cart);
    }

    @Transactional
    public void updateItemQuantity(String username, Long watchId, int newQuantity) {
        Cart cart = getCart(username);
        cart.getItems().stream()
                .filter(item -> item.getWatch().getId().equals(watchId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(newQuantity);
                });
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String username) {
        Cart cart = getCart(username);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
