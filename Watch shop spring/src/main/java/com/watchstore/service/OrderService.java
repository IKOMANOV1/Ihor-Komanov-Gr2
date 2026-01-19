package com.watchstore.service;

import com.watchstore.model.*;
import com.watchstore.repository.OrderRepository;
import com.watchstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final com.watchstore.repository.WatchRepository watchRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, UserRepository userRepository,
            com.watchstore.repository.WatchRepository watchRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.watchRepository = watchRepository;
    }

    @Transactional
    public void createOrder(String username) {
        Cart cart = cartService.getCart(username);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            Watch watch = item.getWatch();
            if (watch.getInStock() < item.getQuantity()) {
                throw new com.watchstore.exception.InsufficientStockException(watch.getId(), watch.getNazwa(),
                        watch.getInStock(), item.getQuantity());
            }
            watch.setInStock(watch.getInStock() - item.getQuantity());
            watchRepository.save(watch);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setWatch(watch);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(watch.getPrice());

            order.getItems().add(orderItem);

            BigDecimal itemTotal = watch.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        order.setTotalPrice(total);
        orderRepository.save(order);

        cartService.clearCart(username);
    }

    public java.util.List<Order> findAll() {
        return orderRepository.findAll();
    }

    public java.util.List<Order> findByUser(User user) {
        return orderRepository.findByUserId(user.getId());
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}
