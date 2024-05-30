package org.aionys.main.commons.auditing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aionys.main.security.users.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityAuditorAware implements AuditorAware<User> {

    private final UserDetailsService userDetailsService;

    @Override
    public @NonNull Optional<User> getCurrentAuditor() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.of((User) userDetailsService.loadUserByUsername(authentication.getName()));
    }
}