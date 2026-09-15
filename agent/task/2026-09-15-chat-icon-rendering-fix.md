# Chat Icon Rendering Fix

Fix the Minecraft 1.21.1 trade broadcast item icons so they render in the HUD without requiring the chat screen to be open.

Requirements:

- Apply the fix after the completed MCA compatibility work without altering that compatibility path.
- Keep the rendering change minimal and limited to the incorrect chat icon pose transformation.
- Increment the patch version from `1.21.1-2.2.0` to `1.21.1-2.2.1`.
- Build and verify the release, then publish it from the `1.21.1` branch.
