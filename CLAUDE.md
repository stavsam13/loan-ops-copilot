# CLAUDE.md: rules for AI assistants working in this repo

Loan Operations Copilot: an AI assistant for loan officers. Java 25 / Spring Boot 4.1 domain services, Python LangGraph agent with MCP tools, PostgreSQL + pgvector, React UI, deployed on AWS. See `README.md` for the overview.

These rules apply to every AI session (Claude Code, Claude, any agent) and to humans. When a rule here conflicts with a request, say so and ask before proceeding.

## Git and pull request rules (non-negotiable)

1. **Never commit or push directly to `main`.** The only exception is when the repo owner explicitly says so in the current conversation, naming `main`. A general "go ahead" or "push it" does not count.
2. **One coding slice = one branch.** Create it from the latest `origin/main`:
   - `slice/<phase>-<short-name>` for feature slices, for example `slice/p1-loan-position-tests`
   - `fix/<short-name>` for bug fixes
   - `chore/<short-name>` for docs, tooling and config
3. **Every branch ends in a pull request with base `main`.** Fill in `.github/pull_request_template.md`. Link the slice spec.
4. **Never merge your own PR.** The repo owner reviews and merges.
5. **Never force-push** to a branch someone else has pushed to, and never to `main`.
6. **Small commits with clear messages** (imperative mood, explain why). Keep a PR to one slice; if it grows, split it.
7. **Never commit secrets.** `.env` stays local; only `.env.example` is committed.

## The workflow

Every piece of work goes through the stages in `docs/workflow.md`:

**Inception → Breakdown → Grilling → Implementation (TDD) → Verification → PR**

Each stage has a skill in `.claude/skills/` (`inception`, `breakdown`, `grill`, `implement`, `verify`). Don't skip stages. Small fixes may compress inception and breakdown into the slice spec, but grilling and tests are never skipped.

No production code is written before the slice's grilling session is recorded in its spec.

## Testing rules

Full strategy in `docs/testing.md`. In short:

- Every acceptance criterion maps to at least one automated test.
- Write the failing test first, then the code (TDD). A bug fix starts with a test that reproduces it.
- All tests must pass locally before a PR is opened; CI must be green before merge.
- Never delete or weaken a test to make it pass. If a test is wrong, explain why in the PR.

## Domain rules

- **The LLM never does the maths.** Balances, arrears, days past due and risk scores are computed by deterministic Java code. The agent calls tools and explains results.
- **Every answer cites its evidence:** the API for figures, document and clause for contract or policy text.
- **Access control is enforced in the services, never only in prompts.**
- **Synthetic data only.** Never use real customer, bank or employer data. No real bank names or branding.

## Commands

| Area | Build / test |
| --- | --- |
| Java loan-service | `cd services/loan-service && mvn verify` (Java 25) |
| Python agent | `cd agent && uv run pytest` |
| UI | `cd ui && npm test` |
| Evals | `cd evals && uv run pytest` |

Some areas are not built yet; check that the directory has code before running its command.
