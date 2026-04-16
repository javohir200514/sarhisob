package com.example.repository;


import com.example.entity.SmsHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SmsHistoryRepository extends CrudRepository<SmsHistoryEntity,String> {

    Optional<SmsHistoryEntity> findTopByPhoneNumberOrderByCreatedDateDesc(String phoneNumber);
    @Transactional
    @Modifying
    @Query(value = "delete from sms_history where id=:id",nativeQuery = true)
    void deleteById(@Param("id")String id);

    @Query(value = "select count(*) from sms_history where phone_number = :phone", nativeQuery = true)
    Integer getAttemptCount(@Param("phone") String phone);




}