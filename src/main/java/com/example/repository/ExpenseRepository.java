package com.example.repository;

import com.example.entity.ExpenseEntity;
import com.vaadin.flow.component.UI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Integer profileId);

    Optional<ExpenseEntity> findByIdAndProfileId(Long id, Integer profileId);

    long countDistinctCategoryByProfileId(Integer id);

    @Query("select coalesce(sum(e.amount), 0) from ExpenseEntity e where e.profile.id = :id")
    BigDecimal sumAmountByProfileId(@Param("id") Integer id);


    @Query(value = "SELECT COUNT(DISTINCT category) FROM expenses", nativeQuery = true)
    long countDistinctCategoryGlobal();
    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM expenses", nativeQuery = true)
    BigDecimal sumAllAmounts();

}