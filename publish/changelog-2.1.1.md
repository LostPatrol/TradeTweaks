# 2.1.1 CHANGELOG

# FIX

+ Trade replacement candidates are now generated from the server's fully loaded trade tables, adding generic support for modded villager professions and data-driven trades.
+ Candidate generation now uses the real villager context without consuming the villager's random sequence.
+ Trade levels are resolved from the villager's actual career unlock order. Both trade slots unlocked at the same level now share the same candidate pool.
+ Librarian replacement options still include every tradeable enchantment at its maximum level.
+ Trade replacement now uses short-lived server sessions and validates the selected candidate, villager, distance, wand, and original offer before applying a change.
