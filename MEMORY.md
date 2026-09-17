# Project Memory

## Current baseline

- Current test build is `1.20.1-2.3.0`; local production build and 18 real-Minecraft-class regression assertions passed on 2026-09-18. Both HMCL instances have matching Quark/Zeta installed; gameplay acceptance is pending. No 2.3.0 public release has been published.
- Quark ancient tome candidates come exclusively from registered ItemListing generators. Only tome exchanges include stack data in deduplication; reflection and synthetic appended offers are removed. Verified Quark versions: Forge 4.0-462 and NeoForge 4.1-485. Enumeration assumes their single bounded random selection and warns above 16384 deterministic probes.


- The `1.20.1` branch targets Forge and Java 17.
- The latest published release version is `1.20.1-2.2.0`.
- MCA compatibility is isolated in `McaInteractionHandler`: it listens to `EntityInteractSpecific` only when the `mca` mod is loaded and the target entity uses the `mca` registry namespace.
- Existing generic entity-interaction handlers remain the fallback and the complete no-MCA behavior path.
- MCA is intentionally not a compile-time or declared runtime dependency.

## Verification baseline

- The earlier MCA compatibility patch was scoped to its compatibility class; the Quark 2.3.0 change is scoped to QuarkCompat and the two trade-selection classes, plus version metadata.
- MCA compatibility was accepted after successful in-game testing on 2026-09-15.

## Release history

- The mistakenly published `2.1.3` release and tag were withdrawn on 2026-09-15 and replaced by the intended minor release `2.2.0`.

## Environment risk

- The Java NIO loopback workaround uses JDK_JAVA_OPTIONS with jdk.net.unixdomain.tmpdir=C:\Users\LostPatrol_2\.java-unixdomain. Local 1.20.1-2.3.0 build/reobfuscation succeeds offline; online dependency resolution can stall under the current network setup.

## Automated distribution

- Both Minecraft branches carry `.agents/skills/tradetweaks-release/SKILL.md` and the `publish-platforms.yml` workflow. Published GitHub Releases distribute their attached production JARs independently to CurseForge and Modrinth; absent branches are not built or published.
- Upload all intended JARs to a draft before publishing. Each JAR carries its own Minecraft/mod version; platform changelogs use the Release body. Manual dispatch supports dry runs and targeted recovery of historical or failed uploads.
- Repository project variables are configured: CurseForge `1316814`, Modrinth `P5pFKnk6`. Both platform Tokens passed live uploads on 2026-09-15: `1.20.1-2.2.0` (run `34990258384`, CurseForge `8887172`, Modrinth `GTvgLvTw`) and `1.21.1-2.2.1` (run `34990262760`, CurseForge `8887176`, Modrinth `1wk4HggX`). Modrinth versions are listed and downloaded SHA-256 hashes match GitHub assets. CurseForge accepted both uploads; public availability/review status could not be confirmed because file pages were inaccessible. `1.21.1-2.2.0` was explicitly excluded.
- Workflow-created Releases using the built-in `GITHUB_TOKEN` require an explicit dispatch or different authorized publishing token. Check remote acceptance before retrying an ambiguous upload; automatic deduplication is not guaranteed.
- Invoking `tradetweaks-release` for a release now authorizes agent-owned build, GitHub draft/asset/publication operations and platform follow-through; the user does not perform routine publishing UI steps. Editing the skill alone does not authorize a release.
- `build.yml` supports API dispatch for on-demand builds. Release artifacts must match the selected commit SHA. Omitted branch scope defaults to the active task/current Minecraft branch; version numbers are not invented or used to overwrite existing releases.
- Platform display names use `Trade Tweaks <minecraft>-<mod-version>` without loader parentheses. Before release, compare the same platform/branch historical naming and changelog structure; explicit user formatting wins. Both 2.2.x releases were renamed in place on both platforms; Modrinth readback verified and CurseForge update-file API accepted the changes.
