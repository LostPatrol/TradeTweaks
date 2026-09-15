<!-- Task scope and acceptance criteria for automated platform publishing. -->
# CurseForge / Modrinth 自动发布

- 添加本项目发布 skill 和 GitHub Release published 工作流。
- 以 Release 正式 JAR 附件为准，支持单独或同时发布 1.20.1 Forge、1.21.1 NeoForge。
- 提供 Secret / 项目 ID 配置入口，Token 由用户随后配置。
- 验证分流、错误附件、预发布与手动补发；不发布测试版本到平台。
- 同步发布设施到两个现有分支，保留原有本地修改。
