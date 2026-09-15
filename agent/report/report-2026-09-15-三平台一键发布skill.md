<!-- Implementation and validation report for the complete release skill workflow. -->
# 三平台一键发布 skill 执行简报

## 完成内容

- 将 `tradetweaks-release` 明确为完整发布入口：准备版本/更新日志、构建指定分支、提交推送、创建 GitHub Draft、上传全部正式 JAR、发布 Release、等待并核验 CurseForge / Modrinth 同步。
- skill 调用即覆盖本次发布链路授权，不再将创建 Draft、上传附件、发布或工作流按钮交给用户操作。编辑/解释 skill 或 dry run 不会被视为真实发布请求。
- 补充分支/版本缺省规则、已有 Release 恢复路径、构建 SHA 与产物核对、超时接收核验、定向重试和最终结果报告要求。
- 添加 GitHub REST API 操作参考，明确现有 GitHub 凭据复用、原始二进制上传、附件完整校验后才发布，以及同步状态追踪。
- 为现有 Build 工作流添加 `workflow_dispatch`，供 skill 在需要时自动触发远程构建；保留原有构建逻辑与 push / pull_request 触发。
- 更新发布指南的一句话调用示例和项目记忆，发布设施同步到两个现有 Minecraft 分支。

## 验证

- GitHub Secrets 元数据查询确认 `CURSEFORGE_TOKEN` 和 `MODRINTH_TOKEN` 均存在；未读取或输出 Token 值。
- skill YAML frontmatter 初次校验发现描述中的冒号需要引号，已修复并重新校验通过。
- 项目内 Python 验证环境执行官方 `quick_validate.py` 通过。
- actionlint 检查 Build 与平台发布工作流通过；`git diff --check` 通过。
- 检查了单分支调用、双分支调用、已发布版本补发与仅修改 skill 四类流程的指令边界；这些是指令检查，不是正式平台发布测试。

## 限制与保留项

- 本次没有请求发布具体新版本，因此未创建 GitHub Release 或上传到平台；完整真实发布链路与 Token 实际权限仍待下一次发布验证。
- 本次仅为 Build 添加手动 API 触发入口，未启动新的远程构建；原有附件下载和单/双分支 dry run 验证记录见上一份自动发布报告。
- 未更改游戏代码、版本号或已有 Release/tag，未生成新版本更新日志。已有本地环境修复相关修改保持原状。
- 用户未给新版本且当前版本已发布并含新生产代码时，skill 会询问版本号；平台权限失效或接收状态无法确定时如实报告，不伪造完成。
