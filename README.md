# Trade Tweaks

Minecraft Forge mod

Provides versatile utility wands, useful item, and in-game options to improve the player experience when trading with villagers.

> Currently in early preview. Many features are still under development.

## Main Content

### Emerald Wand

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/craft1.png?raw=true)

**Usage:**

Method 1:

+ Sneak and use the mouse wheel to switch modes
+ Sneak and right-click to use

Method 2:

+ Press the keybind (`Alt` by default) to open the quick mode selection wheel

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_wheel_en.png?raw=true)

| Mode              | Target                                      | Effect                                               |
| ----------------- | ------------------------------------------- | ---------------------------------------------------- |
| Profession Clear  | Any villager with a profession              | Resets the villager to unemployed                    |
| Work Block Track  | Any villager                                | Highlights the villager's work block or desired work block |
| Villager Track    | A work block used by a villager profession  | Highlights the villager occupying that block         |
| Villager AI Refresh | Unemployed villagers                      | Makes the villager search for a work block again     |
| Profession Upgrade | Any villager with a profession             | Increases the villager's profession level            |
| Trade Selection   | Any villager with a profession              | Freely select the villager's available trades        |

**1. Profession Clear**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_reset_1.png?raw=true)

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_reset_2_en.png?raw=true)

+ Clears any villager's profession
+ Can clear the profession of villagers that have already traded or leveled up, resetting their trade level and trade experience to 0

**2. Work Block Track**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_track_block_en.png?raw=true)

+ Shows the villager's current work block through walls with a red highlight
+ When used on an unemployed villager, shows that villager's current desired work block with a green highlight (not shown in the image)
+ Effective detection range is within a 64-block radius

**3. Villager Track**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_track_villager_en.png?raw=true)

+ If a villager is using the targeted block as a work block, that villager receives the glowing effect

**4. Villager AI Refresh**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_refresh_1.png?raw=true)
![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_refresh_2_en.png?raw=true)

+ Makes an unemployed villager stop most current activities and try to find the nearest work block again

**5. Profession Upgrade**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_upgrade_en.png?raw=true)

+ Immediately increases the villager's profession level by 1
+ Consumes 1 emerald block first, or 9 emeralds by default
+ The consumed item and amount can be customized in `serverconfig/tradetweaks-server.toml`

**Trade Selection**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_select_en.png?raw=true)

> Author: This feature may sound too powerful, but I think repeatedly rerolling villager trades is simply a waste of time. As players, we should spend our limited time doing more interesting things.

+ Allows players to manually select any possible trade for a villager as its current trade option
+ Any trade with the same profession requirement and profession level requirement as the current trade option is considered a possible trade
+ Randomized category trades from mods are expanded into distinct item variants, so tag-like choices such as different logs or signs are all shown instead of one random example
+ The selection screen reuses the vanilla villager trading visuals and controls, with current trades on the left and available replacements on the right; it does not pause a single-player world
+ While the selection screen is open, the villager is occupied just as in vanilla trading and will not wander. Other players cannot trade with it or use TradeTweaks' select, upgrade, reset, or restock actions until the session ends; rejected attempts make the villager shake its head and play its refusal sound
+ The selection screen closes automatically if the villager dies, unloads, or moves more than 8 blocks away
+ Allows a villager to have multiple duplicate trade options, and supports multiplayer
+ Requires 1 nether star to upgrade the wand before this feature can be used
+ For librarians specifically, players can select enchanted books for the highest level of every enchantment, with a higher chance of lower trade prices. This feature can be disabled in the server config

### Consumable Items

Adds several consumable utility items.

**Book of Enlightenment**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/book_of_enlightenment.png?raw=true)

Turns a nitwit villager into an unemployed villager.

**Reversion Bottle**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/reversion_bottle.png?raw=true)

Immediately turns a villager into a zombie villager. Its shapeless recipe uses a glass bottle, two pieces of rotten flesh, and a clock.

**Restoration Bottle**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/restoration_bottle.png?raw=true)

Immediately cures a zombie villager back into a villager, equivalent to curing it manually with the vanilla method. Its shapeless recipe uses a Potion of Weakness, a golden apple, and a clock.

**Totem of Village Hero**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/totem_of_village_hero.png?raw=true)

Grants 5:00 of Hero of the Village. If the player already has Hero of the Village, increases the effect level by 1 (up to level 5) and adds 5:00 duration. After winning a raid, the player receives a number of this item based on the raid level.

**Restock Writ**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/restock_writ.png?raw=true)

Forces a villager to restock immediately. Using it multiple times on the same villager on the same day increases the cost per use.

### Trade Broadcast

Allows nearby villagers' trade lists, including item icons, to be broadcast to the player's chat, with clickable messages that highlight the matching villager. The same villager will not be broadcast again if its trades have not changed.

**Disabled by default**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/tradecast_1_en.png?raw=true)

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/tradecast_2_en.png?raw=true)

#### Commands

```
/tradecast mode [option]
```

Client command. Selects which villager type to broadcast.

| option      | Effect                      |
| ----------- | --------------------------- |
| `all`       | Broadcast all villagers     |
| `librarian` | Broadcast librarians only   |
| `off`       | **Disable trade broadcast** |

```
/tradecast radius [blocks]
```

Server command. Changes the server broadcast range (1-32 blocks). Requires permission.

```
/tradecast refresh
```

Client command. Clears recorded villager and trade list information.

```
/tradecast render [bool]
```

Client command. Enables or disables item icon rendering. When disabled, only text content is output.

+ This is for mod compatibility. If rendering errors, offsets, or similar issues occur, disable this option

```
/tradecast time [seconds]
```

Server command. Changes the server update check interval. Requires permission.

```
/tradetweaks enchant_books_select [bool]
```

Server command.

### ...

## Compatibility

+ Compatible with all villager professions and trades added by mods through standard methods
+ Compatible with all enchantments added by mods, such as Apotheosis, through standard methods, as long as the enchantment is tradeable
+ Compatible with Easy Villagers, but cannot trade directly with villagers placed inside trader blocks
+ Compatible with Quark's Ancient Tomes. When Quark is installed, librarian master-level trades treat all Ancient Tomes as replaceable trades

Known issues:

+ When Showcase Item and Quark are installed together, and each mod has item icon rendering in inventories enabled, rendering issues such as icon offsets or overlaps may occur. Disable client rendering if needed. In practice, this mod, Showcase Item, and Quark all render item icons into inventories in the same general way, so enabling any two of them at the same time may cause rendering issues
+ The trade list of the Ars Nouveau villager profession "Scribe" may not be read correctly

> Standard methods refer to common Forge mod development practices: when possible, using APIs provided directly by Forge or following vanilla Minecraft's implementation style, instead of using forceful or special approaches.

Compatibility showcases with well-known mods:

Quark's Ancient Tome:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_ancient_tome.png?raw=true)

Apotheosis:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_apotheosis.png?raw=true)

PneumaticCraft:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_pneumaticcraft.png?raw=true)

Applied Energistics 2:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_ae2.png?raw=true)

## Credits and Acknowledgements

This mod uses or partially references resources or code from the following third-party projects. Copyright belongs to their respective owners, with thanks and acknowledgement here.

### Code References

The chat item rendering code is based on the **[ShowcaseItem]** project. The original project is licensed under CC BY-NC-SA 3.0:

- Original project: [starforcraft/Showcase-Item: Allows you to showcase your item in the chat](https://github.com/starforcraft/Showcase-Item)
- Modifications: Optimized some mixins with MixinExtra and improved part of the item rendering logic

### Asset References

The texture file `textures/emerald_wand.png` is modified from an asset from **[ConstructionWand]**. The original asset is licensed under MIT:

- Original asset: [Theta-Dev/ConstructionWand: Minecraft Mod - Construction Wands make building easier!](https://github.com/Theta-Dev/ConstructionWand)
- Modifications: Hue adjustment
