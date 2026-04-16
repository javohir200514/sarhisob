package com.example.repository;

import com.example.entity.DebtEntity;
import com.example.enums.DebtStatus;
import com.example.enums.DebtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DebtRepository extends JpaRepository<DebtEntity, Long> {

    Optional<DebtEntity> findByPersonIgnoreCaseAndType(String person, DebtType type);

    List<DebtEntity> findAllByType(DebtType type);
    @Query("select coalesce(sum(d.amount),0) from DebtEntity d where d.type = :type and d.status = :status")
    BigDecimal sumByTypeAndStatus(DebtType type, DebtStatus status);

    Optional<DebtEntity> findFirstByPersonIgnoreCaseAndType(String person, DebtType type);

    // ✅ Type bo‘yicha ro‘yxat
    List<DebtEntity> findAllByTypeOrderByIdDesc(DebtType type);

    // ✅ Type bo‘yicha umumiy summa
    @Query("select coalesce(sum(d.amount),0) from DebtEntity d where d.type = :type")
    BigDecimal sumAllByType(DebtType type);

    // ✅ Status + Type bo‘yicha summa
    @Query("select coalesce(sum(d.amount),0) from DebtEntity d where d.status = :status and d.type = :type")
    BigDecimal sumByStatusAndType(DebtStatus status, DebtType type);
}