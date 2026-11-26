package dev.loanops.loanservice.api;

import dev.loanops.loanservice.domain.DelinquencyCalculator;
import dev.loanops.loanservice.domain.LoanPosition;
import dev.loanops.loanservice.domain.PaymentRecord;
import dev.loanops.loanservice.domain.ScheduledInstalment;
import dev.loanops.loanservice.persistence.LoanRepository;
import dev.loanops.loanservice.persistence.Records.Instalment;
import dev.loanops.loanservice.persistence.Records.Loan;
import dev.loanops.loanservice.persistence.Records.Payment;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanRepository repository;
    private final Clock clock;

    public LoanController(LoanRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @GetMapping("/{loanId}")
    public Loan loan(@PathVariable long loanId) {
        return repository.findLoan(loanId)
                .orElseThrow(() -> new NotFoundException("Loan " + loanId + " not found"));
    }

    @GetMapping("/{loanId}/schedule")
    public List<Instalment> schedule(@PathVariable long loanId) {
        loan(loanId);
        return repository.findSchedule(loanId);
    }

    @GetMapping("/{loanId}/payments")
    public List<Payment> payments(@PathVariable long loanId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        loan(loanId);
        var end = to != null ? to : LocalDate.now(clock);
        var start = from != null ? from : end.minusMonths(12);
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }
        return repository.findPayments(loanId, start, end);
    }

    /**
     * The computed position of a loan: outstanding principal, arrears, days past due and
     * delinquency bucket. This is what the agent uses to answer "why is this loan overdue?".
     */
    @GetMapping("/{loanId}/position")
    public LoanPosition position(@PathVariable long loanId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        var loan = loan(loanId);
        var date = asOf != null ? asOf : LocalDate.now(clock);

        var schedule = repository.findSchedule(loanId).stream()
                .map(i -> new ScheduledInstalment(i.seq(), i.dueDate(), i.principalDue(), i.interestDue()))
                .toList();
        var payments = repository.findPayments(loanId, loan.startDate(), date).stream()
                .map(p -> new PaymentRecord(p.paidOn(), p.amount()))
                .toList();

        return DelinquencyCalculator.calculate(loan.principal(), schedule, payments, date);
    }
}
