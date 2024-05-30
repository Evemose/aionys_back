package org.aionys.main.security.jwt;

public interface JwtEncryptor {
    String encrypt(String username);
}
