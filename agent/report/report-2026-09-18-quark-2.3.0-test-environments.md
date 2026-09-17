<!-- Execution results and manual acceptance checklist for both Quark compatibility builds. -->
# Quark 古卷适配最小修复与 2.3.0 测试环境

日期：2026-09-18。状态：两个分支实现、构建、测试环境安装完成；用户游戏内验收待进行。未创建或发布 GitHub Release，也未上传 CurseForge/Modrinth。

## 实现范围

- 1.20.1：60749f0；1.21.1：c2610f9；均已推送同名远程分支。
- 两分支版本分别为 1.20.1-2.3.0、1.21.1-2.3.0，各生成 publish/changelog-2.3.0.md。
- 各修改 3 个 Java 文件、gradle.properties，新增 1 个更新日志。
- QuarkCompat 仅通过注册名与输入/输出形态识别古卷兑换；不再反射、不直接链接 Quark 类。
- ListingVariantGenerator 保留上游生成的 MerchantOffer；仅古卷交易将支付材料和结果的 NBT（1.20.1）或组件补丁/数量（1.21.1）纳入去重。
- 古卷依据观察到的随机索引范围枚举，最多 16384 次确定性探测，另有 1 次初始探测；超范围记录候选不完整警告。其他交易继续按物品类型去重。
- TradeSelectionSessionManager 删除手工补入古卷的逻辑，沿用实际交易表及原有附魔书选择开关。

## 验证

- 1.20.1 Gradle build 成功，包含 reobfJar；最终复核 build --offline 成功。
- 1.21.1 Gradle build 成功，最终在线复核成功。
- 两分支分别通过 18 项断言：真实附魔 NBT/组件区分、书与古卷存储格式、空交易、单个/20 个/超过物品注册表大小的变体、确定性探测次数、原交易对象和参数保留、普通交易去重不变、重复古卷合并、17000 项输入的上限退出。
- 回归程序使用真实 Minecraft/Forge/NeoForge 类与合成 ItemListing；不是完整 Quark 联机或游戏内验收。Gradle 原有 test 任务为 NO-SOURCE，本次回归由 agent/codex/quark-2.3.0 下的独立 Java 程序完成。
- 使用 javap 检查实际 Quark-4.0-462.jar 和 Quark-4.1-485.jar：均为单次 nextInt(validEnchants.size()) 选附魔，并检查模块启用状态和空列表。
- 已安装生产 JAR 内版本正确，源文件与安装副本 SHA-256 一致；Quark/Zeta 下载通过 Modrinth 发布 SHA-512 校验。
- 原有其他模组逐个 SHA-256 校验通过，文件名与启用/禁用状态完全保留。
- 已解决的环境问题：1.20.1 在线依赖解析停滞，改用已存在缓存的离线构建成功；1.21.1 最终离线复核因缺少资源索引失败，恢复在线构建后成功。未降低编译或重混淆标准。

## 已安装测试环境

| 实例 | TradeTweaks | Quark | Zeta | 加载器 |
|---|---|---|---|---|
| D:\0game\HMCL\.minecraft\versions\1.20.1-Forge | 1.20.1-2.3.0 | 4.0-462 | 1.0-31 | Forge 47.4.23 |
| D:\0game\HMCL\.minecraft\versions\1.21.1-NeoForge | 1.21.1-2.3.0 | 4.1-485 | 1.1-40 | NeoForge 21.1.250 |

各 mods 目录启用上述三个 JAR。Quark 1.21.1 必需的 Biolith 3.0.10 已内置于其 jarjar；现有 NeoForge 满足 Quark 最低 21.1.230 要求。原旧 TradeTweaks 的 .jar.disabled 已移出 mods 并备份；没有启用其他原先禁用的模组。未修改世界或原有配置。两目录此前没有 Quark 配置，首次启动生成默认配置。

详细安装清单、哈希与备份位置见 agent/codex/quark-2.3.0/installation.json。

## 用户验收建议（两个实例分别执行）

1. 启动对应 HMCL 实例，在模组列表确认本模组 2.3.0、Quark、Zeta 正常加载。
2. 在测试世界使用大师级图书管理员，通过绿宝石魔杖的交易选择模式打开大师级候选；如曾关闭选择附魔书功能，使用 /tradetweaks enchant_books_select true。
3. 搜索古卷，确认能选择多个不同附魔、相同附魔不重复；支付正确古卷和满级附魔书完成实际兑换，检查结果附魔、交易次数、补货和存档重进。
4. 在 Quark 配置关闭古卷兑换并重启后，用新村民/新选择会话确认不再额外出现古卷候选；恢复开关再检查。古卷模块关闭、有效附魔列表为空/缩减也需验证。
5. 1.21.1 数据重载后重新打开选择界面，检查候选与新的有效附魔一致；原有会话快照不作实时更新承诺。
6. 回归普通职业及普通附魔书选择。可暂时禁用 Quark 后验证本模组独立启动（不要在有 Quark 内容的重要世界做此项）。

## 未完成验收与风险

- 未启动完整游戏、未验证真实支付/补货/存档或多人网络；上述均待用户实测，不能将构建与合成交易断言当作完整兼容验收。
- 第三方包装生成器或 Quark 将来使用多个相关随机选择时，当前枚举不保证穷尽；本次保证范围基于已检查的两个 Quark 发布版。
- 大于 16384 的选择范围会截断并明确告警；不将截断候选宣称为完整集合。