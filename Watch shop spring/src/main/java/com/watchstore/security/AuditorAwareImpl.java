package com.watchstore.security;

import com.watchstore.model.User;
import com.watchstore.repository.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
// RODO: Компонент для определения текущего пользователя для аудита
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserRepository userRepository;

    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return Optional.of(userRepository.getReferenceById(((UserDetailsImpl) principal).getId()));
        }

        return userRepository.findByUsername(authentication.getName());
    }
}
