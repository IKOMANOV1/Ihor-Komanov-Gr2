package com.watchstore.controller;

import com.watchstore.model.Order;
import com.watchstore.model.User;
import com.watchstore.service.OrderService;
import com.watchstore.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final com.watchstore.service.CartService cartService;

    public OrderController(OrderService orderService, UserService userService,
            com.watchstore.service.CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @PostMapping("/create")
    public String createOrder(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            try {
                orderService.createOrder(userDetails.getUsername());
            } catch (com.watchstore.exception.InsufficientStockException e) {
                model.addAttribute("watchId", e.getWatchId());
                model.addAttribute("watchName", e.getWatchName());
                model.addAttribute("availableStock", e.getAvailableStock());
                model.addAttribute("requestedQuantity", e.getRequestedQuantity());
                return "order_stock_confirm";
            }
        }
        return "redirect:/order/success";
    }

    @PostMapping("/create/confirm")
    public String confirmOrderWithAvailableStock(@AuthenticationPrincipal UserDetails userDetails,
            @org.springframework.web.bind.annotation.RequestParam Long itemWatchId,
            @org.springframework.web.bind.annotation.RequestParam int availableStock) {
        if (userDetails != null) {
            // Update cart item quantity to available stock
            // This requires a method in CartService or we can do it here via CartService
            // Let's add updateCartItemQuantity in CartService
            cartService.updateItemQuantity(userDetails.getUsername(), itemWatchId, availableStock);
            // Retry order creation
            orderService.createOrder(userDetails.getUsername());
        }
        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "order_success";
    }

    @GetMapping("/my")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(userDetails.getUsername()).get();
        // Since we didn't add findByUserId in OrderService explicitely yet but we have
        // it in Repo, let's fix Service to expose specific search
        // Or actually, User entity has 'orders' list relation if FetchType is correct
        // or we use repository.
        // Let's rely on Repo in Service.
        model.addAttribute("orders", orderService.findByUser(user));
        return "my_orders";
    }
}
