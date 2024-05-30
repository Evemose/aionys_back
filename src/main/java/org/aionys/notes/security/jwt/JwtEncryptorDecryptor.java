package org.aionys.notes.security.jwt;


interface JwtEncryptorDecryptor {
    String encrypt(String username);
    String decrypt(String token) throws TokenExpiredException;
}
