package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.services.SecurityAppUserService;
import com.tokioschool.praticas.util.CookieUtil;
import com.tokioschool.praticas.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JwtRefreshController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SecurityAppUserService securityAppUserService;

    @GetMapping("/refresh-jwt")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Check if there's a cookie containing the refresh token
        Cookie refreshCookie = CookieUtil.getCookie(CookieUtil.REFRESH_COOKIE, request.getCookies());
        if (refreshCookie == null) return "redirect:/login";

        try {
            String username = jwtTokenUtil.getUsername(refreshCookie.getValue());
            UserDetails userDetails = securityAppUserService.loadUserByUsername(username);
            String token = jwtTokenUtil.generateAccessToken(userDetails);
            // delete old JWT cookie
            Cookie oldJwtCookie = CookieUtil.getCookie(CookieUtil.JWT_COOKIE, request.getCookies());
            oldJwtCookie.setMaxAge(0);
            oldJwtCookie.setValue(null);
            response.addCookie(oldJwtCookie);
            //
            Cookie newJwtCookie = new Cookie(CookieUtil.JWT_COOKIE, token);
            newJwtCookie.setHttpOnly(true);
            response.addCookie(newJwtCookie);

        } catch (ExpiredJwtException | UsernameNotFoundException exception) {
            return "redirect:/login";
        }
        return "redirect:/products";
    }
}
