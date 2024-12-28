package com.tokioschool.praticas.services;

import com.tokioschool.praticas.domain.AppUser;
import com.tokioschool.praticas.domain.Role;
import com.tokioschool.praticas.repositories.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SecurityAppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> {
            String message = "It was not possible to find an account associated to the username '" + username + "'";
            return new UsernameNotFoundException(message);
        });
        return buildUserForAuthentication(appUser, getAuthorities(appUser.getRoles()));
    }

    private List<GrantedAuthority> getAuthorities(Set<Role> userRoles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        userRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return authorities;
    }

    private UserDetails buildUserForAuthentication(AppUser appUser, List<? extends GrantedAuthority> authorities) {
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.getActive(),
                true,
                true,
                true,
                authorities);
    }
}
