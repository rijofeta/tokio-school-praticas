package com.tokioschool.praticas.config.filters;

import com.tokioschool.praticas.services.SecurityAppUserService;
import com.tokioschool.praticas.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SecurityAppUserService securityAppUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException
    {
        Cookie jwtCookie = getJwtCookie(request.getCookies());
        if (jwtCookie != null) attemptAuthentication(jwtCookie.getValue());
        filterChain.doFilter(request, response);
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String jwtToken = authHeader.substring(7);
//            String username = jwtTokenUtil.getUsername(jwtToken);
//            UserDetails userDetails = securityAppUserService.loadUserByUsername(username);
//
//            if (!jwtTokenUtil.isTokenExpired(jwtToken)
//                    && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                Authentication authentication = new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                );
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        filterChain.doFilter(request, response);
    }
    public static Cookie getJwtCookie(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("JWT"))
                .findFirst()
                .orElse(null);
    }

    private void attemptAuthentication(String jwtToken) {
        if (jwtTokenUtil.isTokenValid(jwtToken)
                && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            String username = jwtTokenUtil.getUsername(jwtToken);
            List<GrantedAuthority> authorities = jwtTokenUtil.getAuthorities(jwtToken);
//            UserDetails userDetails = securityAppUserService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
