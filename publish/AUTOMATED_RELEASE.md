<!-- Configuration and operating guide for GitHub-triggered platform distribution. -->
# CurseForge / Modrinth 自动发布

## 一次性配置

在 [GitHub Actions Secrets](https://github.com/LostPatrol/TradeTweaks/settings/secrets/actions) 点击 **New repository secret**，添加：

| Secret 名称 | 内容 | 获取入口 |
| --- | --- | --- |
| `CURSEFORGE_TOKEN` | 有该项目上传权限的作者 API Token | [CurseForge API Tokens](https://authors-old.curseforge.com/account/api-tokens)（[官方说明](https://support.curseforge.com/support/solutions/articles/9000197321)） |
| `MODRINTH_TOKEN` | 有该项目创建版本权限的 Personal Access Token | [Modrinth PAT](https://modrinth.com/settings/pats) |

以下项目 ID 已核实并配置在 [GitHub Actions Variables](https://github.com/LostPatrol/TradeTweaks/settings/variables/actions)，无需再次填写；迁移仓库时按此恢复：

| Variable 名称 | 内容 |
| --- | --- |
| `CURSEFORGE_PROJECT_ID` | `1316814`（[Trade Tweaks](https://www.curseforge.com/minecraft/mc-mods/trade-tweaks)） |
| `MODRINTH_PROJECT_ID` | `P5pFKnk6`（[Trade Tweaks](https://modrinth.com/mod/trade-tweaks)） |

两个 Minecraft 分支共用同一个平台项目。Token 只保存在 Secrets，不写入仓库或 Release 正文。GitHub 下载附件使用自动提供的只读 `GITHUB_TOKEN`，无需另配下载 Token。

## 一句话发布（推荐）

直接调用项目 skill，例如：

- `$tradetweaks-release 发布当前分支的 2.2.2`
- `$tradetweaks-release 同时发布 1.20.1 和 1.21.1，版本 2.2.2`

Codex 会自动完成版本与更新日志准备、所选分支构建、GitHub Draft 创建、全部 JAR 上传、正式发布，以及 CurseForge / Modrinth 同步与结果检查。无需自己创建 Draft、上传文件或点击工作流按钮。

未指定分支时沿用当前任务范围，否则使用当前 Minecraft 分支；未指定版本时沿用任务约定或 `mod_version`，不会自行猜测新版本号。若已有同版本 Release，则核验并仅补发缺失的平台；若新代码需要占用一个已发布版本，会询问新的版本号。平台 Token 已于 2026-09-15 配置并核对名称，实际有效性由正式上传验证。

本地构建不可用时，skill 可自动调用 `build.yml` 的 `workflow_dispatch`，等待并下载对应提交的构建产物。发布说明中的下列步骤均由 Codex 代为执行，保留作为维护参考。

## 发布步骤（skill 自动执行）

1. 构建并验证本次需要发布的分支，将更新日志写入 `publish/changelog-<版本号>.md`。
2. 创建 **Draft Release**，正文使用更新日志内容，上传本次所有正式 JAR。
3. 确认附件齐全后，发布 Draft。工作流监听 `release.published`，包括正式版和预发布版。
4. 在 [发布工作流](https://github.com/LostPatrol/TradeTweaks/actions/workflows/publish-platforms.yml) 查看各分支 / 平台结果。

| Release 中的附件 | 自动发布结果 |
| --- | --- |
| `tradetweaks-1.20.1-2.2.0.jar` | 两个平台各发布一个 Minecraft 1.20.1 / Forge 版本 |
| `tradetweaks-1.21.1-2.2.1.jar` | 两个平台各发布一个 Minecraft 1.21.1 / NeoForge 版本 |
| 同时包含两个分支 JAR | 两个平台各发布两个独立版本，共四个上传任务 |

分支使用各自 JAR 文件名中的版本号，例如 `1.20.1-2.2.0` 和 `1.21.1-2.2.1`，不以 Release tag 推断分支或模组版本。每个分支只允许一个正式 JAR。sources/dev/javadoc JAR 被忽略，其他无法识别的 JAR、缺少附件、空更新日志、文件内模组身份或版本不符都会在上传前报错。

工作流直接分发 Release 附件，不重新构建。下载时检查大小及 GitHub 提供的 SHA-256（如有），随后读取 JAR 内的 Forge / NeoForge 元数据校验模组 ID 和版本。两平台使用同一份附件快照和 Release 正文。GitHub prerelease 映射为平台 beta，其他映射为 release。

**先上传全部附件，再发布 Draft。** 发布后追加或替换附件不会自动触发同步，也不会修改已上传的平台版本。平台审核可能导致上传成功后仍需等待公开。

## 手动验证与补发

打开发布工作流的 **Run workflow**：

- `tag`：已有 GitHub Release 标签，例如 `2.2.1`。
- `branch`：`all`、`1.20.1` 或 `1.21.1`。
- `platform`：`all`、`curseforge` 或 `modrinth`。
- `dry_run`：默认开启，仅下载、校验和生成发布计划，不需要平台 Token；确认需要补发时关闭。

失败不会取消其他分支或平台。先查平台是否已经接收文件，再使用 **Re-run failed jobs** 或限定分支 / 平台手动补发。上传超时可能发生在平台已经接收后，本流程不保证自动去重，也不自动重试上传；不要直接重跑所有成功任务。保留的附件快照有效期为 7 天，过期后使用手动触发重新获取 Release 附件。

工作流必须已存在于 Release 标签所指的提交；历史标签不会因分支更新自动获得新工作流，使用手动触发补发。手动入口还要求默认分支具有工作流。两个分支应同步发布设施，但不合并游戏代码。

若另一个 GitHub workflow 用内置 `GITHUB_TOKEN` 发布 Release，GitHub 不会因此触发本工作流。该创建流程应使用合适的 PAT / GitHub App token，或在附件齐全后显式调用 `workflow_dispatch`。普通网页发布、外部授权 GitHub API 发布使用正常事件触发。

## Skill 与维护

发布前，skill 会参考同平台、同 Minecraft 分支已有版本的名称、版本号及更新日志格式。当前显示名称统一为 `Trade Tweaks <Minecraft版本>-<模组版本>`，例如 `Trade Tweaks 1.21.1-2.2.1`，不附加 `(forge)` 或 `(neoforge)`；加载器仍通过兼容性字段标注。历史格式仅作为结构参考，不复制旧版本事实。

已发布版本需要改名时，使用 `update-platform-name.yml`，传入平台、现有文件/版本 ID 和 Minecraft 前缀版本号。它仅修改显示名称，不重新上传附件。Modrinth Token 需具有版本编辑权限；CurseForge 使用官方 update-file API。

项目 skill 位于 `.agents/skills/tradetweaks-release/SKILL.md`，可通过 `$tradetweaks-release` 调用。

测试：`node --test .github/scripts/release-plan.test.cjs`。发布 runner 使用 Node.js 22、PowerShell 和固定提交的 `mc-publish` Action，无需修改 Gradle 构建依赖。

参考：[GitHub Release 事件](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows#release)、[GitHub 防止递归触发](https://docs.github.com/en/actions/how-tos/writing-workflows/choosing-when-your-workflow-runs/triggering-a-workflow)、[mc-publish](https://github.com/Kira-NT/mc-publish/tree/52307b03863581dec6b652b83e597aec02ebb075)、[CurseForge API](https://support.curseforge.com/support/solutions/articles/9000197321)、[Modrinth API](https://docs.modrinth.com/api/operations/createversion/)。
