<!-- Actual platform release verification; distinguishes upload acceptance from public availability. -->
# 2.2.0 / 2.2.1 平台发布 skill 实测

## 范围与结果

2026-09-15 按用户要求由子智能体实际调用 tradetweaks-release skill，复用已发布 GitHub Release 的准确附件。只发布 1.20.1-2.2.0（Forge）和 1.21.1-2.2.1（NeoForge），明确跳过 1.21.1-2.2.0。

| 版本 | GitHub workflow | CurseForge | Modrinth |
| --- | --- | --- | --- |
| 1.20.1-2.2.0 | [34990258384](https://github.com/LostPatrol/TradeTweaks/actions/runs/34990258384) | [8887172](https://www.curseforge.com/minecraft/mc-mods/trade-tweaks/files/8887172)，上传成功 | [GTvgLvTw](https://modrinth.com/mod/trade-tweaks/version/GTvgLvTw)，listed |
| 1.21.1-2.2.1 | [34990262760](https://github.com/LostPatrol/TradeTweaks/actions/runs/34990262760) | [8887176](https://www.curseforge.com/minecraft/mc-mods/trade-tweaks/files/8887176)，上传成功 | [1wk4HggX](https://modrinth.com/mod/trade-tweaks/version/1wk4HggX)，listed |

两次 workflow 的 prepare 与四个实际上传任务全部成功。Token 的真实上传权限已验证。Modrinth 公开 API 验证了版本、游戏版本、加载器、文件名、大小与 listed 状态；重新下载两份 Modrinth 文件后 SHA-256 与 GitHub 附件一致。公开 API 未出现被排除的 1.21.1-2.2.0。

CurseForge 上传日志返回成功及上述文件 URL/ID，但公开文件页面不可访问（Cloudflare/抓取失败），因此其审核状态与公开下载状态未确认，不能等同于已审核，也不假定仍在审核。

## 执行与验证

- 发布前检查：Modrinth 无两个目标版本；GitHub 历史仅两个 dry run，无实际上传；CurseForge 公开页面检查受限，已如实保留该限制。
- 使用 Git credential helper 在内存获取既有 GitHub 凭据，通过 REST API 调用 publish-platforms.yml，未使用 gh、模拟键鼠或输出凭据。
- workflow ref 为 1.21.1，源提交 c24a177bc9f59d6eddf521294a2d59030aa1c785；输入分别为 tag=2.2.0 / branch=1.20.1 和 tag=2.2.1 / branch=1.21.1，platform=all，dry_run=false。
- 工作流自动执行 release selection 测试、附件快照校验、JAR 内嵌元数据校验，然后分别上传两个平台。无失败、无重试、无重复派发。
- GitHub Release 2.2.0 标签提交：61763e363a3285b7a0c82452689338f9b3f5e841；2.2.1 标签提交：7efd560f9801172974c5608132e06a551b98eb49。未移动标签或修改附件。
- tradetweaks-1.20.1-2.2.0.jar：479659 字节，SHA-256 d94f74ffd644220914ac24758a89c9a10497ce0b6e0c077d5425a40682f5d679。
- tradetweaks-1.21.1-2.2.1.jar：296861 字节，SHA-256 3bb35412c7f0f80e24a0b36fb40c6b97373346b70c948e98900ffef1a04e2061。

## Skill 测试结论与限制

本次实际验证了 skill 的既有 Release 补发路径、不同分支使用不同版本的精确选择、平台 Secrets 上传权限与结果核查。无需用户执行其他操作；没有发现需要修复的 skill/workflow 缺陷。

由于两个 GitHub Releases 已存在，本次没有新建 Release 或重新构建，因此不将新建 GitHub Release 的完整链路声称为本次已实测。CurseForge 公开状态仍未确认。

已维护两分支 MEMORY.md 的真实发布基线。原有 Gradle 修复相关未提交改动保留，不纳入本次提交。过程日志及校验下载位于 agent/codex/platform-release-20260915。