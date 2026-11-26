package dev.loanops.loanservice.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduledInstalment(int sequence, LocalDate dueDate, BigDecimal principalDue, BigDecimal interestDue) {

    public BigDecimal totalDue() {
        return principalDue.add(interestDue);
    }
}
