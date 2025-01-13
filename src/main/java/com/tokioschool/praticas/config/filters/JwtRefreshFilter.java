package com.tokioschool.praticas.config.filters;

import com.tokioschool.praticas.util.CookieUtil;
import com.tokioschool.praticas.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRefreshFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException
    {
        Cookie jwtCookie = CookieUtil.getCookie(CookieUtil.JWT_COOKIE, request.getCookies());
        if (jwtCookie != null) {
            String token = jwtCookie.getValue();
            if (jwtTokenUtil.isTokenValid(token) && jwtTokenUtil.isExpiring(token)) {
                request.getRequestDispatcher("/refresh-jwt").forward(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
