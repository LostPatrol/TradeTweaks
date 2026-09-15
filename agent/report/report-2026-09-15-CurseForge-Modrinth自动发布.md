<!-- Implementation status and verification evidence for automated platform releases. -->
# CurseForge / Modrinth 自动发布执行简报

## 已完成

- 创建项目 skill：`.agents/skills/tradetweaks-release/SKILL.md`，说明发布准备、单/双分支附件规则、验证及补发。
- 新增 `release.published` 工作流，按 Release 实际附件分流：1.20.1 / Forge / Java 17，1.21.1 / NeoForge / Java 21；一次 Release 可只包含一个分支，也可包含两个分支及不同模组版本。
- 直接下载正式附件并校验大小、可用的 SHA-256、JAR 内模组 ID 与版本，两个平台使用相同附件快照与更新日志。
- 每个分支、平台独立上传；支持手动 dry run、选择分支/平台补发和失败任务重跑。上传尝试次数为 1，不宣称自动去重。
- 发布设施已同步并推送到现有 `1.20.1`、`1.21.1` 分支，未创建新分支，未合并游戏实现。
- 已通过 GitHub API 配置仓库变量：`CURSEFORGE_PROJECT_ID=1316814`、`MODRINTH_PROJECT_ID=P5pFKnk6`。核对了平台项目身份与源码仓库。
- 配置说明与 Token 入口位于 `publish/AUTOMATED_RELEASE.md`。

## 验证结果

- Node.js 发布规划和附件快照测试：14 项全部通过，覆盖单/双分支、不同版本号、预发布、过滤补发、错误/重复附件、空日志、草稿、文件大小与哈希异常等。
- actionlint 1.7.12：工作流检查通过；YAML 解析通过；`git diff --check` 通过。
- 使用项目内 `agent/codex/skill-validation-venv` 执行官方 `quick_validate.py`：Skill is valid。
- 下载真实 GitHub Release `2.2.0` 的两个 JAR，本地元数据检查通过；故意改错预期版本的测试正确拒绝文件。
- GitHub 单分支 dry run：[`2.2.1`，34987381955](https://github.com/LostPatrol/TradeTweaks/actions/runs/34987381955)，成功。
- GitHub 双分支 dry run：[`2.2.0`，34987387817](https://github.com/LostPatrol/TradeTweaks/actions/runs/34987387817)，成功。
- 两次远程运行均完成测试、实际附件下载、嵌入元数据校验及快照保存；平台上传任务按 dry run 设计跳过。

## 尚待配置与实际限制

- 用户尚需添加 `CURSEFORGE_TOKEN`、`MODRINTH_TOKEN` 两个 repository secrets。没有执行真实平台上传，因此尚未验证 Token 权限、平台接收或审核后的公开可下载状态。
- 本次没有修改游戏代码、Gradle 构建或发布新模组版本，因此没有新增玩家更新日志，也没有重新执行游戏构建。
- 发布 Draft 前必须附齐本次所有 JAR；发布后追加附件不自动同步。历史标签需手动触发；新标签提交须包含该工作流。
- 使用其他 workflow 内置 `GITHUB_TOKEN` 创建 Release 不会自动触发后续 release 工作流；skill 已说明 PAT / GitHub App 或显式 dispatch 的接入方式。
- 网络超时不代表平台没有接收上传；补发前需核对平台，避免重跑成功上传。平台审核可能延迟公开。
- 保留任务开始前已有的 MEMORY.md 环境修复修改及相关未跟踪文件；过程工具、验证环境和现有分支的临时 worktree 位于 `agent/codex`。
