package com.tokioschool.praticas.config.filters;

import com.tokioschool.praticas.services.SecurityAppUserService;
import com.tokioschool.praticas.util.CookieUtil;
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
        Cookie jwtCookie = CookieUtil.getCookie(CookieUtil.JWT_COOKIE, request.getCookies());
        if (jwtCookie != null) attemptAuthentication(jwtCookie.getValue());
        filterChain.doFilter(request, response);
    }

    private void attemptAuthentication(String jwtToken) {
        if (jwtTokenUtil.isTokenValid(jwtToken)
                && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            String username = jwtTokenUtil.getUsername(jwtToken);
            List<GrantedAuthority> authorities = jwtTokenUtil.getAuthorities(jwtToken);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
