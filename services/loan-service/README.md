# loan-service

Java 25 / Spring Boot 4.1 service holding the bank's deterministic domain logic: customers, loans, repayment schedules, payments and the computed **loan position** (outstanding principal, arrears, days past due, delinquency bucket).

> **Draft:** this code has not been compiled or run yet. First task of Phase 1 is to build it, add the Maven wrapper and tests.

## Endpoints

| Method | Path | Returns |
| --- | --- | --- |
| GET | `/api/customers/{id}` | Customer |
| GET | `/api/customers/{id}/loans` | Loans of the customer |
| GET | `/api/loans/{id}` | Loan |
| GET | `/api/loans/{id}/schedule` | Repayment schedule |
| GET | `/api/loans/{id}/payments?from=&to=` | Payments (default: last 12 months) |
| GET | `/api/loans/{id}/position?asOf=` | Computed position via `DelinquencyCalculator` |

## Seed data (synthetic)

| Customer | Loan | Situation |
| --- | --- | --- |
| 10234 | LN-5001 | Missed the two most recent instalments, so overdue |
| 10235 | LN-5002 | All instalments paid, so current |
| 10236 | LN-5003 | Latest instalment half paid, so early arrears |

## Run locally

```bash
mvn spring-boot:run   # needs Java 25 and PostgreSQL on localhost:5432
```
