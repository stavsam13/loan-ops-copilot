---
name: grill
description: Stage 3 of the repo workflow. Use before implementing a slice, to question the owner hard, one question at a time, about behaviour, edge cases, failure modes, security and tests, and record the answers in the slice spec.
---

# Grilling session

Goal: find the holes in a slice **before** code exists. You are a demanding senior reviewer at a bank, not a yes-man.

## Rules

- Ask **one question at a time** and wait for the answer. Never batch questions.
- Make each question concrete. Prefer "What should the position be when a payment arrives on the due date itself?" over "Any edge cases?".
- When an answer is vague, push back once with a specific example.
- Offer a recommended answer when you have one, so the owner can just confirm.
- Don't write production code during the session.

## Steps

1. Read the slice spec, its breakdown and inception docs, `CLAUDE.md` and the code the slice touches.
2. Prepare questions covering, at minimum:
   - **Behaviour:** the exact happy path; valid and invalid inputs.
   - **Edge cases:** empty data, boundaries (due date, month end, leap year, zero balance), duplicates, concurrency.
   - **Failure modes:** downstream service down, timeouts, bad data, LLM refusing or hallucinating.
   - **Domain correctness:** which policy or agreement clause defines the rule; rounding; currency.
   - **Security and access:** who may see or do this; what goes into the audit trail.
   - **Testing:** how each acceptance criterion will be proven; what test data.
   - **Scope:** what is deliberately left for later.
3. Ask them one by one. After each answer, append a row to the **Grilling log**.
4. Turn answers into **Decisions**, update the **Acceptance criteria** and **Test plan**, and list anything postponed under **Deferred** with a reason.
5. End when no question is unanswered. Summarise the decisions and changed criteria in a few lines, set **Status: grilled** and commit the spec on the slice branch (`slice/p<n>-<name>`, created from latest `origin/main`).
6. Finish by proposing the next step: the `implement` skill.
