package org.aionys.main.security.jwt;

import io.jsonwebtoken.MalformedJwtException;

public interface JwtDecryptor {
    String extractUsername(String token) throws MalformedJwtException;
}
