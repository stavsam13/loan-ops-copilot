-- Synthetic demo data. All names, numbers and documents are fictional.
-- Dates are relative to the day the migration runs, so the demo always has recent history.
-- The data-generator will replace this with a larger, messier dataset.

INSERT INTO customers (id, first_name, last_name, email, segment, relationship_officer) VALUES
    (10234, 'Eleni',  'Markou',   'eleni.markou@example.com',  'RETAIL', 'officer.anna'),
    (10235, 'Jonas',  'Becker',   'jonas.becker@example.com',  'RETAIL', 'officer.anna'),
    (10236, 'Sofia',  'Ricci',    'sofia.ricci@example.com',   'SME',    'officer.ben');

-- Each loan started 10 months ago: 10 instalments have fallen due (fewer before the 5th of the month).
INSERT INTO loans (id, loan_number, customer_id, product_type, principal, annual_interest_rate,
                   term_months, start_date, currency, agreement_document_id) VALUES
    (5001, 'LN-5001', 10234, 'PERSONAL', 12000.00, 0.0600, 12,
        (date_trunc('month', current_date) - interval '10 months')::date, 'EUR', 'AGR-5001'),
    (5002, 'LN-5002', 10235, 'PERSONAL', 24000.00, 0.0480, 24,
        (date_trunc('month', current_date) - interval '10 months')::date, 'EUR', 'AGR-5002'),
    (5003, 'LN-5003', 10236, 'SME_TERM', 36000.00, 0.0720, 36,
        (date_trunc('month', current_date) - interval '10 months')::date, 'EUR', 'AGR-5003');

-- Straight-line principal, interest on the declining balance, due on the 5th of each month.
INSERT INTO instalments (loan_id, seq, due_date, principal_due, interest_due)
SELECT l.id,
       k,
       (l.start_date + make_interval(months => k) + interval '4 days')::date,
       round(l.principal / l.term_months, 2),
       round((l.principal - (l.principal / l.term_months) * (k - 1)) * l.annual_interest_rate / 12, 2)
FROM loans l, generate_series(1, l.term_months) AS k;

-- 10234 / LN-5001: paid on time, then missed the two most recent instalments -> overdue.
INSERT INTO payments (loan_id, paid_on, amount, channel, reference)
SELECT i.loan_id, i.due_date - 1, i.principal_due + i.interest_due, 'DIRECT_DEBIT', 'DD-' || i.loan_id || '-' || i.seq
FROM instalments i
WHERE i.loan_id = 5001
  AND i.seq <= (SELECT count(*) FROM instalments WHERE loan_id = 5001 AND due_date <= current_date) - 2;

-- 10235 / LN-5002: every due instalment paid -> current.
INSERT INTO payments (loan_id, paid_on, amount, channel, reference)
SELECT i.loan_id, i.due_date, i.principal_due + i.interest_due, 'DIRECT_DEBIT', 'DD-' || i.loan_id || '-' || i.seq
FROM instalments i
WHERE i.loan_id = 5002 AND i.due_date <= current_date;

-- 10236 / LN-5003: paid in full except the latest instalment, which was paid only half -> early arrears.
INSERT INTO payments (loan_id, paid_on, amount, channel, reference)
SELECT i.loan_id,
       i.due_date + 2,
       CASE WHEN i.due_date = (SELECT max(due_date) FROM instalments WHERE loan_id = 5003 AND due_date <= current_date)
            THEN round((i.principal_due + i.interest_due) / 2, 2)
            ELSE i.principal_due + i.interest_due END,
       'TRANSFER',
       'TR-' || i.loan_id || '-' || i.seq
FROM instalments i
WHERE i.loan_id = 5003 AND i.due_date <= current_date;
