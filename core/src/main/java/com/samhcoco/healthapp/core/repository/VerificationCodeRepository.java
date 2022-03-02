package com.samhcoco.healthapp.core.repository;

import com.samhcoco.healthapp.core.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByCodeAndUserIdAndVerifiedFalse(String code, Long userId);
}