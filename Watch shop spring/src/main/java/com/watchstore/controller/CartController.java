package com.watchstore.controller;

import com.watchstore.model.Cart;
import com.watchstore.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Cart cart = cartService.getCart(userDetails.getUsername());
        model.addAttribute("cart", cart);

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getWatch().getPrice().doubleValue() * item.getQuantity())
                .sum();
        model.addAttribute("totalPrice", total);

        return "cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> payload) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Long watchId = Long.valueOf(payload.get("watchId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());
            cartService.addToCart(userDetails.getUsername(), watchId, quantity);
            return ResponseEntity.ok(Map.of("message", "Item added to cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/remove")
    public String removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long watchId) {
        if (userDetails != null) {
            cartService.removeFromCart(userDetails.getUsername(), watchId);
        }
        return "redirect:/cart";
    }
}
