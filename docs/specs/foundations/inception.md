# Inception: foundations (discovery, data model, first ADRs)

- **Roadmap phase:** P0
- **Status:** agreed
- **Date:** 2026-11-28

## Problem

A loan officer answering "how much does this customer owe, and why are they overdue?" pieces it together from several screens, PDF agreements and a credit policy, and has to do arithmetic by hand along the way. Before we build a copilot for that, we need a shared, written picture of the officer's real questions, where each answer's facts live, and how the system is shaped to answer them safely. Today the repo has a draft schema and an uncompiled loan-service, but no discovery note, no documented data model and no recorded architecture decisions. Without them every later phase re-argues the same choices (what "balance" means, which component owns the maths, how documents and clauses are identified, where it is hosted), and the agent has no stable contract to call.

## Users and jobs

| Persona | Question or job this feature serves |
| --- | --- |
| Loan officer | Their questions are captured, prioritised and mapped to a data source, so the copilot is built around real jobs rather than guesses |
| Repo owner (developer) | Decisions on stack, tool layer, maths ownership, retrieval and hosting are written down once, with alternatives and consequences, so later phases build on them |
| Future AI sessions / contributors | Can read the discovery note, data model and ADRs and pick up work without re-deriving context |
| Reviewer / demo audience | Can see why the system is built the way it is |

## Success criteria

- **Discovery note** (`docs/discovery.md`) lists at least 8 loan-officer questions, each with: priority (must / should / later), the roadmap phase that answers it, the data source (API, document, policy) and what evidence the answer must cite.
- **Glossary** in the discovery note defines every domain term used in answers (outstanding principal, arrears, days past due, delinquency bucket, credit balance, instalment, as-of date), so "balance" means one thing everywhere.
- **Data model** (`docs/data-model.md`) has an ER diagram (Mermaid) covering every entity needed for Phases 1–3 (customers, loans, instalments, payments, agreement documents/clauses, policy documents/clauses), matches the Flyway migrations exactly for the tables that exist today (document and clause entities are marked *planned* until Phase 2 migrates them), states money rules (type, scale, rounding, currency), and defines the document and clause identification scheme and citation format used by answers (for example `AGR-5001 §4.2`).
- **Seed scenarios as test oracles:** each seed customer's expected loan position (outstanding principal, arrears, days past due, bucket) is worked out by hand, expressed relative to the run date (the seed stays relative to `current_date`), with one fully worked example for a concrete as-of date. Phase 1 tests use their own fixed-date fixtures that mirror these scenarios, so they have an independent expected value.
- **ADRs** in `docs/adr/` in MADR format, one decision each, with context, options considered, decision and consequences, status *accepted* by the owner. Eight ADRs: (1) deterministic maths in Java, LLM only explains; (2) MCP as the tool layer between agent and services; (3) PostgreSQL + pgvector for both domain data and retrieval; (4) hosting: EC2 + docker-compose now, ECS Fargate + RDS as the target; (5) LLM provider and model, how to switch, cost cap; (6) agent framework: LangGraph + FastAPI; (7) Java 25 / Spring Boot 4.1 with JdbcClient and Flyway; (8) UI stack: React + Vite and the UI-to-agent protocol.
- **Roadmap** in `README.md` updated: Phase 0 ticked, and Phase 1 scope adjusted to anything the discovery changed.
- Phase 0 is docs only: no production code changes, except schema fixes the data-model review proves necessary (each one then becomes a Phase 1 slice, not done here).

## Constraints

- **Synthetic data only.** Discovery uses invented personas and questions drawn from public, general knowledge of loan operations (owner-reviewed, no interviews); no real customer, bank or employer data, no real bank names or branding.
- **Domain rules from `CLAUDE.md` are fixed inputs**, not up for debate in the ADRs: the LLM never does the maths, every answer cites evidence, access control lives in the services.
- **Small and readable:** each doc is short enough to read in one sitting; ADRs about one page.
- **Cost:** no cloud resources are created in Phase 0.

## Out of scope

- Compiling, testing or fixing the draft loan-service (Phase 1)
- Building the agent, MCP server, UI, ingestion or infra
- Writing the synthetic agreement and policy PDFs and the ingestion pipeline (Phase 2); Phase 0 only fixes how documents and clauses are identified and cited
- Detailed RBAC and audit design (Phase 5), beyond noting the `relationship_officer` hook in the data model
- A data-generator for a larger dataset

## Open questions for the owner

- [x] Where do the loan-officer questions come from? **Invented from general, public knowledge of loan operations**, drafted by AI and reviewed by the owner. No interviews, no real officers.
- [x] What does "outstanding balance" mean in an answer? **The headline figure is outstanding principal. Arrears and the next instalment appear next to it as separate, labelled figures from the position API; the agent never adds them together.** A payoff amount is not part of "balance"; if wanted later it becomes its own Java calculation.
- [x] Seed dates relative or fixed? **Keep the demo seed relative to `current_date`** so the demo always has recent history. Seed oracles are written relative to the run date (for example "10 instalments due, the last 2 unpaid"), and automated tests build their own fixtures with fixed dates and a fixed `Clock`.
- [x] Which ADRs beyond the first four belong in Phase 0? **All four candidates: LLM provider and model, agent framework, Java 25 / Spring Boot 4.1, UI stack.** Eight ADRs in total (see success criteria).
- [x] ADR format? **MADR** (context, decision drivers, options with pros and cons, outcome, consequences), numbered `docs/adr/NNNN-<title>.md`, with a MADR template committed in `docs/adr/`.
- [x] Clause identification decided here or in Phase 2? **Here.** The data model defines document and clause entities and a stable citation format (for example `AGR-5001 §4.2`, `POL-CREDIT §4`), because Java code already refers to policy sections.
