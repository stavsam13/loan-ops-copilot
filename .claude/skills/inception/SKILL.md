---
name: inception
description: Stage 1 of the repo workflow. Use when starting a new feature or roadmap phase, to agree on the problem, users, success criteria and scope before any design or code.
---

# Inception

Goal: agree on **what** and **why** with the owner. No design, no code.

1. Read `README.md`, `CLAUDE.md` and `docs/workflow.md` (stage 1) and the roadmap phase this feature belongs to.
2. Pick a kebab-case feature name and create `docs/specs/<feature>/inception.md` from `docs/specs/_templates/inception.md`.
3. Draft every section from what you know. Keep success criteria measurable.
4. Ask the owner the open questions, **one at a time**, and update the doc with each answer.
5. When the owner agrees, set **Status: agreed**.
6. Commit the doc on a `chore/<feature>-inception` branch (never on `main`), push and open a PR with base `main`, or add it to the first slice's branch if the owner prefers.
7. Finish by proposing the next step: the `breakdown` skill.
