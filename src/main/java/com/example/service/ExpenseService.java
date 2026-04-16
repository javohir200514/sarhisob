package com.example.service;

import com.example.dto.ExpenseDTO;
import com.example.entity.ExpenseEntity;
import com.example.entity.ProfileEntity;
import com.example.repository.ExpenseRepository;
import com.example.repository.ProfileRepository;
import com.example.util.SpringSecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProfileRepository profileRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          ProfileRepository profileRepository) {
        this.expenseRepository = expenseRepository;
        this.profileRepository = profileRepository;
    }

    public List<ExpenseDTO> getAllByCurrentUser() {
        String currentUsername = SpringSecurityUtil.currentUsername();

        if (currentUsername == null) {
            throw new RuntimeException("Current username is null");
        }

        ProfileEntity profile = profileRepository.findByEmailAndVisibleIsTrue(currentUsername)
                .orElseThrow(() -> new RuntimeException("Profile not found for username: " + currentUsername));

        return expenseRepository.findByProfileIdOrderByDateDesc(profile.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ExpenseDTO save(ExpenseDTO dto) {
        String currentUsername = SpringSecurityUtil.currentUsername();

        if (currentUsername == null) {
            throw new RuntimeException("Current username is null");
        }

        ProfileEntity profile = profileRepository.findByEmailAndVisibleIsTrue(currentUsername)
                .orElseThrow(() -> new RuntimeException("Profile not found for username: " + currentUsername));

        ExpenseEntity entity = new ExpenseEntity();
        entity.setCategory(dto.getCategory());
        entity.setAmount(dto.getAmount());
        entity.setDate(dto.getDate());
        entity.setDescription(dto.getDescription());
        entity.setProfileId(profile.getId());

        ExpenseEntity saved = expenseRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public ExpenseDTO update(Long id, ExpenseDTO dto) {
        String currentUsername = SpringSecurityUtil.currentUsername();

        if (currentUsername == null) {
            throw new RuntimeException("Current username is null");
        }

        ProfileEntity profile = profileRepository.findByEmailAndVisibleIsTrue(currentUsername)
                .orElseThrow(() -> new RuntimeException("Profile not found for username: " + currentUsername));

        ExpenseEntity entity = expenseRepository.findByIdAndProfileId(id, profile.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));

        entity.setCategory(dto.getCategory());
        entity.setAmount(dto.getAmount());
        entity.setDate(dto.getDate());
        entity.setDescription(dto.getDescription());

        ExpenseEntity updated = expenseRepository.save(entity);
        return toDTO(updated);
    }



    @Transactional
    public void delete(Long id) {
        String currentUsername = SpringSecurityUtil.currentUsername();

        if (currentUsername == null) {
            throw new RuntimeException("Current username is null");
        }

        ProfileEntity profile = profileRepository.findByEmailAndVisibleIsTrue(currentUsername)
                .orElseThrow(() -> new RuntimeException("Profile not found for username: " + currentUsername));

        ExpenseEntity entity = expenseRepository.findByIdAndProfileId(id, profile.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));

        expenseRepository.delete(entity);
    }


    private ExpenseDTO toDTO(ExpenseEntity entity) {
        return new ExpenseDTO(
                entity.getId(),
                entity.getCategory(),
                entity.getAmount(),
                entity.getDate(),
                entity.getDescription()
        );
    }
}