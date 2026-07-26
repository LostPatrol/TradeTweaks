# Trade Tweaks

A Minecraft Forge mod that provides versatile utility staffs, useful items, and in-game options to enhance the player experience when trading with villagers.

> Currently in early preview - many features are still under development.

## Key Features

### Emerald Wand

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/craft1.png?raw=true)

**Usage:**

Method 1:

*   Sneak + Mouse Wheel: Cycle through modes
*   Sneak + Right Click: Activate current mode

Method 2:

*   Press the keybind (`Alt` by default) to open the quick mode selection wheel

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_wheel_en.png?raw=true)

| Mode              | Target                | Effect                               |
| ----------------- | --------------------- | ------------------------------------ |
| Profession Clear  | Any employed villager | Resets villager to unemployed        |
| Workblock Tracker | Any villager          | Highlights linked or potential workblock |
| Villager Tracker  | Workstation blocks    | Highlights villager using it         |
| AI Refresh        | Unemployed villagers  | Forces job search behavior           |
| Upgrade           | Any employed villager | Increase villager's profession level |
| Trade Select      | Any employed villager | Freely select available trades       |

**1\. Profession Clear**


![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_reset_1.png?raw=true)
![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_reset_2_en.png?raw=true)

*   Removes profession from any villager
*   Works even on leveled/traded villagers
*   Resets their trade progression completely

**2\. Workblock(JobSite) Tracker**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_track_block_en.png?raw=true)

*   Visually highlights the villager's linked workstation in red
*   When used on an unemployed villager, highlights the villager's current potential workstation in green (not shown in the image)
*   Effective range: 64 blocks

**3\. Villager Tracker**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_track_villager_en.png?raw=true)

*   Applies glowing effect to villager using the targeted workstation

**4\. Villager AI Refresh**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_refresh_1.png?raw=true)
![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_refresh_2_en.png?raw=true)

*   Forces unemployed villagers to stop current activities
*   Makes them (try to) immediately search for nearest valid workstation





**5\. Villager Instant Upgrade**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_upgrade_en.png?raw=true)

+ Increases villager's profession level immediately
+ Consumes `1 Emerald Block` or `9 Emerald` by default. Block will be consumed first.
+ Items and counts costs can be modified in `serverconfig/tradetweaks-server.toml` freely.  it's convenient for some modpacks, to modify it to other mods' or custom items.



**6\. Trade Selection**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_select_en.png?raw=true)


> This sounds rule-breaking, but I believe that repeatedly refreshing villager trades to get desired offers is a complete waste of the player's time – requiring no real cost except hours of grinding. 
>
> Therefore, I don't consider this feature to break Minecraft's balance or gameplay integrity. **As players, we should value our limited playtime and spend it on actually fun experiences.**



+ Allows players to manually select available trades from villager's potential offer pool
+ Any trade with the same profession and profession level required, will be considered as an available trade.
+ Randomized category trades from mods are expanded into distinct item variants, so tag-like choices such as different logs or signs are all shown instead of one random example.
+ Allow duplicated trades
+ Compatible with multiplayers
+ A Nether Star is required to upgrade your wand to use this mode.
+ Specially for librarian, if you select a trade of enchanted book, you will be allowed to choose all the max-level enchantments, with a higher chance of lower trade prices. This feature can be disabled in the server config.








## Consumable Items

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

## Trade Broadcast System

Displays nearby villagers' trade lists (with item icons) in chat, with click-to-highlight functionality. Won't repeat broadcasts for unchanged trades.

**off by default**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/tradecast_1_en.png?raw=true)

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/tradecast_2_en.png?raw=true)

#### Commands

```
/tradecast mode [option]
```

(Client-side) Filters which villagers get broadcast:



| Option    |Effect                    |
| --------- |------------------------- |
| <code>all</code> |All villagers             |
| <code>librarian</code> |Only librarians           |
| <code>off</code> |Disables broadcast system |




```
/tradecast radius [blocks]
```


(Server-side) Sets broadcast range (1-32 blocks). Requires permissions.


```
/tradecast refresh
```

(Client-side) Clears cached villager/trade data.

```
/tradecast render [bool]
```

(Client-side) Toggles item icon rendering (if true, text-only mode will be available for compatibility).



```
/tradecast time [seconds]
```

(Server-side) Adjusts trade check interval. Requires permissions.

```
/tradetweaks enchant_books_select [bool]
```

(Server-side) Determines whether the wand's Trade Selection feature can be used to select librarians' enchanted books. Requires permissions.

### …

## Compatibility

*   Compatible with all mod-added villager professions
*   Compatible with all mod-added enchantments (as long as it is 'tradeable')
*   Compatible with Easy Villager, but you can't interact with villagers inside its functional blocks using Emerald Wand
*   Compatible with the Ancient Tome added by Quark
*   Potential rendering conflicts when used with Showcase Item/Quark's inventory icon features (disable client rendering if overlap occurs)

Take Selection mode for example:

Quark's Ancient Tome:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_ancient_tome.png?raw=true)

Apotheosis:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_apotheosis.png?raw=true)

PneumaticCraft:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_pneumaticcraft.png?raw=true)

Applied Energistics 2:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_ae2.png?raw=true)




## Credits and Licensing

This mod incorporates resources from the following third-party projects, and we gratefully acknowledge their contributions:

### Code Attribution

The item rendering functionality in this mod is derived from **\[ShowcaseItem\]**, which is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) license.

*   Original source: [starforcraft/Showcase-Item: Allows you to showcase your item in the chat](https://github.com/starforcraft/Showcase-Item)
*   Modifications: Enhanced the ChatComponentMixin and ItemRenderer implementations.

### Assets Attribution

The texture `textures/emerald_wand.png` is a modified version of an asset from **\[ConstructionWand\]**, licensed under the MIT License.

*   Original asset: [Theta-Dev/ConstructionWand: Minecraft Mod - Construction Wands make building easier!](https://github.com/Theta-Dev/ConstructionWand)
*   Changes: Recolored.
