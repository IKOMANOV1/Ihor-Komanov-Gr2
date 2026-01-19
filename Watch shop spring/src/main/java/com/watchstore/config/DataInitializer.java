package com.watchstore.config;

import com.watchstore.model.Role;
import com.watchstore.model.User;
import com.watchstore.repository.RoleRepository;
import com.watchstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create Roles
            createRoleIfNotFound("ROLE_USER");
            createRoleIfNotFound("ROLE_ADMIN");
            createRoleIfNotFound("ROLE_MODERATOR");

            // Create Admin
            createUserIfNotFound("admin", "admin@watchstore.com", "admin123", "ROLE_ADMIN");

            // Create Manager (Moderator)
            createUserIfNotFound("manager", "manager@watchstore.com", "manager123", "ROLE_MODERATOR");

            // Create User
            createUserIfNotFound("user", "user@watchstore.com", "user123", "ROLE_USER");
        };
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            roleRepository.save(role);
        }
    }

    private void createUserIfNotFound(String username, String email, String password, String roleName) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(Set.of(roleRepository.findByName(roleName).get()));
            user.setCreatedAt(LocalDateTime.now());
            // user.setCreatedBy(null); // System creation
            userRepository.save(user);
            System.out.println("Created default user: " + username + " / " + password);
        }
    }
}
