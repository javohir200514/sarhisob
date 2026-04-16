package com.example.controller;

import com.example.entity.ProfileEntity;
import com.example.entity.TelegramUserLinkEntity;
import com.example.repository.ProfileRepository;
import com.example.repository.TelegramUserLinkRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

@Controller
public class TelegramConnectController {

    private final ProfileRepository profileRepository;
    private final TelegramUserLinkRepository telegramUserLinkRepository;

    public TelegramConnectController(ProfileRepository profileRepository,
                                     TelegramUserLinkRepository telegramUserLinkRepository) {
        this.profileRepository = profileRepository;
        this.telegramUserLinkRepository = telegramUserLinkRepository;
    }

    @GetMapping("/telegram/connect")
    public void connectTelegram(@RequestParam Long chatId, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && !"anonymousUser".equals(auth.getName());

        if (!authenticated) {
            response.sendRedirect("/login");
            return;
        }

        String username = auth.getName();

        Optional<ProfileEntity> optionalProfile = profileRepository.findByEmailAndVisibleIsTrue(username);
        if (optionalProfile.isEmpty()) {
            response.sendRedirect("/registration");
            return;
        }

        ProfileEntity profile = optionalProfile.get();

        TelegramUserLinkEntity link = telegramUserLinkRepository.findByChatId(chatId)
                .orElseGet(TelegramUserLinkEntity::new);

        link.setChatId(chatId);
        link.setProfileId(profile.getId());
        telegramUserLinkRepository.save(link);

        response.sendRedirect("/profile");
    }
}