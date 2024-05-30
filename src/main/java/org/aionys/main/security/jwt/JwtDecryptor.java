package org.aionys.main.security.jwt;

public interface JwtDecryptor {
    String decrypt(String token) throws TokenExpiredException;
}
