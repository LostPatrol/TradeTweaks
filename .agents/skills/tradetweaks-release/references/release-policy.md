<!-- TradeTweaks version ownership and channel-aware release-note policy. -->
# Release version and changelog policy

## Version ownership

The user decides every version value and when it changes. The release skill executes that decision; it does not recommend, calculate or apply a version increment during an automated release.

- Treat an exact version in the current request or prior active task as authoritative.
- Otherwise read each selected branch's `mod_version` as an existing user-owned value. Do not modify it automatically.
- If the request and branch metadata disagree, or a new Release tag cannot be selected without deciding a version, stop before build/publication and ask the user for the exact value.
- For a multi-branch release, accept equal or different branch versions exactly as supplied. Gaps are valid. Do not backfill a skipped version or independently add one to each branch.
- Reject reuse of an already published full artifact version for changed bytes. Existing-version requests use the verification/recovery path.

The full artifact version remains `<minecraft>-<mod-version>`, and the display name remains `Trade Tweaks <minecraft>-<mod-version>`.

## Determine the audience baseline

For every selected Minecraft branch, inspect the latest actually published version on GitHub Releases, CurseForge and Modrinth. Record the predecessor found for each destination. A repository tag, JAR built on another branch, or publication on another destination does not prove that users on this destination received it.

Compare the target branch with each destination's predecessor and retain only user-facing changes that are present in that target branch. Do not infer applicability from version numbers alone. In particular:

- If a destination skipped an intermediate version, include all applicable changes from that skipped version in the next release notes.
- If a change never entered the target branch, omit it even when its version number lies between the predecessor and target.
- If a destination has no previous release, write a concise initial/current feature summary rather than an invented delta.

## Compose release notes

The GitHub Release body is the source forwarded by the current platform workflow, so it must cover every selected branch and destination audience.

- When all selected destinations for a branch share the same predecessor, describe the delta from that predecessor.
- When destination histories differ, use the union of applicable changes since the oldest destination predecessor. Repeating a previously announced item on a more current destination is acceptable; omitting a change for users who skipped a release is not.
- For a single branch, one concise changelog section is sufficient.
- For multiple branches with different predecessors or change sets, use explicit `## Minecraft 1.20.1` and `## Minecraft 1.21.1` sections. Under each heading, describe that branch's cumulative audience delta. Shared changes may appear in both sections when they apply to both artifacts.
- Preserve the repository's player-facing `ADD`, `CHANGE` and `FIX` style. Describe outcomes, not implementation details. The body must not imply that an omitted branch received a release.

Before publication, perform a coverage check: for each `(Minecraft branch, destination)` pair, list its predecessor and confirm every applicable user-visible change between that predecessor and the target is present in the release body. Record this matrix in the task report.

## Current historical example

GitHub contained a `1.21.1-2.2.0` asset, while CurseForge and Modrinth advanced from `1.21.1-2.1.2` directly to `1.21.1-2.2.1`. Therefore the platform audience for `1.21.1-2.2.1` needed both the MCA interaction fix from 2.2.0 and the chat-icon fix from 2.2.1. A GitHub-only delta from 2.2.0 was insufficient as the shared platform text.
