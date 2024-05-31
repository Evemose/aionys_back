package org.aionys.main.security.jwt;

public interface JwtDecryptor {
    String extractUsername(String token);
}
