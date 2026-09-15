# Project Memory

## Current baseline

- The `1.21.1` branch targets NeoForge and Java 21.
- The current release version is `1.21.1-2.2.0`.
- MCA compatibility is isolated in `McaInteractionHandler`: it listens to `EntityInteractSpecific` only when the `mca` mod is loaded and the target entity uses the `mca` registry namespace.
- The compatibility handler is explicitly registered on `NeoForge.EVENT_BUS`; existing generic entity-interaction handlers remain the fallback and the complete no-MCA behavior path.
- MCA is intentionally not a compile-time or declared runtime dependency.

## Verification baseline

- Compatibility builds must retain byte-identical existing production jar entries outside the intended compatibility class and entrypoint-registration changes.
- MCA compatibility was accepted after successful in-game testing on 2026-09-15.

## Release history

- The mistakenly published `2.1.3` release and tag were withdrawn on 2026-09-15 and replaced by the intended minor release `2.2.0`.

## Environment risk

- On 2026-09-15, local Gradle startup was blocked before project configuration by a host Java NIO loopback connection failure. GitHub Actions is the authoritative full Gradle build fallback until the host networking issue is resolved.
