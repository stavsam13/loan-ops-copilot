package dev.loanops.loanservice.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Computes a loan's position (outstanding principal, arrears, days past due, delinquency bucket)
 * from its repayment schedule and actual payments.
 *
 * <p>This class is the single source of truth for every number the copilot reports. The LLM never
 * does arithmetic: it calls the API that wraps this class and explains the result.
 *
 * <p>Allocation rule: payments are applied in date order to the oldest instalment with an unpaid
 * amount, interest before principal. Any amount left after all due instalments are covered is
 * reported as a credit balance.
 *
 * <p>Pure Java with no framework dependencies, so it is trivially unit-testable.
 */
public final class DelinquencyCalculator {

    private static final int SCALE = 2;

    private DelinquencyCalculator() {
    }

    public static LoanPosition calculate(BigDecimal originalPrincipal,
                                         List<ScheduledInstalment> schedule,
                                         List<PaymentRecord> payments,
                                         LocalDate asOf) {
        var instalments = schedule.stream()
                .sorted(Comparator.comparingInt(ScheduledInstalment::sequence))
                .toList();
        var dueInstalments = instalments.stream()
                .filter(i -> !i.dueDate().isAfter(asOf))
                .toList();

        // Only payments received up to asOf count.
        BigDecimal remainingPaid = payments.stream()
                .filter(p -> !p.paidOn().isAfter(asOf))
                .map(PaymentRecord::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = remainingPaid;

        var statuses = new ArrayList<InstalmentStatus>();
        BigDecimal principalRepaid = BigDecimal.ZERO;
        BigDecimal totalDue = BigDecimal.ZERO;

        for (var instalment : dueInstalments) {
            BigDecimal due = instalment.totalDue();
            totalDue = totalDue.add(due);

            BigDecimal allocated = remainingPaid.min(due);
            remainingPaid = remainingPaid.subtract(allocated);

            // Interest first, then principal.
            BigDecimal toPrincipal = allocated.subtract(instalment.interestDue()).max(BigDecimal.ZERO);
            principalRepaid = principalRepaid.add(toPrincipal);

            statuses.add(new InstalmentStatus(
                    instalment.sequence(),
                    instalment.dueDate(),
                    scale(due),
                    scale(allocated),
                    scale(due.subtract(allocated))));
        }

        var unpaid = statuses.stream().filter(s -> s.shortfall().signum() > 0).toList();
        BigDecimal arrears = unpaid.stream()
                .map(InstalmentStatus::shortfall)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long daysPastDue = unpaid.isEmpty()
                ? 0
                : ChronoUnit.DAYS.between(unpaid.getFirst().dueDate(), asOf);

        // A credit balance (prepayment) also reduces principal.
        BigDecimal credit = remainingPaid;
        BigDecimal outstandingPrincipal = originalPrincipal
                .subtract(principalRepaid)
                .subtract(credit)
                .max(BigDecimal.ZERO);

        var nextInstalment = instalments.stream()
                .filter(i -> i.dueDate().isAfter(asOf))
                .findFirst()
                .orElse(null);

        return new LoanPosition(
                asOf,
                scale(originalPrincipal),
                scale(outstandingPrincipal),
                scale(totalDue),
                scale(totalPaid),
                scale(arrears),
                scale(credit),
                daysPastDue,
                DelinquencyBucket.fromDaysPastDue(daysPastDue),
                unpaid,
                nextInstalment == null ? null : nextInstalment.dueDate(),
                nextInstalment == null ? null : scale(nextInstalment.totalDue()));
    }

    private static BigDecimal scale(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_EVEN);
    }
}
