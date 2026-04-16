package com.example.repository;

import com.example.entity.TelegramUserLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramUserLinkRepository extends JpaRepository<TelegramUserLinkEntity, Long> {
    Optional<TelegramUserLinkEntity> findByChatId(Long chatId);
    Optional<TelegramUserLinkEntity> findByProfileId(Integer profileId);
}