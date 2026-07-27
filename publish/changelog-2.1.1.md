# 2.1.1 CHANGELOG

# FIX

+ Trade replacement candidates are now generated from the server's fully loaded trade tables, adding generic support for modded villager professions and data-driven trades.
+ Randomized category-based trade factories now expose every distinct item variant they can generate instead of showing only one random example, covering modded groups such as logs and signs without profession-specific rules.
+ Candidate generation now uses the real villager context without consuming the villager's random sequence.
+ Trade levels are resolved from the villager's actual career unlock order. Both trade slots unlocked at the same level now share the same candidate pool.
+ Librarian replacement options still include every tradeable enchantment at its maximum level.
+ Trade replacement now uses short-lived server sessions and validates the selected candidate, villager, distance, wand, and original offer before applying a change.
+ The trade selection screen no longer pauses single-player worlds, preventing the emerald wand's nearly finished cooldown overlay from flickering.
+ The trade selection screen now closes if its villager dies, unloads, or moves more than 8 blocks away.
