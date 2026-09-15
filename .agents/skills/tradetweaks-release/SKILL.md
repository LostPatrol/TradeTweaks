---
name: tradetweaks-release
description: Prepare, publish, verify, or recover TradeTweaks GitHub Releases and their automated CurseForge and Modrinth distribution, including single or dual Minecraft branch releases.
---

<!-- Project-specific release procedure; automated uploads are implemented by GitHub Actions. -->
# TradeTweaks release

Read `publish/AUTOMATED_RELEASE.md` from the repository root for configuration links and recovery steps.

- Work only on the branches included in the user's release request. `1.20.1` uses Forge / Java 17; `1.21.1` uses NeoForge / Java 21. A Release tag points to one commit, but its attached JARs can include builds from both branches.
- Read `MEMORY.md`, repository instructions and each selected branch's `gradle.properties`. Build and verify each selected branch at its intended commit. Keep existing local changes; do not merge the Minecraft implementations together.
- Keep `publish/changelog-<version>.md` concise and player-facing. Set the GitHub Release body to the corresponding changelog content; the workflow forwards that exact body to both platforms. If branch versions differ, include both branch-specific change sections in the Release body.
- Create a draft Release, attach all intended production JARs named `tradetweaks-<minecraft>-<mod-version>.jar`, verify their identities and then publish the draft. Never publish first and upload the other branch later: `release.published` snapshots the assets once. Never infer both branches from a shared tag or silently build the omitted branch.
- The `.github/workflows/publish-platforms.yml` workflow must already exist at the tagged commit. Keep its scripts, workflow and this skill synchronized across both existing branches. Manual dispatch additionally requires the workflow on the default branch.
- Publish via GitHub API using the existing user-authorized authentication; this machine has no `gh`. A Release created with a workflow's built-in `GITHUB_TOKEN` does not trigger another release workflow; use a suitable PAT / GitHub App token or explicitly dispatch the publishing workflow from that automation.
- The workflow publishes automatically once the user-authorized GitHub Release is published. No additional per-platform approval is required for this configured flow. Do not publish unrelated releases or create a live test release merely to test the configuration.
- Check every selected branch/platform job separately, including the recorded platform URLs. Report an upload accepted for review separately from a publicly downloadable release.
- For failure recovery, first inspect the remote platform and logs for an accepted upload. A timeout can occur after acceptance. Use **Re-run failed jobs**, or manual dispatch with a specific tag, branch and platform and `dry_run=false`, only for confirmed missing uploads. Do not rerun successful destinations or promise automatic deduplication.
- Run `node --test .github/scripts/release-plan.test.cjs` when changing selection logic. Use manual dry runs against existing single and dual Release assets for download and embedded metadata checks. Do not expose token values. Maintain the task report and project memory after code changes.
