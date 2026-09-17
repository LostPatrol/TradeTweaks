<!-- Audit of the English changelog correction for the existing 2.3.0 release. -->
# 2.3.0 英文更新日志修正

最终英文条目：Improved compatibility with Quark's Ancient Tomes and fixed related issues.

- 两分支 publish/changelog-2.3.0.md 已改为英文并推送。
- GitHub 2.3.0 Release body 已更新，API 回读确认一致。标签与 JAR 不变。
- Modrinth vyci7TF8 / LOzZqZoC 已修改并公开 API 回读确认一致；工作流 35259487217、35259490740 成功。
- CurseForge 8905730 / 8905729 的 update-file API 首次以 markdown 格式修改，工作流 35259493845、35259497707 返回 HTTP 500；改为 text 格式重试，35259683964、35259686596 仍失败。无法确认远端是否部分应用，不能宣称已完成 CurseForge 同步。
- 浏览器访问 CurseForge 文件页尝试超时，当前无可用登录页面来完成 UI 修改。
- 已恢复工作流使用官方支持的 markdown 格式；保留新增的可选 changelog 输入，以便后续原地维护发布说明。actionlint 验证通过。
- 没有新建平台版本，没有重新上传文件，也没有移动版本标签。

重要偏好：用户用中文描述更新内容通常是提供大意，发布日志应按项目惯例写成简洁英文，除非明确要求中文原文。