package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.dtos.LoginDTO;
import com.tokioschool.praticas.services.AppUserService;
import com.tokioschool.praticas.services.AuthService;
import com.tokioschool.praticas.services.SecurityAppUserService;
import com.tokioschool.praticas.util.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private SecurityAppUserService securityAppUserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDTO loginDTO, HttpServletResponse response) {
        Authentication authentication = authService.authenticate(loginDTO);

        UserDetails userDetails;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            userDetails = (UserDetails) principal;
        } else if (principal instanceof String) {
            userDetails = securityAppUserService.loadUserByUsername((String) principal);
        } else {
            throw new RuntimeException("Loading the user failed.");
        }
        String token = jwtTokenUtil.generateToken(userDetails);
        Cookie jwtCookie = new Cookie("JWT", token);
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        logger.info("Successfull login: id={}, username={}",
                appUserService.findByUsername(userDetails.getUsername()).getId(),
                userDetails.getUsername());

        return "redirect:/products";
    }
}
