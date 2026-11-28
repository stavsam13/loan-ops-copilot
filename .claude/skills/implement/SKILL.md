---
name: implement
description: Stage 4 of the repo workflow. Use to implement a grilled slice test-first on its own branch, never on main.
---

# Implementation (TDD)

1. **Preconditions.** The slice spec has **Status: grilled** and an empty list of unanswered questions. If not, stop and run `grill`.
2. **Branch.** `git fetch origin`, then `git switch -c slice/p<n>-<name> origin/main` (or switch to the existing slice branch). Confirm with `git branch --show-current` that you are **not** on `main` before any commit.
3. **Red → green → refactor, one acceptance criterion at a time:**
   1. Write a test named after the behaviour in the criterion. Run it and see it fail for the right reason.
   2. Write the minimum code to make it pass. Run the area's whole suite.
   3. Refactor with the tests green.
   4. Commit (imperative message explaining why), and tick the criterion in the spec with the test name.
4. **Follow the rules** in `CLAUDE.md` and `docs/testing.md`: the LLM never does the maths, money as `BigDecimal`, fixed clocks, no real network or LLM calls in unit tests, synthetic data only.
5. **If reality differs from the plan**, update the spec's **Implementation notes**. If the change is significant, stop and ask the owner.
6. Push the branch regularly (`git push -u origin <branch>`). Never push to `main`.
7. Finish by proposing the next step: the `verify` skill.
