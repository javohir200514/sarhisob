package com.example.repository;

import com.example.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findTopByEmailOrderByIdDesc(String email);
    Optional<EmailOtp> findByEmailAndCodeAndUsedFalse(String email, String code);
}