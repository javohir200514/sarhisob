package com.example.service;




import com.example.config.SpringConfig;
import com.example.dto.PersonalInfoDTO;
import com.example.dto.RegistrationDTO;
import com.example.entity.ProfileEntity;
import com.example.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<RegistrationDTO> dtoList = new ArrayList<>();
    @Autowired
    private SpringConfig springConfig;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileRepository profileRepository;

    public void save(RegistrationDTO user) {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(user.getFirstName());
        profileEntity.setSurname(user.getLastName());
        profileEntity.setEmail(user.getEmail());
        profileEntity.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        profileEntity.setProfileRoles(new ArrayList<>());
        profileEntity.setVisible(true);
        profileEntity.setCreatedDate(LocalDateTime.now());
        profileRepository.save(profileEntity);

    }

    public RegistrationDTO getById(String studentId) {
        for (RegistrationDTO studentDTO : dtoList) {
            //if (studentDTO.().equals(studentId)) {
                return studentDTO;
           // }
        }
        return null;
    }
    public List<PersonalInfoDTO> getLastUsers(int limit) {
        List<ProfileEntity> top5ByOrderByIdDesc = profileRepository.findTop5ByEnabledTrueOrderByIdDesc();
        List<PersonalInfoDTO> personalInfoDTOList = new ArrayList<>();
        top5ByOrderByIdDesc.forEach(profileEntity -> {
            PersonalInfoDTO personalInfoDTO = new PersonalInfoDTO();
            personalInfoDTO.setName(profileEntity.getName());
            personalInfoDTO.setSurname(profileEntity.getSurname());
            personalInfoDTO.setUsername(profileEntity.getEmail());
            personalInfoDTO.setPhotoId(profileEntity.getPhotoId());
            personalInfoDTOList.add(personalInfoDTO);
        });
        return personalInfoDTOList;
    }

    public void enableByEmail(String email) {
        ProfileEntity profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        profile.setEnabled(true);
        profileRepository.save(profile);
    }
}
