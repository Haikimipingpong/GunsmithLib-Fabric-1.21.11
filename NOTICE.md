# 第三方组件与授权说明

本仓库是 **GunsmithLib** 的非官方 **Minecraft 1.21.11 / Fabric** 移植。
所有原始代码版权归原作者，本移植仅做平台适配。

## 上游项目

| 项目 | 作者 | 授权 | 说明 |
|---|---|---|---|
| [GunsmithLib](https://github.com/ChloePrime/GunsmithLib) | ChloePrime | **GPL-3.0** | 本仓库的代码来源（`mc1.21` 分支 7.1.1） |
| [TaCZ / Timeless and Classics Zero](https://github.com/MCModderAnchor/TACZ) | TaCZ Dev Team | GPL-3.0 / CC BY-NC-ND 4.0 | GunsmithLib 的前置枪械模组 |
| [TaCZ Refabricated（非官方 Fabric 移植）](https://github.com/q14433686-arch/TaCZ_Refabricated_Unofficial) | Sh1roCu 等 | GPL-3.0 | 本移植的运行底座，1.21.11 分支 |
| [Fabric API](https://github.com/FabricMC/fabric) | FabricMC | Apache-2.0 | |
| [Forge Config API Port](https://github.com/Fuzss/forgeconfigapiport) | Fuzss | LGPL-2.1 | 提供 TaCZ 依赖的配置 API |

## 本仓库不应包含的内容

`lib/` 与 `prebuilt/` 已在 `.gitignore` 中排除：

- **`lib/*.jar`** —— 第三方模组二进制（TaCZ Refabricated、Forge Config API Port、luaj）。
  体积大、版权归各自作者，**不随本仓库分发**。构建前请自行下载放入 `lib/`，
  具体文件名见 `README.md` 的"依赖"一节。
- **`prebuilt/`** —— 编译产物，仅在发布 Release 时作为附件上传。

## 维护状态

本仓库**不会被维护**：不接受功能请求，不保证跟进上游更新，Issue/PR 可能不会被处理。
代码按"现状"提供，不附带任何担保。需要长期维护的版本请基于
[上游项目](https://github.com/ChloePrime/GunsmithLib) 自行继续。

## 免责声明

- 本移植**非官方**，未被 ChloePrime（GunsmithLib 作者）或 TaCZ Dev Team 审核或背书。
- 使用非官方 TaCZ Fabric 移植版作为底座，其稳定性与兼容性由其作者负责。
- 按 GPL-3.0 分发：你可以自由使用、修改、再分发，但衍生作品须同样以 GPL-3.0 开源，
  并保留原作者署名。
