---
name: tradetweaks-release
description: "Complete TradeTweaks releases end to end using user-chosen versions and channel-aware cumulative notes: build selected branches, publish GitHub assets, synchronize CurseForge and Modrinth, and verify every destination."
---

<!-- One-invocation release orchestration; Codex owns GitHub publication and platform follow-through. -->
# TradeTweaks release

## Invocation contract

A request to run this skill for a release authorizes the complete chain: prepare release metadata, build, commit/push the relevant changes on existing branches, create/upload/publish the GitHub Release, trigger platform synchronization, and verify the results. Perform those steps yourself; do not hand the user draft-creation, upload, publish, or workflow buttons to click. Do not stop at a draft or a dispatched workflow and call it complete. Existing session authorization covers the steps; no additional routine publication confirmation is needed.

A request to edit/explain this skill or to dry-run is not a request to publish a real version. Honor narrower user scope, including GitHub-only or preparation-only requests.

Read repository instructions and local `MEMORY.md` when it exists. Read [references/release-policy.md](references/release-policy.md) whenever preparing or reviewing a release. For GitHub operations, also read [references/github-release.md](references/github-release.md).

## Resolve the release

- Before publishing, inspect predecessor releases for each Minecraft/loader/destination pair and record the reference in the report. From 2.4.0, JAR names are `tradetweaks-<minecraft>-<mod-version>-<loader>.jar`, platform version IDs are `<minecraft>-<mod-version>-<loader>`, and display names are `Trade Tweaks <minecraft>-<mod-version> (Fabric/Forge/NeoForge)`. Preserve historical unsuffixed identities when maintaining old releases. Explicit user formatting takes precedence.
- Use the requested branch and loader set; otherwise use the scope established in the active task. Never silently add another Minecraft branch. `1.20.1` supports Forge and Fabric / Java 17; `1.21.1` supports NeoForge and Fabric / Java 21.
- Version selection and release timing belong to the user. Use supplied versions or existing metadata unless the user explicitly delegates choosing a version. A delegated version choice does not authorize publication. Honor any approval requirement in the active request before creating a public Release or uploading to platforms.
- Branch versions may be equal, different or non-consecutive. Preserve the user's choice. Do not treat a skipped number as an error and do not reuse a published version for different content. A tag points to one selected branch commit, while attachments can contain builds with distinct versions.
- Build the GitHub Release body and platform release notes according to `references/release-policy.md`. Base notes on each selected branch's actual code and each destination's last published version. Never assume that a GitHub release, another Minecraft branch, or another platform was available to that audience. Never implement TODO items as part of release preparation.

## Build and publish GitHub automatically

1. Inspect local changes and remote heads; preserve unrelated work. Update only release-relevant metadata and documentation. Use existing branch worktrees under `agent/codex` as needed; do not merge game implementations or invent new branches. Commit/push relevant changes before taking each final build SHA.
2. Reuse a successful, unexpired Build artifact only when its source SHA matches the intended release commit. Otherwise build with the project's Gradle wrapper, or dispatch `build.yml` on each selected branch through GitHub API and wait for success. Verify the returned run's `head_sha` equals the intended commit; a branch race requires re-evaluation, not substitution with any latest successful build. Download the artifact from that exact run. A stopped or failed build is not a release artifact.
3. Select one production JAR per intended Minecraft/loader pair. Build the native output with `:build` and Fabric with `:fabric:build`. Check filename, embedded mod ID/version/loader metadata, and SHA-256. Reuse the release-plan and JAR-validation logic under `agent/codex`; stage only selected files. Record branch, loader, commit, build run, filename and hash. The embedded version excludes the loader suffix used by platform version IDs.
4. Check the Release/tag before creating anything. A tag must resolve to the intended selected commit; never move an existing release tag or replace published assets automatically. A matching draft can be resumed. An existing published Release goes through the recovery path, not recreation.
5. Create the GitHub draft through API with an explicit target commit SHA and exact changelog body. Upload all selected JARs. Re-fetch the draft and verify the complete asset set, uploaded state, sizes and hashes (download and hash if GitHub has no digest). Only then set `draft=false` through API. Codex performs this final publish operation itself.

## Follow through to CurseForge and Modrinth

- The `release.published` event starts `publish-platforms.yml`; its workflow and scripts must exist at the tagged commit. Keep release facilities on both existing branches; manual dispatch also requires the workflow on the default branch.
- Match the publishing run to this release tag and publication time. Wait for each intended branch/platform job, inspect failures and output URLs, and confirm that successful jobs actually produced platform version/file identifiers. A skipped job is not a successful upload.
- Releases created using a workflow's built-in `GITHUB_TOKEN` do not trigger another release workflow. Prefer the already available user-authorized GitHub API authentication. If the event is suppressed or the workflow is absent from a historical tag, check for an existing run and published platform files, then explicitly dispatch the workflow through API for only the confirmed missing destinations. Do not ask the user to do it.
- On partial failure, inspect remote versions first. Retry a confirmed missing upload for only the failed Minecraft/loader/platform tuple, at most once after fixing a recoverable cause. Manual dispatch supports a `loader` filter. If acceptance is uncertain after a timeout, stop retrying that destination and report the ambiguity. Never rerun successful destinations.
- GitHub Secrets are already configured under `CURSEFORGE_TOKEN` and `MODRINTH_TOKEN`; inspect names only. Their contents cannot be retrieved through GitHub. Use Actions to consume them and do not ask the user to paste them. Project variables are documented in the release guide.
- Report GitHub and each selected platform's actual outcome with links. Distinguish API acceptance / pending platform review from public availability. If pending review persists, report it explicitly rather than claim the entire release is publicly downloadable.
- Maintain the task report and project memory with release version, source commits, artifacts, workflow URLs and remaining failures. Commit/push those documentation changes without changing the release tag.

## Maintenance validation

For skill edits, run the skill-creator validator in the project validation environment. For release selection changes, run `node --test .github/scripts/release-plan.test.cjs`; for workflow edits, run actionlint. Use dry runs for configuration checks. Do not create a live test Release merely to validate this skill.
