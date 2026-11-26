package dev.loanops.loanservice.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRecord(LocalDate paidOn, BigDecimal amount) {
}
