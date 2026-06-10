/Users/maxwellmapako/Documents/Git/android-emojify/tmp/researcher-workflow-findings.md — unable to write from this session because no write/edit tool is available; suggested file contents below.

```md
# Research: GitHub Actions required checks and skipped workflows/jobs

## Summary
GitHub treats workflow-level skips and job-level skips very differently for required checks. If a workflow is skipped by trigger filters such as `paths`, GitHub leaves its associated checks **Pending**, which blocks automerge/merge; if a job inside an already-started workflow is skipped via `if:`, GitHub reports that job as **Success/skipped**, which is merge-friendly. For this repo, the safest pattern is an always-triggered `pull_request` workflow (and `merge_group` too, if merge queue is used) that computes changed areas first, then conditionally runs expensive jobs or reusable workflows, plus one stable always-run gate/noop job as the required check.

## Findings
1. **Workflow-level path filtering is incompatible with required checks.** GitHub documents that if a workflow is skipped due to path filtering, branch filtering, or commit-message skipping, the checks associated with that workflow remain **Pending**; if those checks are required, the PR is blocked from merging. Practically, this means `on.pull_request.paths` or `paths-ignore` should not be used on workflows whose checks are marked required. [Source](https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions)

2. **Job-level `if:` skips are safe for required checks.** GitHub documents that a skipped job shows “This check was skipped” and reports **Success**; GitHub’s protected branch docs also say required checks may be **successful, skipped, or neutral**. So an always-run workflow can safely skip expensive jobs with job-level `if:` without blocking merge, even if the job name is required. [Source](https://docs.github.com/en/actions/using-jobs/using-conditions-to-control-job-execution) [Source](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)

3. **Recommended pattern: always-triggered workflow + change-detection job + conditional heavy jobs.** The merge-friendly design is: trigger a lightweight workflow on every PR, run a cheap `changes` job, and use its outputs to guard later jobs with `if:`. `dorny/paths-filter` and `tj-actions/changed-files` are both designed for this exact in-workflow pattern—detect changes after the workflow starts, then run only the relevant jobs/steps. [Source](https://github.com/dorny/paths-filter) [Source](https://github.com/tj-actions/changed-files)

4. **A stable gate/noop job is the cleanest required check when real work is optional or dynamic.** If the actual work fans out into multiple optional jobs, matrix jobs, or reusable workflows, add one final always-run job such as `required-ci` that depends on them and reports a single predictable result. This keeps branch protection tied to a stable check name and avoids relying on workflow-level skips. `workflow_call` is useful for decomposition, but it does not change the underlying rule: the required entrypoint should still be an always-started workflow/job. [Source](https://docs.github.com/en/actions/using-workflows/reusing-workflows) [Source](https://docs.github.com/en/actions/using-jobs/using-conditions-to-control-job-execution)

5. **If merge queue is involved, required-check workflows must also listen to `merge_group`.** GitHub documents that workflows producing required checks for merge queue must run on the `merge_group` event; otherwise the queue cannot obtain the required status. In practice, many repos keep the same always-run CI entrypoint for both `pull_request` and `merge_group`, often being more conservative on `merge_group` (for example, running broader validation). [Source](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#merge_group) [Source](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/configuring-pull-request-merges/managing-a-merge-queue)

## Practical implications for this repo
1. **Do not put required CI behind workflow-level `paths` filters.** If this repo’s protected branch or automerge relies on those checks, they can stay Pending forever on unaffected-file PRs.
2. **Prefer one always-run PR CI workflow** that:
   - triggers on `pull_request` (and `merge_group` if merge queue is enabled),
   - runs a `changes` job first,
   - sets outputs for areas such as `emojify/**`, `contract/**`, `serializer/**`, `buildSrc/**`, Gradle files, GitHub workflow files, and docs-only changes,
   - conditionally runs Gradle jobs with job-level `if:`.
3. **Use a stable required gate job** like `required-ci` or `pr-gate` as the protected-branch required check. That job can:
   - `needs` the optional jobs,
   - run with `if: always()`,
   - succeed when work was legitimately skipped,
   - fail only when a needed job failed.
4. **If you want modular CI, use `workflow_call` behind the always-run entrypoint.** The caller workflow should remain the merge-facing required surface; reusable workflows can implement the actual module-specific work.
5. **For `merge_group`, err on the side of more validation.** Changed-file logic on queue branches can be trickier than on plain PRs, so a broader test set on `merge_group` is usually safer than aggressively skipping.

## Sources
- Kept: Workflow syntax for GitHub Actions (https://docs.github.com/en/actions/writing-workflows/workflow-syntax-for-github-actions) — primary source for path filters and skipped-workflow pending behavior.
- Kept: Using conditions to control job execution (https://docs.github.com/en/actions/using-jobs/using-conditions-to-control-job-execution) — primary source for skipped-job => success behavior.
- Kept: About protected branches (https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches) — authoritative source for required-check conclusions accepted by branch protection.
- Kept: Events that trigger workflows: `merge_group` (https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#merge_group) — primary source for merge queue workflow triggering.
- Kept: Managing a merge queue (https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/configuring-pull-request-merges/managing-a-merge-queue) — merge-queue requirements context.
- Kept: Reusing workflows (https://docs.github.com/en/actions/using-workflows/reusing-workflows) — official source for `workflow_call` composition pattern.
- Kept: dorny/paths-filter (https://github.com/dorny/paths-filter) — widely used changed-file gating action for job/step conditions.
- Kept: tj-actions/changed-files (https://github.com/tj-actions/changed-files) — alternative changed-file action with rich outputs.
- Dropped: generic blogs and forum posts — excluded because official GitHub docs and action READMEs were stronger evidence for the required-check semantics.

## Gaps
- I could not inspect this repo’s current branch protection or ruleset configuration, so I cannot confirm which exact check names are currently required.
- I could not verify from repo state whether merge queue/`merge_group` is already in use, or whether automerge is only standard PR automerge.
- I also could not confirm whether current required checks map to stable job names or to checks that vary by matrix/configuration.

## Remaining clarification questions
1. Which exact check names are currently marked required in branch protection/rulesets?
2. Is merge queue enabled, or only standard automerge on PRs?
3. Do you want docs-only changes to bypass all Gradle work, or still run a minimal smoke/format check?
4. Are there current matrix jobs or reusable workflows whose names make required-check configuration unstable?
```