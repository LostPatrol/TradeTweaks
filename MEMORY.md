# Project Memory

## Current baseline

- Branch `1.20.1`, source `3a233dd`, version `1.20.1-2.4.0`: Forge and Fabric, Java 17. The root 1.21.1 checkout supports NeoForge and Fabric at source `4e77987`.
- Native dependency remains Forge `47.1.3`; Fabric uses Loader `0.18.4`, Fabric API `0.92.12+1.20.1`, and bundled FCAP `8.0.3`. Gradle remains 8.12.1 with Legacy ModDevGradle 2.0.78 / Loom 1.10.5.
- Keep this branch's NBT/FriendlyByteBuf APIs, raid-win END-tick polling and search prefix meanings (`#` tooltips, `$` tags). Do not blindly copy 1.21.1 implementations or README text.
- Native Forge production JAR must include reobfuscation and the nested MixinExtras 0.4.1 dependency. Resource expansion declares version inputs so incremental builds refresh mods.toml.
- The pre-existing 1.20.1 chat renderer still multiplies the inherited HUD pose; this port preserves that behavior. The 1.21.1 branch has an earlier correction. Visual chat checks remain relevant before release.

## Architecture and maintenance

- Each Minecraft version stays on its existing branch. The root build supplies the native loader; `fabric/` compiles the same gameplay sources with thin loader adapters. Keep rules in shared classes and hooks in `platform/` or Fabric adapters; update Fabric's source exclusion list when adding a replacement class.
- Fabric requires Fabric API on both client and server; Forge Config API Port is nested in the production JAR. Do not ship the Fabric dev JAR or test source sets.
- `gameplay-tests.gradle` provides native `:runGameTestServer`, Fabric `:fabric:runGameplayTest`, and `:runClientSmoke` / `:fabric:runClientSmoke`. Development launchers use `--no-configuration-cache`. Client smoke waits for the loading overlay to disappear and 40 stable title-screen ticks.
- Local production builds, real-server gameplay regressions (46 shared checks per target plus Fabric interaction dispatch), and client startup passed for all four targets. This does not establish visual GUI correctness or third-party modpack compatibility; exact evidence and remaining checks belong in the task report.
- Native MCA interaction hooks and Quark listing-generation logic remain in place. MCA is not a compile-time/runtime dependency. Previous native MCA testing was accepted on 2026-09-15; Fabric MCA/other integrations still need loader-specific playtesting.
- Quark Ancient Tome choices come only from registered ItemListing generators. Tome deduplication includes stack data; no synthetic offers/reflection. Previously checked Quark versions: Forge 4.0-462 and NeoForge 4.1-485. Bounded enumeration warns above 16384 deterministic probes.
- `agent/` is ignored. This branch still tracks `MEMORY.md`; keep it as the current development baseline, without execution logs.

## Release state and workflow

- Latest published release remains `2.3.0`. Source version `2.4.0` is prepared but UNRELEASED; the user explicitly requires approval before actual publication.
- New names: `tradetweaks-<minecraft>-<version>-<loader>.jar`; platform version ID: `<minecraft>-<version>-<loader>`; display name: `Trade Tweaks <minecraft>-<version> (Fabric/Forge/NeoForge)`. Historical unsuffixed assets retain their identities.
- `build.yml` builds both loaders and runs server GameTests plus release-tool checks. `publish-platforms.yml` distributes exact approved GitHub Release assets to CurseForge/Modrinth; manual runs default to dry-run and support Minecraft/loader/platform filters. Never repeat successful destination tuples without a reason.
- `tradetweaks-release` governs release execution within the user's current scope. A user may explicitly delegate version selection, as for 2.4.0; this does not override a requirement for publication approval. Editing the skill alone does not authorize publishing.
- Release notes are concise English unless instructed otherwise. Compare predecessors per Minecraft/loader/destination and include the union of undelivered player-facing changes. Upload all intended JARs to a draft before publishing.
- Repository variables: CurseForge project `1316814`, Modrinth project `P5pFKnk6`. Existing platform credentials last passed live uploads in September 2026; recheck at publication.
- Published 2.3.0: Forge source `af437a1`, NeoForge source/tag target `4d429f0`; sync run `35258867415`. Modrinth IDs `vyci7TF8` / `LOzZqZoC`; CurseForge IDs `8905730` / `8905729`. GitHub/Modrinth downloads were verified. CurseForge accepted uploads, but public visibility and its later changelog correction remained unconfirmed.
- `GITHUB_TOKEN`-created Releases may need an explicit authorized publishing dispatch because event recursion is suppressed. Do not assume an ambiguous request failed or blindly re-upload.

## Local environment

- Run Gradle with JDK 21; the 1.20.1 game/compiler use JDK 17. Fabric 1.20.1 JavaExec launchers explicitly select 17.
- Existing Java NIO workaround: `JDK_JAVA_OPTIONS=-Djdk.net.unixdomain.tmpdir=C:\Users\LostPatrol_2\.java-unixdomain`. Do not remove or alter system settings casually.
- This machine uses the system proxy at `127.0.0.1:10808`; Java dependency downloads may need per-process HTTP/HTTPS proxy properties. No global proxy changes are required.
- Offline builds can fail when launcher dependencies or NeoGradle asset indexes are absent. Use online resolution for missing files; avoid treating network stalls as source failures.
