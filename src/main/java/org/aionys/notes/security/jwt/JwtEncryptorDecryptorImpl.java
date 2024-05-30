package org.aionys.notes.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.StandardException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Profile({"prod", "test-prod"})
class JwtEncryptorDecryptorImpl implements JwtEncryptorDecryptor {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-time}")
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
    public String decrypt(String token) throws TokenExpiredException {
        var parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build();
        var body = parser.parseSignedClaims(token).getPayload();
        if (body.getExpiration().before(new Date())) {
            throw new TokenExpiredException();
        }
        return body.getSubject();
    }
}
