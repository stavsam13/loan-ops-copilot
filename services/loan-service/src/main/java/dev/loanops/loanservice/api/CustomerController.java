package dev.loanops.loanservice.api;

import dev.loanops.loanservice.persistence.LoanRepository;
import dev.loanops.loanservice.persistence.Records.Customer;
import dev.loanops.loanservice.persistence.Records.Loan;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final LoanRepository repository;

    public CustomerController(LoanRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{customerId}")
    public Customer customer(@PathVariable long customerId) {
        return repository.findCustomer(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
    }

    @GetMapping("/{customerId}/loans")
    public List<Loan> loans(@PathVariable long customerId) {
        customer(customerId); // 404 for unknown customers rather than an empty list
        return repository.findLoansByCustomer(customerId);
    }
}
