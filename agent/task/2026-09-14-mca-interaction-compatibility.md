# MCA Interaction Compatibility Fix

Implement a minimal MCA-specific `EntityInteractSpecific` compatibility path on the existing `1.20.1` and `1.21.1` branches.

Requirements:

- Preserve the existing interaction path when MCA is not installed.
- Intercept only MCA entities and Trade Tweaks villager tools.
- Execute the existing item interaction implementations instead of duplicating gameplay logic.
- Do not add a compile-time or runtime MCA dependency.
- Build and verify both branches.
- Prepare the user's HMCL 1.20.1 Forge and 1.21.1 NeoForge instances with the production jars and required MCA test dependencies without removing unrelated mods.
- Document manual verification steps and any remaining risks.
