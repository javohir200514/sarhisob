package com.example.service;

import com.example.dto.PersonalInfoDTO;
import com.example.dto.RegistrationDTO;
import com.example.entity.AttachEntity;
import com.example.entity.ProfileEntity;
import com.example.repository.AttachRepository;
import com.example.repository.ProfileRepository;
import com.example.util.SpringSecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileSettingService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttachService attachService;
    private final AttachRepository attachRepository;

    public ProfileSettingService(ProfileRepository profileRepository,
                                 PasswordEncoder passwordEncoder,
                                 AttachService attachService,
                                 AttachRepository attachRepository) {
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.attachService = attachService;
        this.attachRepository = attachRepository;
    }

    public RegistrationDTO getPersonalInfo(Integer profileId) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profil topilmadi"));

        RegistrationDTO dto = new RegistrationDTO();
        dto.setFirstName(profile.getName());
        dto.setLastName(profile.getSurname());
        dto.setEmail(profile.getEmail());
        return dto;
    }

    public void saveNameAndSurname(String name, String surname) {
        ProfileEntity profile = getCurrentProfile();

        profile.setName(name);
        profile.setSurname(surname);
        profileRepository.save(profile);
    }

    public void saveUsername(String newUsername) {
        ProfileEntity current = getCurrentProfile();

        String normalized = (newUsername == null) ? "" : newUsername.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new RuntimeException("Username kiritilishi shart");
        }

        if (current.getEmail() != null && current.getEmail().equalsIgnoreCase(normalized)) {
            return;
        }

        Optional<ProfileEntity> exists = profileRepository.findByEmailAndVisibleIsTrue(normalized);
        if (exists.isPresent() && !exists.get().getId().equals(current.getId())) {
            throw new RuntimeException("Bu username allaqachon band");
        }

        current.setEmail(normalized);
        profileRepository.save(current);
    }

    public void changePassword(String currentRaw, String newRaw) {
        ProfileEntity profile = getCurrentProfile();

        if (!passwordEncoder.matches(currentRaw, profile.getPassword())) {
            throw new RuntimeException("Joriy parol noto‘g‘ri");
        }

        if (newRaw == null || newRaw.trim().length() < 6) {
            throw new RuntimeException("Yangi parol kamida 6 ta belgidan iborat bo‘lishi kerak");
        }

        profile.setPassword(passwordEncoder.encode(newRaw));
        profileRepository.save(profile);
    }

    public PersonalInfoDTO getCurrentPersonalInfo() {
        ProfileEntity entity = getCurrentProfile();
        return toDto(entity);
    }

    private PersonalInfoDTO toDto(ProfileEntity entity) {
        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setUsername(entity.getEmail());
        if (entity.getPhoto() != null) {
            dto.setPhotoId(entity.getPhoto().getId());
        }

        return dto;
    }

    public String getCurrentPhotoId() {
        ProfileEntity profile = getCurrentProfile();
        return profile.getPhotoId();
    }

    public void removePhotoId() {
        ProfileEntity profile = getCurrentProfile();

        profile.setPhotoId(null);
        profileRepository.save(profile);
    }

    @Transactional
    public void replacePhotoId(String newPhotoId) {
        if (newPhotoId == null || newPhotoId.isBlank()) {
            throw new RuntimeException("New photo id is empty");
        }

        ProfileEntity profile = getCurrentProfile();
        String oldPhotoId = profile.getPhotoId();

        profile.setPhotoId(newPhotoId);
        profileRepository.save(profile);

        if (oldPhotoId != null && !oldPhotoId.isBlank() && !oldPhotoId.equals(newPhotoId)) {
            try {
                attachService.delete(oldPhotoId);
            } catch (Exception e) {
                System.err.println("Old photo delete failed: " + e.getMessage());
            }
        }
    }

    private ProfileEntity getCurrentProfile() {
        String username = SpringSecurityUtil.currentUsername();

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Foydalanuvchi tizimga kirmagan");
        }

        return profileRepository.findByEmailAndVisibleIsTrue(username)
                .orElseThrow(() -> new RuntimeException("Profil topilmadi"));
    }
}