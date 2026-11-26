package dev.loanops.loanservice.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Row-level records returned by the API. Column names map to fields via snake_case to camelCase.
 */
public final class Records {

    private Records() {
    }

    public record Customer(long id, String firstName, String lastName, String email, String segment,
                           String relationshipOfficer) {
    }

    public record Loan(long id, String loanNumber, long customerId, String productType, BigDecimal principal,
                       BigDecimal annualInterestRate, int termMonths, LocalDate startDate, String currency,
                       String agreementDocumentId) {
    }

    public record Instalment(int seq, LocalDate dueDate, BigDecimal principalDue, BigDecimal interestDue) {
    }

    public record Payment(long id, LocalDate paidOn, BigDecimal amount, String channel, String reference) {
    }
}
