<!-- GitHub API operations needed to execute the release skill without user UI steps. -->
# GitHub execution reference

Repository: `LostPatrol/TradeTweaks`. Use PowerShell, Git and GitHub REST APIs; `gh` is not installed. Keep temporary manifests, downloaded artifacts, request bodies and logs in `agent/codex`. Never save credentials there.

## Authentication and preflight

Use an available authenticated GitHub connector when it supports the operation, otherwise the existing Git credential helper. Capture `git credential fill` output into a variable using `protocol=https`, `host=github.com` and a terminating blank line; parse the password in memory, never print the credential output. Send authentication only to GitHub API/upload endpoints; do not forward it to redirected signed artifact URLs.

Use JSON request bodies serialized by PowerShell `ConvertTo-Json`, `Accept: application/vnd.github+json`, and a supported `X-GitHub-Api-Version`. Upload binaries with `Invoke-WebRequest -InFile`, not JSON or multipart wrapping. Do not interpolate Release text into executable command strings.

- Check repository permissions and selected remote branch SHAs before writing.
- List `/repos/LostPatrol/TradeTweaks/actions/secrets` and `/actions/variables` to verify names and project IDs. A secret name being present does not prove the platform Token is valid.
- Existing GitHub credentials are separate from the two platform Secrets; use the established GitHub authentication without requiring another token solely for this skill. Report an actual authorization failure if the credential lacks the required operation.

## Build and artifact operations

- `POST /repos/LostPatrol/TradeTweaks/actions/workflows/build.yml/dispatches` with `{ "ref": "1.21.1" }` (or `1.20.1`). The Build workflow supports dispatch without additional inputs.
- Find the new `workflow_dispatch` run via `/actions/workflows/build.yml/runs`, matching branch, expected `head_sha` and dispatch time. Record its ID; poll `/actions/runs/{run_id}` with bounded waits and continue user-facing progress updates.
- Require `conclusion=success`, then list `/actions/runs/{run_id}/artifacts` and retrieve the unexpired `tradetweaks-{run_id}` artifact using `/actions/artifacts/{artifact_id}/zip`. Follow the signed download redirect without carrying the API Authorization header.
- Extract under a unique `agent/codex` staging directory. Match the actual production JAR against that branch's `gradle.properties`; exclude sources/dev/javadoc artifacts.

## Draft, upload, publish

1. Look up `/releases/tags/{encoded_tag}` and, when needed, list authenticated `/releases` to find a draft. Resolve any existing Git tag to its commit. Do not treat authorization/server failures as proof the tag or Release is absent.
2. `POST /repos/LostPatrol/TradeTweaks/releases` with `tag_name`, `target_commitish` set to the full intended SHA, `name`, exact `body`, `draft=true`, the intended `prerelease` value, and `generate_release_notes=false`. Use the returned ID and `upload_url`.
3. Remove the URI template suffix from `upload_url`; append `?name=` with a URI-encoded JAR filename. POST each raw JAR as `application/java-archive`. Before resuming an interrupted upload, check existing assets and hashes. An ambiguous upload response is not permission to delete and re-upload a file blindly.
4. `GET /releases/{release_id}` and `/releases/{release_id}/assets` to verify the complete expected production asset set and checksums. Confirm the body matches the changelog and every selected branch is present.
5. `PATCH /releases/{release_id}` with `{ "draft": false }`. Verify the returned Release is published and record its `html_url` and `published_at`. This is an agent-owned operation, not a user handoff.

## Platform synchronization

Inspect `/actions/workflows/publish-platforms.yml/runs` and `/actions/runs/{run_id}/jobs`; correlate the tag, event, time and expected branch/platform jobs. Inspect logs for returned platform IDs/URLs and failures. Query public platform version/file pages when available to distinguish review from public download.

If explicit recovery is necessary, `POST /actions/workflows/publish-platforms.yml/dispatches` with `ref` set to a branch containing the workflow and inputs such as `{ "tag": "2.2.1", "branch": "1.21.1", "platform": "modrinth", "dry_run": "false" }`. Match the new run before waiting. Never dispatch both platforms again when one has already succeeded. Use `dry_run=true` only when the user's task is validation, not as a substitute for the requested release.

Official references: [Releases](https://docs.github.com/en/rest/releases/releases), [Release assets](https://docs.github.com/en/rest/releases/assets), [Workflow dispatch](https://docs.github.com/en/rest/actions/workflows#create-a-workflow-dispatch-event), [Artifacts](https://docs.github.com/en/rest/actions/artifacts).
