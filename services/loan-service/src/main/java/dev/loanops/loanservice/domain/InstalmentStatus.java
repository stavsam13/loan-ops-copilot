package dev.loanops.loanservice.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstalmentStatus(int sequence, LocalDate dueDate, BigDecimal amountDue, BigDecimal amountPaid,
                               BigDecimal shortfall) {
}
