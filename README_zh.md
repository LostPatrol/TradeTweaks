# Trade Tweaks

Minecraft Forge mod

提供功能多样实用的工具手杖、功能道具以及游戏内选项，提升玩家与村民交易时的游戏体验

> 目前为早期预览版本，诸多功能有待实现

## 主要内容

### 绿宝石手杖

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/craft1.png?raw=true)

**使用：**

方法1：

+ 潜行状态下使用鼠标滚轮切换不同模式
+ 潜行状态下右键使用

方法2：

+ 按键（默认为`Alt`）启用模式快捷选择轮盘

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_wheel.png?raw=true)

| 模式         | 应用对象                       | 效果                     |
| ------------ | ------------------------------ | ------------------------ |
| 职业清除     | 任意职业的村民                 | 使村民重置为无业         |
| 工作方块追踪 | 任意村民                       | 高亮该村民的工作方块或期望工作方块 |
| 村民追踪     | 任意职业的村民所对应的工作方块 | 高亮占有该方块的村民     |
| 村民行为刷新 | 无业村民                       | 令村民重新寻找工作方块   |
| 职业升级     | 任意职业的村民                 | 提升村民的职业等级       |
| 交易选择     | 任意职业的村民                 | 自由选择该村民的可用交易 |

**1.职业清除**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_reset_1.png?raw=true)

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_reset_2_en.png?raw=true)




+ 清除任意村民的职业
+ 可以清除已经交易过甚至已经升级过的村民的职业，并重置他们的交易等级与交易经验为0



**2.工作方块追踪**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_track_block_en.png?raw=true)




+ 红色高亮透视显示村民当前的工作方块
+ 对无业村民使用时，将以绿色高亮显示该村民当前期望的工作方块（图片中未展示）
+ 有效检测范围为半径64格以内



**3.村民追踪**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_track_villager_en.png?raw=true)



+ 若有村民正在使用该方块作为工作方块，则给予该村民发光效果



**4.村民行为刷新**



![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_refresh_1.png?raw=true)
![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_refresh_2_en.png?raw=true)

+ 使无业村民停止当前的大部分活动，尝试重新寻找最近的工作方块



**5.职业升级**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_upgrade_en.png?raw=true)



+ 立刻将村民的职业等级提高1级
+ 默认消耗1个绿宝石块（优先）或9个绿宝石
+ 消耗的物品和数量可以在`serverconfig/tradetweaks-server.toml`自定义



**交易选择**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/mode_select_en.png?raw=true)

> 作者：这个功能可能听上去过于强大，但我认为跟村民反复刷交易纯粹是浪费时间。作为玩家我们应该用有限的时间去做更有趣的事情。



+ 允许玩家手动选择村民的任一可能出现的交易作为当前交易选项
+ 任何与当前交易选项有着相同职业要求和职业等级要求的交易，都将被视为“可能出现的交易”
+ 对于模组中按类别随机生成的交易，会展开其中不同的物品变体；例如各种原木、告示牌等都会显示，而不是只随机显示一种
+ 自选交易界面复用原版村民交易界面的视觉与操作方式，左栏显示村民现有交易，右栏显示可替换交易；界面不会暂停单人游戏世界
+ 自选交易界面打开期间，村民会像原版交易时一样被占用且不会乱跑。其他玩家在会话结束前无法与其交易，也无法执行自选、升级、清除职业或使用补货令；被拒绝时村民会摇头并发出拒绝声
+ 村民死亡、卸载或离玩家超过8格时，自选交易界面会自动关闭
+ 允许一个村民有多个重复的交易选项，并且兼容多人游戏
+ 需要1个下界之星对手杖进行升级以使用此功能
+ 特别地，对于图书管理员，可以选择所有魔咒的最高等级所对应的附魔书，且有更高几率出现较低的交易价格（此特性可由服务器配置关闭）



### 一次性道具

添加了一些消耗性的功能道具。



**启智之书**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/book_of_enlightenment.png?raw=true)

将傻子村民(Nitwit)变为无业村民；



**转生之瓶**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/reversion_bottle.png?raw=true)

将村民立刻转为僵尸村民；无序合成配方需要1个玻璃瓶、2个腐肉和1个时钟；



**复生之瓶**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/restoration_bottle.png?raw=true)

将僵尸村民立刻救回村民，效果等同于使用原版方法手动医治；无序合成配方需要1瓶虚弱药水、1个金苹果和1个时钟；



**村庄英雄图腾**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/totem_of_village_hero.png?raw=true)

获得5:00村庄英雄效果；已有村庄英雄buff时，将buff等级+1(最高5级)，时间+5:00；劫掠事件胜利后，将视劫掠等级奖励玩家一定数量的该物品；



**补货令**

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/restock_writ.png?raw=true)

强制村民立刻补货，同一天内对同一村民多次使用将会增加单次消耗个数。



### 交易播报

允许将附近村民的交易列表（以及物品图标）播报到玩家的聊天栏，并可以点击高亮对应村民。同一村民如果交易未发生变化则不会重复播报。

**默认关闭**



![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/tradecast_1_en.png?raw=true)

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/tradecast_2_en.png?raw=true)



#### 命令



```
/tradecast mode [option]
```

客户端命令，选择播报的村民类型

| option      | 效果                 |
| ----------- | -------------------- |
| `all`       | 播报所有村民         |
| `librarian` | 仅播报图书管理员     |
| `off`       | **关闭交易播报系统** |



```
/tradecast radius [blocks]
```

服务端命令，修改服务器播报范围（1-32格），需要权限



```
/tradecast refresh
```

客户端命令，清空已记录的村民及其交易列表信息



```
/tradecast render [bool]
```

客户端命令，开启或关闭物品图标渲染；关闭后，将仅输出文字内容

+ 这是出于mod兼容性的考虑，如果出现渲染错误、偏移等问题，请关闭



```
/tradecast time [seconds]
```

服务端命令 修改服务器更新检测间隔，需要权限



```
/tradetweaks enchant_books_select [bool]
```

服务端命令 决定手杖的自选交易功能是否可以用于选择图书管理员的附魔书。

### 进度

Trade Tweaks新增了独立的进度页，共包含17项进度，覆盖全部手杖模式、交易自选、一次性道具和交易播报。

| 进度 | 类型 | 达成条件 |
| --- | --- | --- |
| 不是建筑手杖 | 根节点 | 制作或获得一根任意形态的绿宝石手杖 |
| 定位 | 普通 | 从一名村民定位工作站 |
| 定位2.0 | 普通 | 从工作站定位一名村民 |
| 解雇 | 普通 | 成功清除一名村民的职业 |
| 放空大脑 | 普通 | 成功刷新一名无业村民的行为 |
| boss直聘 | 普通 | 提升一名村民的职业等级 |
| 市场支配 | 目标 | 获得一根使用下界之星升级后的绿宝石手杖 |
| 有形的大手 | 挑战 | 替换一次村民交易 |
| 魔咒图书馆青春版 | 目标 | 为图书管理员选择一种附魔书交易 |
| 调教高手 | 挑战 | 六种手杖模式均至少成功使用一次 |
| 知识学爆 | 挑战 | 使用启智之书教化一名傻子村民 |
| 庸医 | 目标 | 使用转生之瓶感染一名村民 |
| 神医 | 目标 | 使用复生之瓶拯救一名僵尸村民 |
| 拿来！ | 目标 | 成功使用一次补货令 |
| 村庄英雄之证 | 目标 | 使用一次村庄英雄图腾 |
| 小道消息 | 普通 | 首次收到一份非空的交易播报 |
| 我闻到了经验修补 | 目标 | 交易播报发现并高亮一名出售经验修补的村民 |



## 兼容性

+ 兼容所有由mod通过规范的方法添加的村民职业及其交易；
+ 兼容所有由mod（例如神化 Apotheosis）通过规范的方法添加的魔咒（只要它是“可交易”的魔咒）；
+ 兼容简易村民（EasyVillager），但不能直接与放置在交易所内的村民交易；
+ 兼容由夸克（Quark）提供的古卷：当有Quark安装时，图书管理员的大师级交易将视所有古卷为可替换交易。



已知问题：

+ 与Showcase Item，Quark一起安装，且各mod均开启了物品图标渲染到物品栏功能时，可能会有出现渲染问题（图标偏移或重合），可关闭客户端渲染；实际上，本mod，Showcase Item，Quark的渲染物品图标到物品栏的实现方法是同一种，因此任意两者同时启用都有可能导致渲染问题；
+ 新生魔艺（Ars Nouveau）添加的村民职业“暗影导师”的交易列表可能无法正常读取。



> 规范的方法是指Forge模组开发的一般做法：条件允许时，使用Forge直接提供的API，或遵循与原版mc相同的写法，而非采取其它暴力或特殊手段。



与知名模组的兼容性展示：



古卷 Quark's Ancient Tome:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_ancient_tome.png?raw=true)

神化 Apotheosis:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_apotheosis.png?raw=true)

气动工艺 PneumaticCraft:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_pneumaticcraft.png?raw=true)

应用能源2 Applied Energistics 2:

![alt text](https://github.com/LostPatrol/TradeTweaks/blob/1.20.1/publish/assets/compatible_ae2.png?raw=true)







## 声明与致谢

本模组使用或部分引用了以下第三方项目的资源或代码，在此声明版权归属并致以感谢：

### 代码引用

聊天栏渲染物品的代码基于 **[ShowcaseItem]** 项目实现，原项目采用 CC BY-NC-SA 3.0 协议授权：

- 原始项目：[starforcraft/Showcase-Item: Allows you to showcase your item in the chat](https://github.com/starforcraft/Showcase-Item)
- 修改内容：使用MixinExtra优化了部分Mixin, 改进部分物品渲染逻辑

### 素材引用

纹理文件 `textures/emerald_wand.png` 修改自 **[ConstructionWand]** 的素材，原素材采用 MIT 授权：

- 原始素材：[Theta-Dev/ConstructionWand: Minecraft Mod - Construction Wands make building easier!](https://github.com/Theta-Dev/ConstructionWand)
- 修改内容：色相调整
