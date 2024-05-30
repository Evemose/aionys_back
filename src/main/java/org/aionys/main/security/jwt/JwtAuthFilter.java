package org.aionys.main.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Profile({"prod", "test-prod"})
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtDecryptor jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        var token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            var jwt = token.substring(7);
            String username;
            try {
                username = jwtService.decrypt(jwt);
            } catch (TokenExpiredException e) {
                filterChain.doFilter(request, response); // let auth fail downstream
                return;
            }
            var user = userDetailsService.loadUserByUsername(username);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }
        filterChain.doFilter(request, response);
    }
}
