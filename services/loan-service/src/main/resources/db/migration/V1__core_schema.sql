CREATE TABLE customers (
    id                   BIGINT PRIMARY KEY,
    first_name           VARCHAR(100) NOT NULL,
    last_name            VARCHAR(100) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    segment              VARCHAR(30)  NOT NULL,          -- RETAIL, SME
    relationship_officer VARCHAR(100) NOT NULL,          -- used for row-level access control later
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE loans (
    id                    BIGINT PRIMARY KEY,
    loan_number           VARCHAR(30)   NOT NULL UNIQUE,
    customer_id           BIGINT        NOT NULL REFERENCES customers (id),
    product_type          VARCHAR(30)   NOT NULL,         -- PERSONAL, MORTGAGE, SME_TERM
    principal             NUMERIC(14, 2) NOT NULL CHECK (principal > 0),
    annual_interest_rate  NUMERIC(6, 4) NOT NULL CHECK (annual_interest_rate >= 0),
    term_months           INT           NOT NULL CHECK (term_months > 0),
    start_date            DATE          NOT NULL,
    currency              CHAR(3)       NOT NULL,
    agreement_document_id VARCHAR(50)   NOT NULL          -- links to the agreement indexed for RAG
);
CREATE INDEX idx_loans_customer ON loans (customer_id);

CREATE TABLE instalments (
    loan_id       BIGINT         NOT NULL REFERENCES loans (id),
    seq           INT            NOT NULL,
    due_date      DATE           NOT NULL,
    principal_due NUMERIC(14, 2) NOT NULL,
    interest_due  NUMERIC(14, 2) NOT NULL,
    PRIMARY KEY (loan_id, seq)
);

CREATE TABLE payments (
    id        BIGSERIAL PRIMARY KEY,
    loan_id   BIGINT         NOT NULL REFERENCES loans (id),
    paid_on   DATE           NOT NULL,
    amount    NUMERIC(14, 2) NOT NULL CHECK (amount > 0),
    channel   VARCHAR(30)    NOT NULL,                    -- DIRECT_DEBIT, TRANSFER, BRANCH
    reference VARCHAR(60)    NOT NULL
);
CREATE INDEX idx_payments_loan_date ON payments (loan_id, paid_on);
