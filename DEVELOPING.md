<!-- Maintainer guide for the shared gameplay sources and loader builds. -->
# Development

Each Minecraft version stays on its existing branch: `1.20.1` targets Forge and Fabric, and `1.21.1` targets NeoForge and Fabric. Do not copy gameplay code between Minecraft versions without adapting their vanilla APIs.

## Source layout

- `src/main/java` contains shared gameplay, GUI and packet data, plus the native loader entrypoints/adapters.
- `fabric/src/main/java` supplies Fabric entrypoints and adapters. `prepareSharedSources` copies the shared sources into `fabric/build/generated/sources/shared/java/main`, excluding native implementations. Compilation and IDEA synchronization run this task automatically; edit the originals in `src/main/java`, never generated copies. Separate source roots prevent IDEA from assigning native classes to the Fabric module.
- The platform seam handles registration, events, configuration, entity data and loader metadata. Keep gameplay rules in shared classes; keep loader-specific hooks in adapters.
- `src/main/resources` holds shared assets/data. Fabric adds its metadata, access widener, mixins and loader-specific recipe override under `fabric/src/main/resources`.

When adding a loader adapter with the same class name, update the exclusion list in `fabric/build.gradle`. When changing shared behavior, verify both loaders on that branch. Client-only classes must not be loaded by dedicated-server code.

## Build

Use JDK 21 to run Gradle. Also install JDK 17 for Minecraft 1.20.1; the Gradle Java toolchain selects the game's target version.

```powershell
.\gradlew.bat :build
.\gradlew.bat :fabric:build
node --test .github/scripts/release-plan.test.cjs
```

The first command builds Forge/NeoForge into `build/libs`; the second builds Fabric into `fabric/build/libs`. Use the remapped production JAR, excluding `sources`, `dev` and `javadoc` artifacts. Filenames include the Minecraft version, mod version and loader, for example `tradetweaks-1.21.1-2.4.0-fabric.jar`. Use separate development game directories for each loader and preserve existing saves.

Reload the Gradle project in IDEA after changing build scripts. Loom generates the `Trade Tweaks Fabric client/server` run/debug configurations during synchronization. The Gradle panel also exposes uniquely named `:fabric:runFabricClient` and `:fabric:runFabricServer` tasks, avoiding hidden tasks with names matching the native loader. From PowerShell, use `.\gradlew.bat :fabric:runFabricClient --no-configuration-cache`. The native client remains `:runClient`.

## Gameplay checks

The dedicated GameTest source set in `gameplay-tests.gradle` exercises shared gameplay against real server entities. It is excluded from production JARs and the ordinary JUnit test task. Run `runGameTestServer` for the native loader and `:fabric:runGameplayTest` for Fabric, with `--no-configuration-cache` for the development launcher tasks.

Checks cover selection sessions and payloads, costs, restocking, conversion persistence, raid rewards and the potion recipe; Fabric additionally checks its early interaction callback. Before release, also check both clients visually: wheel/scroll controls, world highlights, trade search and chat icons with chat open and closed. Test third-party integrations on the exact loader/version being released.

## Release maintenance

`build.yml` builds both loaders on the current branch and validates release tooling. `publish-platforms.yml` distributes the exact assets of an approved, published GitHub Release to CurseForge and Modrinth. Manual runs default to a dry run; use the Minecraft, loader and platform filters for targeted recovery. Do not publish or dispatch a real upload without release approval.

The embedded mod version remains `<minecraft>-<version>`. New platform version IDs append the loader; display names use `Trade Tweaks <minecraft>-<version> (Fabric/Forge/NeoForge)`. Legacy assets keep their historical names and identifiers. `update-platform-name.yml` accepts either identifier format for metadata maintenance.

Attach every intended production JAR before publishing. The planner allows one JAR per Minecraft/loader pair and the validator checks embedded loader metadata and mod version. Update `CHANGELOG.md` with player-facing changes and check destination-specific predecessor releases before final publication.
