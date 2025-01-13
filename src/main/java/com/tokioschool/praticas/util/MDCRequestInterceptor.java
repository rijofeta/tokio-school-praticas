package com.tokioschool.praticas.util;

import com.tokioschool.praticas.domain.AppUser;
import com.tokioschool.praticas.services.AppUserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCRequestInterceptor implements HandlerInterceptor {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        putRequestInfo(request);
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            putUserInfo(request);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        MDC.clear();
    }

    private void putUserInfo(HttpServletRequest request) {
        Cookie jwtCookie = CookieUtil.getCookie(CookieUtil.JWT_COOKIE, request.getCookies());
        if(jwtCookie != null && jwtTokenUtil.isTokenValid(jwtCookie.getValue())) {
            AppUser appUser = appUserService.findByUsername(jwtTokenUtil.getUsername(jwtCookie.getValue()));
            MDC.put("userId", String.valueOf(appUser.getId()));
            MDC.put("username", appUser.getUsername());
            MDC.put("roles", appUser.getRoles().toString());
        }
    }

    private void putRequestInfo(HttpServletRequest request) {
        String endpoint = request.getRequestURI();
        if(endpoint.equals("/error")) {
            endpoint = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        }
        MDC.put("endpoint", endpoint);
        MDC.put("method", request.getMethod());
    }
}
