package com.example.dto;

import java.math.BigDecimal;

public class StatsDTO {

    private long users;
    private long services;
    private BigDecimal totalAmount;

    public StatsDTO(long users, long services, BigDecimal totalAmount) {
        this.users       = users;
        this.services    = services;
        this.totalAmount = totalAmount;
    }

    public StatsDTO() {
        this.users       = 0L;
        this.services    = 0L;
        this.totalAmount = BigDecimal.ZERO;
    }

    public long       getUsers()       { return users; }
    public long       getServices()    { return services; }
    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setUsers(long users)             { this.users = users; }
    public void setServices(long services)       { this.services = services; }
    public void setTotalAmount(BigDecimal total) { this.totalAmount = total; }
}