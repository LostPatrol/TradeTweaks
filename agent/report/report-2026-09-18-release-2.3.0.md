<!-- Release audit for both user-authorized Trade Tweaks 2.3.0 artifacts. -->
# Trade Tweaks 2.3.0 发布报告

日期：2026-09-18（北京时间）。按用户调用 tradetweaks-release 执行，两个分支版本均沿用已指定的 2.3.0。

## 发布结果

- GitHub：https://github.com/LostPatrol/TradeTweaks/releases/tag/2.3.0 。两个正式 JAR 均已发布；上传状态、大小、SHA-256、正文和标签目标已回读验证。
- 标签 2.3.0 指向 1.21.1 提交 4d429f05bf820eb6b163d2c81c8e6ac6f980fca4。
- 1.20.1 来源提交 af437a19a90104f7ae3a0b28dbb9be2ef4ad57fe；本地 Gradle 离线 build 成功（含重混淆）。
- 1.21.1 来源提交 4d429f05bf820eb6b163d2c81c8e6ac6f980fca4；本地 Gradle build 成功。
- 同步工作流 https://github.com/LostPatrol/TradeTweaks/actions/runs/35258867415 ：prepare 及四个分支/平台上传任务全部成功，无重试或手工重复上传。
- Modrinth Forge：https://modrinth.com/mod/P5pFKnk6/version/vyci7TF8 。版本 listed，正文一致，实际下载 SHA-256 与 GitHub 一致。
- Modrinth NeoForge：https://modrinth.com/mod/P5pFKnk6/version/LOzZqZoC 。版本 listed，正文一致，实际下载 SHA-256 与 GitHub 一致。
- CurseForge Forge：https://www.curseforge.com/minecraft/mc-mods/trade-tweaks/files/8905730 。上传 API 已接受并返回文件 ID。
- CurseForge NeoForge：https://www.curseforge.com/minecraft/mc-mods/trade-tweaks/files/8905729 。上传 API 已接受并返回文件 ID。
- CurseForge 两个公开文件页均返回 403；无法独立确认审核状态或公开下载可用性，不能将上传成功等同于公开可下载。

## 更新日志与受众基线

两分支 publish/changelog-2.3.0.md 与 GitHub Release body 完全一致；平台转发同一正文。唯一玩家条目严格使用用户指定文本：

- 改进、修复了与Quark古卷的兼容性

| 分支 | GitHub 前版 | Modrinth 前版 | CurseForge 前版证据 | 本次条目覆盖 |
|---|---|---|---|---|
| 1.20.1 | 2.2.0 | 2.2.0 / GTvgLvTw | 历次 API 接受 2.2.0 / 8887172；本次公开列表缓存仍显示 2.1.2 | Quark 改动；用户指定日志优先 |
| 1.21.1 | 2.2.1 | 2.2.1 / 1wk4HggX | 历次 API 接受 2.2.1 / 8887176；本次公开列表缓存仍显示 2.1.2 | Quark 改动；用户指定日志优先 |

Git 历史确认 GitHub/Modrinth 前版之后，两分支生产源码变更仅为本次 Quark 修复。CurseForge 历次公开可用状态未得到独立验证；没有将其缓存列表当作准确实时状态。按用户明确要求，不额外扩展更新日志。平台显示名沿用已确认格式 Trade Tweaks <minecraft>-<mod-version>；Modrinth 前版实时回读成功，CurseForge 使用既定格式。

## 正式文件

- tradetweaks-1.20.1-2.3.0.jar：479119 bytes；SHA-256 17c77377538cd70705a35cca0a71519a8fee3ca94863bdb4aa96d1894ddabca5。
- tradetweaks-1.21.1-2.3.0.jar：295382 bytes；SHA-256 84809d3eb77a8de08918ef7383239246684f583dd08c0c1648b81294df544535。
- 使用仓库 release-plan.cjs 与 validate-release.ps1 检查正式资产选择、内嵌模组 ID、版本和加载器元数据。
- 发布文件与上一任务已安装到两个 HMCL mods 目录的测试文件哈希完全相同，无需重新替换。
- 审计材料位于 agent/codex/release-2.3.0；不包含凭据。

## 未解决项

仅 CurseForge 公开页/审核状态不可独立确认（403）。本次未额外运行游戏验收，也不把用户发布指令记录为已完成游戏测试。