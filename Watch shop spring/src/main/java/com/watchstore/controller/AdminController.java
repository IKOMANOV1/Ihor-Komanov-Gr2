package com.watchstore.controller;

import com.watchstore.model.OrderStatus;
import com.watchstore.model.Role;
import com.watchstore.model.Watch;
import com.watchstore.repository.RoleRepository;
import com.watchstore.service.OrderService;
import com.watchstore.service.UserService;
import com.watchstore.service.WatchService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final WatchService watchService;
    private final UserService userService;
    private final OrderService orderService;
    private final RoleRepository roleRepository;

    public AdminController(WatchService watchService, UserService userService, OrderService orderService,
            RoleRepository roleRepository) {
        this.watchService = watchService;
        this.userService = userService;
        this.orderService = orderService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    // --- Watches Management (Moderator Only) ---

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/watches")
    public String listWatches(Model model) {
        model.addAttribute("watches", watchService.findAll());
        return "admin/watches";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/watches/add")
    public String addWatchForm(Model model) {
        model.addAttribute("watch", new Watch());
        return "admin/watch_form";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/watches/edit/{id}")
    public String editWatchForm(@PathVariable Long id, Model model) {
        Watch watch = watchService.findById(id).orElseThrow(() -> new RuntimeException("Watch not found"));
        model.addAttribute("watch", watch);
        return "admin/watch_form";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/watches/save")
    public String saveWatch(@Valid @ModelAttribute("watch") Watch watch, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/watch_form";
        }

        // ДЕФОЛТНАЯ КАРТИНКА: Если URL пустой, ставим 1.jpg ... 5.jpg (или .jpeg)
        if (watch.getImageUrl() == null || watch.getImageUrl().trim().isEmpty()) {
            java.util.List<String> validImages = new java.util.ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                // Проверяем наличие файла в ресурсах
                if (new org.springframework.core.io.ClassPathResource("static/images/watches/" + i + ".jpg").exists()) {
                    validImages.add("/images/watches/" + i + ".jpg");
                } else if (new org.springframework.core.io.ClassPathResource("static/images/watches/" + i + ".jpeg")
                        .exists()) {
                    validImages.add("/images/watches/" + i + ".jpeg");
                }
            }

            if (!validImages.isEmpty()) {
                int randomIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(validImages.size());
                watch.setImageUrl(validImages.get(randomIndex));
            } else {
                // Fallback если картинок нет вообще
                watch.setImageUrl("/images/watches/1.jpg");
            }
        }

        watchService.save(watch);
        return "redirect:/admin/watches";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/watches/delete/{id}")
    public String deleteWatch(@PathVariable Long id) {
        watchService.delete(id);
        return "redirect:/admin/watches";
    }

    // --- Users Management (Admin Only) ---

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    // СПИСОК ПОЛЬЗОВАТЕЛЕЙ (RODO): Просмотр всех пользователей и их ролей
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/block/{id}")
    public String toggleBlockUser(@PathVariable Long id) {
        userService.toggleBlockUser(id);
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/roles")
    // УПРАВЛЕНИЕ РОЛЯМИ: Назначение ролей пользователю
    public String updateUserRoles(@RequestParam Long userId, @RequestParam List<String> roles) {
        userService.updateUserRoles(userId, roles);
        return "redirect:/admin/users";
    }

    // --- Orders Management (Moderator Only) ---

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "admin/orders";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping("/orders/status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders";
    }
}
