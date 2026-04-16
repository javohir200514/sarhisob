package com.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
public class ExpenseDTO {

    private Long id;
    private String category;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    public ExpenseDTO() {
    }

    public ExpenseDTO(Long id, String category, BigDecimal amount, LocalDate date, String description) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public ExpenseDTO(String category, BigDecimal amount, LocalDate date, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

}