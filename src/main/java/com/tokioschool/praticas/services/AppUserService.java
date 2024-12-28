package com.tokioschool.praticas.services;

import com.tokioschool.praticas.controllers.AppErrorController;
import com.tokioschool.praticas.domain.AppUser;
import com.tokioschool.praticas.domain.Role;
import com.tokioschool.praticas.repositories.AppUserRepository;
import com.tokioschool.praticas.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username).orElseThrow();
    }

    public boolean existsByUsername(String username) {
        return appUserRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }

    public AppUser registerAppUser(AppUser appUser) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setCreationDate(LocalDate.now());
        appUser.setActive(true);
        appUser.setRoles(new HashSet<>(Collections.singleton(roleRepository.getByName(Role.USER_ROLE))));
        return appUserRepository.save(appUser);
    }
}
