package com.example.dto;

import java.math.BigDecimal;

public class DebtStatsDTO {
    private BigDecimal total;
    private BigDecimal unpaid;
    private BigDecimal paid;
    private BigDecimal zero;
    private long count;
    private long overdue;

    public DebtStatsDTO() {}

    public DebtStatsDTO(BigDecimal total, BigDecimal unpaid, BigDecimal zero, BigDecimal paid, long count, long overdue) {
        this.total = total;
        this.unpaid = unpaid;
        this.zero = zero;
        this.paid = paid;
        this.count = count;
        this.overdue = overdue;
    }



    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getUnpaid() { return unpaid; }
    public void setUnpaid(BigDecimal unpaid) { this.unpaid = unpaid; }

    public BigDecimal getPaid() { return paid; }
    public void setPaid(BigDecimal paid) { this.paid = paid; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }

    public long getOverdue() { return overdue; }
    public void setOverdue(long overdue) { this.overdue = overdue; }
}