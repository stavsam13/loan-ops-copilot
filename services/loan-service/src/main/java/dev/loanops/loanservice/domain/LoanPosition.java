package dev.loanops.loanservice.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * A loan's computed position on a given date. Every field is deterministic and reproducible
 * from the schedule and payment history, so the agent can cite it as a fact.
 */
public record LoanPosition(
        LocalDate asOf,
        BigDecimal originalPrincipal,
        BigDecimal outstandingPrincipal,
        BigDecimal totalDueToDate,
        BigDecimal totalPaidToDate,
        BigDecimal arrearsAmount,
        BigDecimal creditBalance,
        long daysPastDue,
        DelinquencyBucket bucket,
        List<InstalmentStatus> unpaidInstalments,
        LocalDate nextDueDate,
        BigDecimal nextInstalmentAmount) {
}
