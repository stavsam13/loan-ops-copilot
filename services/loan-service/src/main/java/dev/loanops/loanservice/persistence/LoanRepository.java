package dev.loanops.loanservice.persistence;

import dev.loanops.loanservice.persistence.Records.Customer;
import dev.loanops.loanservice.persistence.Records.Instalment;
import dev.loanops.loanservice.persistence.Records.Loan;
import dev.loanops.loanservice.persistence.Records.Payment;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class LoanRepository {

    private final JdbcClient jdbc;

    public LoanRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Customer> findCustomer(long customerId) {
        return jdbc.sql("""
                        SELECT id, first_name, last_name, email, segment, relationship_officer
                        FROM customers WHERE id = :id
                        """)
                .param("id", customerId)
                .query(Customer.class)
                .optional();
    }

    public List<Loan> findLoansByCustomer(long customerId) {
        return jdbc.sql(LOAN_SELECT + " WHERE customer_id = :customerId ORDER BY start_date")
                .param("customerId", customerId)
                .query(Loan.class)
                .list();
    }

    public Optional<Loan> findLoan(long loanId) {
        return jdbc.sql(LOAN_SELECT + " WHERE id = :id")
                .param("id", loanId)
                .query(Loan.class)
                .optional();
    }

    public List<Instalment> findSchedule(long loanId) {
        return jdbc.sql("""
                        SELECT seq, due_date, principal_due, interest_due
                        FROM instalments WHERE loan_id = :loanId ORDER BY seq
                        """)
                .param("loanId", loanId)
                .query(Instalment.class)
                .list();
    }

    public List<Payment> findPayments(long loanId, LocalDate from, LocalDate to) {
        return jdbc.sql("""
                        SELECT id, paid_on, amount, channel, reference
                        FROM payments
                        WHERE loan_id = :loanId AND paid_on BETWEEN :from AND :to
                        ORDER BY paid_on, id
                        """)
                .param("loanId", loanId)
                .param("from", from)
                .param("to", to)
                .query(Payment.class)
                .list();
    }

    private static final String LOAN_SELECT = """
            SELECT id, loan_number, customer_id, product_type, principal, annual_interest_rate,
                   term_months, start_date, currency, agreement_document_id
            FROM loans
            """;
}
