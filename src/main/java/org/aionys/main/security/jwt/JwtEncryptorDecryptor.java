package org.aionys.main.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
class JwtEncryptorDecryptor implements JwtEncryptor, JwtDecryptor {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String encrypt(String username) {
        return Jwts
                .builder()
                .subject(username)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        var parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build();
        return parser.parseSignedClaims(token).getPayload().getSubject();
    }
}
