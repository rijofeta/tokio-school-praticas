package com.tokioschool.praticas.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsername(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        return getClaimFromToken(token, claims -> {
            List<?> auths = claims.get("auths", List.class);
            if (auths != null) {
                return auths.stream()
                        .map(role -> new SimpleGrantedAuthority(role.toString()))
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        });
    }

    public String generateToken(UserDetails userDetails) {
        // Create a List of authorities, so they get represented as a JSON array in "auths"
        List<String> auths = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenDuration))
                .setIssuer(issuer)
                .claim("auths", auths)
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
