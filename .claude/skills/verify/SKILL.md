---
name: verify
description: Stages 5 and 6 of the repo workflow. Use when a slice's implementation is done, to run all checks, self-review the diff and open a pull request with base main.
---

# Verification and pull request

## Verify

1. Confirm the current branch is not `main` and is up to date with `origin/main` (rebase or merge `origin/main` if needed and re-run tests).
2. Run the full test suite, build and lint for **every area the diff touches** (commands in `CLAUDE.md`). Record the results.
3. Check every acceptance criterion in the slice spec is ticked and names its test.
4. Read the full diff (`git diff origin/main...HEAD`) as a reviewer: no debug code, no secrets, no unrelated changes, no weakened tests, docs updated.
5. If anything fails, go back to `implement`. Don't open the PR with failing checks.

## Open the pull request

1. Push the branch: `git push -u origin <branch>`.
2. Open a PR with **base `main`** and head `<branch>`, titled `[P<n>] <slice name>`, with the body filled in from `.github/pull_request_template.md`: spec link, what and why, criteria-to-tests table, verification checklist, deferred items.
3. Put the PR link in the slice spec and set **Status: in review**.
4. **Never merge the PR.** Tell the owner it is ready for review. Address review comments with new commits on the same branch.
