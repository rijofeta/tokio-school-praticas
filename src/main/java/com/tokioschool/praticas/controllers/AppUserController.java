package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.domain.AppUser;
import com.tokioschool.praticas.dtos.RegisterDTO;
import com.tokioschool.praticas.exceptions.EmailAlreadyInUseException;
import com.tokioschool.praticas.exceptions.ErrorResponse;
import com.tokioschool.praticas.exceptions.UsernameAlreadyExistsException;
import com.tokioschool.praticas.services.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppUserController {

    private final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody RegisterDTO registerDTO) {
        this.checkValidity(registerDTO);
        AppUser registerdAppUser = appUserService.registerAppUser(convertToAppUser(registerDTO));
        logger.info("New user registered: id={}, username={}",
                registerdAppUser.getId(),
                registerdAppUser.getUsername());
        return ResponseEntity.ok(registerdAppUser);
    }

    private AppUser convertToAppUser(RegisterDTO registerDTO) {
        AppUser appUser = new AppUser();
        appUser.setUsername(registerDTO.getUsername());
        appUser.setPassword(registerDTO.getPassword());
        appUser.setName(registerDTO.getName());
        appUser.setSurname(registerDTO.getSurname());
        appUser.setEmail(registerDTO.getEmail());
        return appUser;
    }

    private void checkValidity(RegisterDTO registerDTO) {
        if (appUserService.existsByUsername(registerDTO.getUsername())) {
            throw new UsernameAlreadyExistsException(registerDTO.getUsername());
        }

        if (appUserService.existsByEmail(registerDTO.getEmail())) {
            throw new EmailAlreadyInUseException(registerDTO.getEmail());
        }
    }
}
