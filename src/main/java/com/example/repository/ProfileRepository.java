package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.entity.ProfileEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Integer> {
    Optional<ProfileEntity> findByEmailAndVisibleIsTrue(String username);


    @Query("SELECT p.id FROM ProfileEntity p WHERE p.email = :email AND p.enabled = true")
    Integer findProfileIdByEmail(@Param("email") String email);

    Optional<ProfileEntity> findByEmailAndEnabledTrue(String email);

    List<ProfileEntity> findTop5ByEnabledTrueOrderByIdDesc();
    @Modifying
    @Transactional
    @Query(value = """
        ALTER TABLE profile
        DROP CONSTRAINT IF EXISTS fkol7hbk7arjubdo9ce832xe569
        """, nativeQuery = true)
    void dropPhotoFk();

    @Modifying
    @Transactional
    @Query(value = """
        ALTER TABLE profile
        ADD CONSTRAINT fkol7hbk7arjubdo9ce832xe569
        FOREIGN KEY (photo_id)
        REFERENCES attach(id)
        ON DELETE SET NULL
        """, nativeQuery = true)
    void addPhotoFkOnDeleteSetNull();

    long countByEnabledTrue();
}