# Trade Tweaks

A Minecraft Forge mod that provides versatile utility staffs and in-game options to enhance the player experience when trading with villagers.

> Currently in early preview - many features are still under development.

## Key Features

### Emerald Wand

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/craft1.png?raw=true)

**Usage:**

*   Sneak + Mouse Wheel: Cycle through modes
*   Sneak + Right Click: Activate current mode

| Mode              | Target                | Effect                               |
| ----------------- | --------------------- | ------------------------------------ |
| Profession Clear  | Any employed villager | Resets villager to unemployed        |
| Workblock Tracker | Any employed villager | Highlights linked workblock          |
| Villager Tracker  | Workstation blocks    | Highlights villager using it         |
| AI Refresh        | Unemployed villagers  | Forces job search behavior           |
| Upgrade           | Any employed villager | Increase villager's profession level |
| Trade Select      | Any employed villager | Freely select available trades       |

**1\. Profession Clear**


![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_reset_1.png?raw=true)
![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_reset_2_en.png?raw=true)

*   Removes profession from any villager
*   Works even on leveled/traded villagers
*   Resets their trade progression completely

**2\. Workblock(JobSite) Tracker**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_track_block_en.png?raw=true)

*   Visually highlights the villager's linked workstation
*   Effective range: 64 blocks

**3\. Villager Tracker**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_track_villager_en.png?raw=true)

*   Applies glowing effect to villager using the targeted workstation

**4\. Villager AI Refresh**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_refresh_1.png?raw=true)
![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_refresh_2_en.png?raw=true)

*   Forces unemployed villagers to stop current activities
*   Makes them (try to) immediately search for nearest valid workstation





**5\. Villager Instant Upgrade**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_upgrade_en.png?raw=true)

+ Increases villager's profession level immediately
+ Consumes `1 Emerald Block` or `9 Emerald` by default. Block will be consumed first.
+ Items and counts costs can be modified in `serverconfig/tradetweaks-server.toml` freely.  it's convenient for some modpacks, to modify it to other mods' or custom items.



**6\. Trade Selection**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/mode_select_en.png?raw=true)


> This sounds rule-breaking, but I believe that repeatedly refreshing villager trades to get desired offers is a complete waste of the player's time – requiring no real cost except hours of grinding. 
>
> Therefore, I don't consider this feature to break Minecraft's balance or gameplay integrity. **As players, we should value our limited playtime and spend it on actually fun experiences.**



+ Allows players to manually select available trades from villager's potential offer pool
+ Any trade with the same profession and profession level required, will be considered as an available trade.
+ Allow duplicated trades
+ Compatible with multiplayers
+ A Nether Star is required to upgrade your wand to use this mode. (not completed)
+ Honestly, you can use this to force the villager to restock... but it doesn't matter and i don't want to fix.
+ Specially for librarian, if you select a trade of enchanted book, you will be allowed to choose the enchantment and its level.(not completed)








## Trade Broadcast System

Displays nearby villagers' trade lists (with item icons) in chat, with click-to-highlight functionality. Won't repeat broadcasts for unchanged trades.

**off by default**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/tradecast_1_en.png?raw=true)

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/master/publish/assets/tradecast_2_en.png?raw=true)

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

### …

## Compatibility

*   Should work with all mod-added villager professions
*   Potential rendering conflicts when used with Showcase Item/Quark's inventory icon features (disable client rendering if overlap occurs)


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