package com.example.service;


import com.example.entity.SmsHistoryEntity;
import com.example.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository smsHistoryRepository;

    public void save(String email, String body, String code) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhoneNumber(email);
        entity.setBody(body);
        entity.setCode(code);
        entity.setCreatedDate(LocalDateTime.now());
        smsHistoryRepository.save(entity);
    }

    public SmsHistoryEntity getByEmail(String email) {
        Optional<SmsHistoryEntity> optional =
                smsHistoryRepository.findTopByPhoneNumberOrderByCreatedDateDesc(email);

        if (optional.isEmpty()) {
            throw new RuntimeException("Bu email manzilga tasdiqlash kodi yuborilmagan.");
        }

        return optional.get();
    }

    public boolean isCodeSentToEmail(String email, String code) {
        SmsHistoryEntity smsHistoryEntity = getByEmail(email);

        Integer attemptCount = smsHistoryRepository.getAttemptCount(email);
        if (attemptCount == null) {
            attemptCount = 0;
        }

        long secondsBetween = Duration.between(
                smsHistoryEntity.getCreatedDate(),
                LocalDateTime.now()
        ).getSeconds();

        if (secondsBetween > 120) {
            throw new RuntimeException("Tasdiqlash kodi eskirgan. Qaytadan kod oling.");
        }

        if (attemptCount > 3) {
            throw new RuntimeException("Urinishlar soni tugagan. Qaytadan kod oling.");
        }

        if (!code.equals(smsHistoryEntity.getCode())) {
            int remainingAttempts = Math.max(0, 3 - attemptCount);
            throw new RuntimeException("Tasdiqlash kodi noto‘g‘ri. Qolgan urinishlar soni: " + remainingAttempts);
        }

        smsHistoryRepository.deleteById(smsHistoryEntity.getId());
        return true;
    }

    public void deleteById(String id) {
        smsHistoryRepository.deleteById(id);
    }

    public Integer getAttemptCount(String email) {
        return smsHistoryRepository.getAttemptCount(email);
    }
}