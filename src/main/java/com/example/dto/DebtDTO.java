package com.example.dto;

import com.example.enums.DebtStatus;
import com.example.enums.DebtType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtDTO {
    private Long id;
    private String person;
    private BigDecimal amount;
    private LocalDate date;
    private LocalDate deadline;
    private DebtStatus status;
    private DebtType type; // ✅

    public DebtDTO() {}

    public DebtDTO(Long id,String person, BigDecimal amount, LocalDate date, LocalDate deadline, DebtStatus status, DebtType type) {
        this.id = id;
        this.person = person;
        this.amount = amount;
        this.date = date;
        this.deadline = deadline;
        this.status = status;
        this.type = type;
    }

    public DebtType getType() { return type; }
    public void setType(DebtType type) { this.type = type; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public DebtStatus getStatus() {
        return status;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }
}