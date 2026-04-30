package com.example.service;

import com.example.config.SpringConfig;
import com.example.entity.ProfileEntity;
import com.example.repository.ProfileRepository;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final ProfileRepository profileRepository;
    private final SpringConfig springConfig;

    public AuthService(ProfileRepository profileRepository, SpringConfig springConfig) {
        this.profileRepository = profileRepository;
        this.springConfig = springConfig;
    }

    public boolean login(String username, String password) {

        Optional<ProfileEntity> optional =
                profileRepository.findByEmailAndVisibleIsTrueAndEnabledIsTrue(username);

        if (optional.isEmpty()) {
            return false;
        }

        ProfileEntity profile = optional.get();

        boolean isMatch = springConfig
                .bcryptPasswordEncoder()
                .matches(password, profile.getPassword());

        if (!isMatch) {
            return false;
        }

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        profile.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = VaadinServletRequest.getCurrent().getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return true;
    }

    public boolean existsUser(String username) {
        return profileRepository
                .findByEmailAndVisibleIsTrueAndEnabledIsTrue(username)
                .isPresent();
    }
}