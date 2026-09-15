<!-- Scope for extending the existing release skill into a complete publishing entry point. -->
# 一次调用完成三平台发布

- 将 GitHub Release 创建、附件上传、正式发布纳入 skill 的自动执行职责。
- 自动等待并核验 CurseForge / Modrinth 同步，保留单/双分支和定向恢复能力。
- 核对用户已配置的两个 Token 名称；不读取或输出其值。
- 补齐可由 agent 调用的远程构建入口，维护说明、记忆和报告并同步现有分支。
- 本次仅调整发布设施，不发布真实新版本。
