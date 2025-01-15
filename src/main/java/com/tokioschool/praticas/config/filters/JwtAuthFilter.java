package com.tokioschool.praticas.config.filters;

import com.tokioschool.praticas.services.SecurityAppUserService;
import com.tokioschool.praticas.util.CookieUtil;
import com.tokioschool.praticas.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final String[] skipFilterPaths = new String[]{
            "/resources/**", "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**", "/fonts/**",
            "/webjars/**", "/register", "/refresh-jwt", "/login", "/error", "/favicon.ico"
    };

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

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
        // Check if authentication is needed
        if (isPathMatched(request.getRequestURI(), skipFilterPaths) ||
                SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        // Check if there's a cookie containing the access JWT
        Cookie jwtCookie = CookieUtil.getCookie(CookieUtil.JWT_COOKIE, request.getCookies());
        if (jwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // Attempt authentication
        String jwt = jwtCookie.getValue();
        try {
            attemptAuthentication(jwt);
        } catch (ExpiredJwtException expiredJwtException) {
            // Delegate exception to appropriate handler (GlobalExceptionHandler in this case)
            handlerExceptionResolver.resolveException(request, response, null, expiredJwtException);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void attemptAuthentication(String jwtToken) {
        String username = jwtTokenUtil.getUsername(jwtToken);
        List<GrantedAuthority> authorities = jwtTokenUtil.getAuthorities(jwtToken);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isPathMatched(String requestPath, String[] patterns) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : patterns) {
            if (matcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
}
