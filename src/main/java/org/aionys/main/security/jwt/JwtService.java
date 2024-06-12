package org.aionys.main.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
class JwtService implements JwtDecryptor, JwtCookieFactory {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private String encrypt(String username) {
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

    /**
     * {@inheritDoc}
     * <p>
     * @implSpec Creates two cookies, one with the first half of the JWT and the other with the second half.
     * The first cookie is httpOnly, the second is not.
     * The second cookie is used to invalidate the session on logout on a client.
     * @param username the user to create a cookie for
     * @return
     */
    @Override
    public List<Cookie> forUser(String username) {
        var jwt = encrypt(username);

        var bearerCookie = new Cookie("BearerHead", jwt.substring(0, jwt.length()/2));
        bearerCookie.setHttpOnly(true);
        bearerCookie.setPath("/");
        bearerCookie.setMaxAge((int) jwtExpiration / 1000);

        var sessionCookie = new Cookie("BearerTail", jwt.substring(jwt.length()/2));
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge((int) jwtExpiration / 1000);

        return List.of(bearerCookie, sessionCookie);
    }
}
