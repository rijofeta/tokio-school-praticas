package com.tokioschool.praticas.util;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;

public class CookieUtil {

    public static final String JWT_COOKIE = "JWT";
    public static final String REFRESH_COOKIE = "refresh_token";

    public static Cookie getCookie(String cookieName, Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }
}
