---
name: breakdown
description: Stage 2 of the repo workflow. Use after an inception doc is agreed, to split the feature into small, vertical, independently mergeable slices with acceptance criteria and test plans.
---

# Breakdown

Goal: turn an agreed inception into ordered **vertical slices**.

1. Read `docs/specs/<feature>/inception.md` and `docs/workflow.md` (stage 2). Stop if the inception is not agreed.
2. Propose slices. Each slice must be:
   - **vertical** (all layers it needs, not one layer for the whole feature),
   - **independently mergeable** (`main` keeps working),
   - **about one day** of work and one PR,
   - **testable** through Given / When / Then acceptance criteria.
3. Write `docs/specs/<feature>/breakdown.md` from the template: the ordered table, dependencies and why this order.
4. For each slice, create `slice-NN-<name>.md` from `docs/specs/_templates/slice.md` with goal, acceptance criteria and test plan. Leave the grilling log empty.
5. Show the owner the slice table and adjust until they agree.
6. Commit on a `chore/<feature>-breakdown` branch, push, open a PR with base `main`.
7. Finish by proposing the next step: the `grill` skill on slice 01.
