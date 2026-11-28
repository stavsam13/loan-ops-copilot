# Testing strategy

Tests prove the copilot's two promises: **figures are exactly right** and **answers are backed by evidence**. Deterministic code is tested like any banking system; LLM behaviour is measured by evals.

## Principles

1. **Acceptance criteria drive tests.** Every criterion in a slice spec maps to at least one automated test, named after the behaviour it proves.
2. **Test first.** Write the failing test, then the code. A bug fix starts with a test that reproduces the bug.
3. **Deterministic by default.** Fixed clocks (`Clock` in Java), fixed seeds, no real network calls in unit tests, no real LLM calls outside evals.
4. **Money is exact.** Assert `BigDecimal` values with scale (`compareTo` or exact equality on scaled values), never floating point.
5. **Never weaken a test to make it pass.** If a test is wrong, fix it and explain why in the PR.

## Test pyramid per area

| Area | Unit | Integration | Tools |
| --- | --- | --- | --- |
| Java loan-service | Domain logic (`DelinquencyCalculator`, risk rules), pure Java, no Spring | Controllers + real PostgreSQL via Testcontainers; Flyway migrations run in tests | JUnit 5, AssertJ, Spring Boot test, Testcontainers |
| MCP server | Each tool's input validation and output mapping, loan-service mocked | Tool calls against a running loan-service in docker-compose | pytest, httpx mock transport |
| Agent (LangGraph) | Graph routing with a fake chat model returning scripted tool calls | End-to-end graph with real tools and a fake model | pytest |
| Retrieval / ingestion | Chunking and clause metadata extraction | Ingest sample PDFs into pgvector, query returns the expected clause | pytest, Testcontainers |
| UI | Components render citations, approvals and errors | Mocked agent API | Vitest, Testing Library |
| End to end | n/a | Key user journeys on the deployed stack (later phases) | Playwright |

## Evals (LLM behaviour)

Evals live in `evals/` and are not unit tests: they call a real model and score results.

- **Golden set:** questions with expected answers, expected tool calls and expected source clauses.
- **Metrics:** exact match on figures, citation correctness, retrieval recall@k, tool-selection accuracy, refusal correctness, LLM-as-judge for prose.
- Run in CI on PRs that touch `agent/`, `ingestion/` or prompts; results table committed to `evals/results/`.

## What every PR must show

- New or changed behaviour has tests.
- All suites for touched areas pass locally and in CI.
- Coverage of the domain layer (`services/loan-service/.../domain`) stays at 100% of branches; elsewhere, coverage is a signal, not a target.
