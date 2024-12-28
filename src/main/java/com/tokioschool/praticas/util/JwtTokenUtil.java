package com.tokioschool.praticas.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${security.jwt.token-duration}")
    private long jwtTokenDuration;

    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    private Claims getAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).requireIssuer(issuer).parseClaimsJws(token).getBody();
    }

    public <T> T getclaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsername(String token) {
        return getclaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getclaimFromToken(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenDuration))
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            return getUsername(token) != null && getExpirationDate(token) != null;
        } catch (Exception exception) {
            return false;
        }
    }
}
