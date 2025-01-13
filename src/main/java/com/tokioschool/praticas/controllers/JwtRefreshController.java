package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.util.CookieUtil;
import com.tokioschool.praticas.util.JwtTokenUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static com.tokioschool.praticas.util.JwtTokenUtil.JWT_TOKEN_DURATION;

@Controller
public class JwtRefreshController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/refresh-jwt")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie oldJwtCookie = CookieUtil.getCookie(CookieUtil.JWT_COOKIE, request.getCookies());
        if (oldJwtCookie != null && jwtTokenUtil.isTokenValid(oldJwtCookie.getValue())) {
            String username = jwtTokenUtil.getUsername(oldJwtCookie.getValue());
            List<GrantedAuthority> auths = jwtTokenUtil.getAuthorities(oldJwtCookie.getValue());
            String token = jwtTokenUtil.generateToken(username, auths);
            // delete old JWT cookie
            oldJwtCookie.setMaxAge(0);
            oldJwtCookie.setValue(null);
            response.addCookie(oldJwtCookie);
            //
            Cookie newJwtCookie = new Cookie(CookieUtil.JWT_COOKIE, token);
            newJwtCookie.setHttpOnly(true);
            newJwtCookie.setMaxAge((int) JWT_TOKEN_DURATION / 1000);
            response.addCookie(newJwtCookie);
        }
        return "redirect:" + request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
    }
}
