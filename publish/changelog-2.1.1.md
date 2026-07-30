# 2.1.1 CHANGELOG

# NEW

+ Added a dedicated Trade Tweaks advancement tab with 17 advancements covering all Emerald Wand modes, trade selection, consumable villager tools, and trade broadcasts.
+ Successful mod actions now feed event-driven advancement criteria, including an all-six-modes challenge and Mending discovery through trade broadcasts.
+ Added a live JEI-style replacement search field to the trade selection screen, including prefix filters, combined/excluded terms, and keyboard-focused text editing.

# FIX

+ Fixed Market Dominance failing to unlock for crafted Nether Star-upgraded Emerald Wands on Minecraft 1.20.1 because the advancement predicate expected a different NBT numeric type.
+ Fixed long trade-selection candidate lists reaching the visual bottom of the scrollbar before their final entries were displayed.
+ Trade replacement candidates are now generated from the server's fully loaded trade tables, adding generic support for modded villager professions and data-driven trades.
+ Randomized category-based trade factories now expose every distinct item variant they can generate instead of showing only one random example, covering modded groups such as logs and signs without profession-specific rules.
+ Candidate generation now uses the real villager context without consuming the villager's random sequence.
+ Trade levels are resolved from the villager's actual career unlock order. Both trade slots unlocked at the same level now share the same candidate pool.
+ Librarian replacement options still include every tradeable enchantment at its maximum level.
+ Trade replacement now uses short-lived server sessions and validates the selected candidate, villager, distance, wand, and original offer before applying a change.
+ The trade selection screen no longer pauses single-player worlds, preventing the emerald wand's nearly finished cooldown overlay from flickering.
+ The trade selection screen now closes if its villager dies, unloads, or moves more than 8 blocks away.
+ The custom trade selection lists have been replaced with a two-column interface using the vanilla villager trading visuals and controls, with current trades on the left and available replacements on the right.
+ Fixed the trade selection panel and title text being blurred by a second background render.
+ Opening the trade selection screen now occupies the villager through its vanilla trading state, preventing it from wandering until the session ends.
+ Vanilla trading and trade selection now lock the villager for other players. Conflicting trade selection, upgrade, profession reset, and restock actions are rejected with the villager's normal head shake and refusal sound, without an extra failure message.
