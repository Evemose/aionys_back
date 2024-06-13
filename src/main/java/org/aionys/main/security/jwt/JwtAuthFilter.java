package org.aionys.main.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtDecryptor jwtService;

    private final UserDetailsService userDetailsService;

    private final Environment environment;

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/register") || request.getRequestURI().equals("/login");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String auth = null;
        if (environment.matchesProfiles("dev")) {
            auth = request.getHeader("Authorization");
        }
        if (auth == null) {
            auth = getBearerFromCookies(request.getCookies());
        }
        try {
            if (auth != null && auth.startsWith("Bearer ")) {
                var jwt = auth.substring(7);
                var username = jwtService.extractUsername(jwt);
                var user = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                );
            }

        } catch (Exception e) {
            log.info("Failed to authenticate user {}: {}", auth, e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getBearerFromCookies(Cookie... cookies) {
        StringBuilder tokenBuilder = new StringBuilder();
        if (cookies != null) {
            String tailIfEncountered = null;
            for (var c : cookies) {
                if (c.getName().equals("BearerHead")) {
                    tokenBuilder.append(c.getValue());
                    if (tailIfEncountered != null) {
                        tokenBuilder.append(tailIfEncountered);
                        break;
                    }
                } else if (c.getName().equals("BearerTail")) {
                    if (tokenBuilder.isEmpty()) {
                        tailIfEncountered = c.getValue();
                    } else {
                        tokenBuilder.append(c.getValue());
                        break;
                    }
                }
            }
        }
        return tokenBuilder.isEmpty() ? null : tokenBuilder.insert(0, "Bearer ").toString();
    }

}
