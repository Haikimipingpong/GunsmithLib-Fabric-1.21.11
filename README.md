# GunsmithLib · Fabric 1.21.11（非官方移植 / Unofficial Port）

> **中文** · [English below](#english)

---

## 下载 / Download

| 方式 | 链接 |
|---|---|
| **直接下载 jar** | [gunsmithlib-1.21.11-7.1.1-fabric.jar](https://github.com/Haikimipingpong/GunsmithLib-Fabric-1.21.11/raw/main/dist/gunsmithlib-1.21.11-7.1.1-fabric.jar) |
| **发布页（含更新说明）** | [releases](https://github.com/Haikimipingpong/GunsmithLib-Fabric-1.21.11/releases) |

> **GunsmithLib 1.21.11 Fabric 移植版** —— 这是 [GunsmithLib](https://github.com/ChloePrime/GunsmithLib) 的
> **unofficial GunsmithLib port for Minecraft 1.21.11 Fabric**，为 [TaCZ](https://github.com/MCModderAnchor/TACZ)
> 枪械模组提供扩展支持。需要 Minecraft 1.21.11 + Fabric Loader + Fabric API。

## 中文

### 这是什么

本仓库是 **[GunsmithLib](https://github.com/ChloePrime/GunsmithLib)**（作者 **ChloePrime**，GPL-3.0）
从 **NeoForge 1.21.1** 移植到 **Minecraft 1.21.11 / Fabric** 的**非官方**源码。

GunsmithLib 是枪械模组 **TaCZ（Timeless and Classics Zero）** 的扩展库 / 前置，
面向枪包作者提供数据驱动的扩展能力（枪盾、火控、能量武器、破片、空爆、跳弹、
药水效果、命中粒子……）以及 Lua 脚本 API。

> **本项目与原作者无关，未获作者审核或背书。**
> 这是一次性的社区移植，**作者不会维护本仓库**（详见下方"维护状态"）。

### 当前状态（实话实说）

**能玩，但功能不完整。** 已完成平台迁移与核心链路，约四分之一的功能因
1.21.11 的 API 变动暂时缺失。

已验证可用：

- 启动 / 进入世界 / 退出，无崩溃
- 枪械拿取、开火、换弹（由 TaCZ 本体提供）
- **变体系统**：短按切换武器部件、长按打开轮盘选择弹种（含图标）
- **手持属性加成**：枪械与配件属性合并后施加给玩家
- 枪包扩展数据（`gunsmithlib_extension`）完整解析
- 键鼠输入事件、数据包重载、生物/玩家 tick 事件
- 方块与物品注册

已知降级、已下线的 mixin、以及"能做到但没做"的清单，见
[已知限制](#已知限制与未实现项)。

### 安装

运行需要：

| 组件 | 版本 |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.19.3 |
| Fabric API | 0.141.6+1.21.11 |
| Java | 21 |
| [TaCZ Refabricated](https://github.com/q14433686-arch/TaCZ_Refabricated_Unofficial)（非官方 Fabric 移植） | 1.1.8 R3-hotfix4 / 1.21.11 分支 |
| [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) | 21.11.1+ |

> 注意：底座 TaCZ 本身也是**非官方 Fabric 移植**，其稳定性不属于本项目责任范围。

### 构建

```bash
export JAVA_HOME=/path/to/jdk-25      # 必须是 JDK 25，fabric-loom 1.19.4 要求
./gradlew build
```

首次构建会下载 Gradle 9.8、Minecraft 1.21.11 与 Fabric Loom。
若 `./gradlew` 无法下载 Gradle（部分地区访问 services.gradle.org 受限），
可改用本机已安装的 Gradle 9.x：`gradle build`。

编译期依赖放在 `lib/`，**不随本仓库分发**（版权归各自作者），请自行下载：

- TaCZ Refabricated 1.21.11
- Forge Config API Port 21.11.1（Fabric）
- luaj（figura 分支，可从 TaCZ 的 `META-INF/jars/` 内解出）

### 与上游的关键差异

| 层 | 说明 |
|---|---|
| `fcompat/` | 自建 NeoForge 兼容层：事件总线、`DeferredRegister`/`DeferredHolder`、能力、配置、事件类 |
| `fabric/` | Fabric 适配层：入口点、事件钩子（tick / 登录 / 重载 / 能力注册）、属性注入、订阅类扫描 |
| `cn/chloeprime/commons/` | 上游私有库 `kuroutils2` 的最小替身（该库仅支持 1.21.1，运行期会 `NoSuchMethodError`） |
| `com/rbasamoyai/…`、`net/neoforged/fml/…` | 为链接第三方 jar 而补的同名桩类 |

主要适配点：

1. **事件总线** —— NeoForge 自动扫描 `@EventBusSubscriber` 并分发事件；Fabric 没有，
   改为运行时扫描 + 自建总线 + mixin 注入真实时机（`LivingEntity#tick`、`Player#tick`、
   `KeyboardHandler#keyPress`、`MouseHandler#onButton`、`ItemStack#forEachModifier` 等）。
2. **属性注入** —— NeoForge 用 `EntityAttributeModificationEvent`，Fabric 改为在
   `AttributeSupplier.Builder#build` 时注入模组的 10 个自定义属性。
3. **RPC** —— 上游依赖私有库的客户端↔服务端远程调用，改为**本地直调**
   （单机等价；多人下跨玩家同步不完整）。
4. **变体切换** —— 改为真实 Fabric 网络包 `C2SSwitchVariant`。

### 已知限制与未实现项

**明确无法还原**（平台层面无解）

- AAA Particles 粒子效果：该库只有 Forge/NeoForge 版，等于需要再移植一个模组
- NeoForge 专属渲染时机（如"手部渲染前"插入点）：1.21.9 起 GUI 改为延迟提交，无等价阶段
- 依赖已从 Minecraft 彻底删除且无替代的类（`ItemOverrides`/`BakedModel`/`ShaderInstance`）

**能做到但要重写**

- 旧枪包自动升级（TaCZ Fabric 版删除了转换器类）
- 测距仪与 TaCZ 瞄准镜的集成（Fabric 版无对应类）
- 枪盾完整判定链、绕过无敌帧的特殊伤害链路

**只是没做（后续可补）**

- Lua 脚本事件桥接（TaCZ 事件未转发给脚本系统）
- 多人游戏同步（本地直调改真包）
- 激光光柱渲染、v2 准星、HUD 弹药类型显示
- 伤害类型标签注入、配件属性 tooltip

**已下线的 mixin**：65 个中启用 49 个。下线原因多为目标方法在 1.21.11 改名或删除，
清单可在 `src/main/resources/gunsmithlib.mixins.json` 与上游同名文件对比得出，
代码内降级处均标注 `// PORT:`。

### 维护状态（重要）

**本仓库不会被维护。**

- 这是一次性的移植工作，**不保证跟进上游更新**，也不承诺响应时间。
- **Issue / PR 欢迎提交**，但合并不保证。
- 代码按"现状"提供，**不提供任何担保**；使用风险自负。
- 如需长期维护的版本，建议基于上游 [GunsmithLib](https://github.com/ChloePrime/GunsmithLib)
  自行继续，或联系原作者。

> **最难的 Fabric 1.21.11 地基已经打通，剩下的交给你们了。欢迎提交 PR。**
>
> 接手前请先看 **[技术债清单](TECHNICAL_DEBT.md)** —— 里面列了 3 条建议优先处理的、
> 6 处语义偏差、以及所有降级点的定位方法。

### 致谢

- **ChloePrime** —— GunsmithLib 原作者。本项目的全部逻辑、设计、枪包数据格式
  都来自其作品；没有他的工作就没有这个移植。
- **TaCZ Dev Team** —— TaCZ（Timeless and Classics Zero）本体。
- **Sh1roCu 及 TaCZ Refabricated 的贡献者** —— 非官方 Fabric 移植，本项目的运行底座。
- **FabricMC** —— Fabric Loader / API / Loom。
- **Fuzss** —— Forge Config API Port。

也许哪天我发现我自己都用不了了，我就会去维护一下。

本项目沿用上游 **GPL-3.0** 许可，详见 [LICENSE](LICENSE)；
第三方组件授权与署名见 [NOTICE.md](NOTICE.md)。

---

## English

### What is this

An **unofficial** port of **[GunsmithLib](https://github.com/ChloePrime/GunsmithLib)**
by **ChloePrime** (GPL-3.0) from **NeoForge 1.21.1** to **Minecraft 1.21.11 / Fabric**.

GunsmithLib is an extension library for the gun mod **TaCZ (Timeless and Classics Zero)**,
providing data-driven features for gun pack authors (gun shields, fire control, energy
weapons, fragmentation, airburst, ricochet, potion effects, hit particles, …) plus a Lua
scripting API.

> **This project is not affiliated with, reviewed by, or endorsed by the original author.**
> It is a one-off community port and **will not be maintained** (see below).

### Status

**Playable, but incomplete.** The platform migration and core feature paths work;
roughly a quarter of the features are missing due to API changes in 1.21.11.

Verified working:

- Launching, entering a world, exiting — stable, no crashes
- Holding / firing / reloading guns (provided by TaCZ itself)
- **Ammo-variant system**: short-press to switch weapon parts, long-press for the selection
  wheel (icons included)
- **Held-item attribute bonuses**: gun + attachment modifiers merged and applied to the player
- Full parsing of gun pack extension data (`gunsmithlib_extension`)
- Keyboard/mouse input events, datapack reload, entity/player tick events
- Block and item registration

See [Known limitations](#known-limitations) for the degraded features and disabled mixins.

### Installation

| Component | Version |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.19.3 |
| Fabric API | 0.141.6+1.21.11 |
| Java | 21 |
| [TaCZ Refabricated](https://github.com/q14433686-arch/TaCZ_Refabricated_Unofficial) (unofficial Fabric port) | 1.1.8 R3-hotfix4 / 1.21.11 branch |
| [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) | 21.11.1+ |

> Note: the underlying TaCZ build is itself an **unofficial Fabric port**; its stability is
> outside the scope of this project.

### Building

```bash
export JAVA_HOME=/path/to/jdk-25      # JDK 25 is required by fabric-loom 1.19.4
./gradlew build
```

Compile-time dependencies live in `lib/` and are **not redistributed** here
(they belong to their respective authors). Download them yourself:

- TaCZ Refabricated 1.21.11
- Forge Config API Port 21.11.1 (Fabric)
- luaj (figura fork — can be extracted from TaCZ's `META-INF/jars/`)

### Key differences from upstream

| Layer | Purpose |
|---|---|
| `fcompat/` | Hand-written NeoForge compatibility layer: event bus, `DeferredRegister`/`DeferredHolder`, capabilities, config, event classes |
| `fabric/` | Fabric glue: entrypoints, event hooks (tick / login / reload / capability registration), attribute injection, subscriber scanning |
| `cn/chloeprime/commons/` | Minimal stand-in for `kuroutils2`, an upstream-private library that only targets 1.21.1 and throws `NoSuchMethodError` at runtime |
| `com/rbasamoyai/…`, `net/neoforged/fml/…` | Stub classes so third-party jars can link |

1. **Event bus** — NeoForge scans `@EventBusSubscriber` and dispatches automatically; Fabric
   does not. This port scans at runtime, keeps its own bus, and injects the real timings via
   mixins (`LivingEntity#tick`, `Player#tick`, `KeyboardHandler#keyPress`,
   `MouseHandler#onButton`, `ItemStack#forEachModifier`, …).
2. **Attributes** — `EntityAttributeModificationEvent` is replaced by injecting the mod's ten
   custom attributes in `AttributeSupplier.Builder#build`.
3. **RPC** — upstream relies on its private library's client↔server calls; here they execute
   **locally** (equivalent in singleplayer, incomplete cross-player sync in multiplayer).
4. **Variant switching** — reimplemented with a real Fabric packet (`C2SSwitchVariant`).

### Known limitations

**Not achievable** (platform level)

- AAA Particles effects — Forge/NeoForge only; would require porting another mod
- NeoForge-specific render timings (e.g. "before hand rendering") — GUI rendering became
  deferred-submission in 1.21.9, no equivalent stage exists
- Classes removed from Minecraft with no replacement (`ItemOverrides` / `BakedModel` / `ShaderInstance`)

**Doable but would require a rewrite**

- Legacy gun pack auto-upgrade (TaCZ's Fabric port dropped the converter classes)
- Rangefinder integration with TaCZ optics (no corresponding classes in the Fabric port)
- Full gun-shield blocking chain; special damage path that bypasses i-frames

**Simply not done yet**

- Lua script event bridging (TaCZ events are not forwarded to the scripting system)
- Multiplayer sync (local calls → real packets)
- Laser beam rendering, v2 crosshair, HUD ammo-type display
- Damage-type tag injection, attachment attribute tooltips

**Disabled mixins**: 52 of 65 are enabled. Most are disabled because their target methods were
renamed or removed in 1.21.11. Degraded spots are marked `// PORT:` in the code.

### Maintenance

**This repository will not be maintained.**

- It is a one-off port: **no guarantee of tracking upstream updates**, and no promised response time.
- **Issues / PRs are welcome**, but merging is not guaranteed.
- Provided **as-is**, without warranty of any kind; use at your own risk.
- If you need a maintained version, build on the upstream
  [GunsmithLib](https://github.com/ChloePrime/GunsmithLib) instead.

> **The hardest part — the Fabric 1.21.11 foundation — is done. The rest is up to you. PRs are welcome.**
>
> Before contributing, read **[TECHNICAL_DEBT.md](TECHNICAL_DEBT.md)** — it lists 3 items worth
> fixing first, 6 known semantic deviations, and how to locate every degraded spot.

### Credits

- **ChloePrime** — author of GunsmithLib. All the logic, design and gun pack data formats
  come from their work; this port would not exist without it.
- **TaCZ Dev Team** — TaCZ (Timeless and Classics Zero).
- **Sh1roCu and TaCZ Refabricated contributors** — the unofficial Fabric port this build runs on.
- **FabricMC** — Fabric Loader / API / Loom.
- **Fuzss** — Forge Config API Port.

Maybe one day, when I find I can no longer use it myself, I'll come back and maintain it.

Licensed under **GPL-3.0**, inherited from upstream — see [LICENSE](LICENSE).
Third-party attribution in [NOTICE.md](NOTICE.md).
