# 技术债清单 / Technical Debt

本移植是**时间受限的工程妥协**，不是完美实现。这份清单把已知的"将来会被骂"的地方
逐条列出，方便二次开发者接手。

> 上游 GunsmithLib 的原始代码质量很好，**不要去改它**。下面的问题全部集中在移植新增的
> 四层里：`fcompat/`、`fabric/`、`cn/chloeprime/commons/`、桩类包。

## 排在最前面的三条（建议优先处理）

### 1. `fcompat.ModConfigSpec` 是重复造轮子

- **位置**：`fcompat/ModConfigSpec.java`（246 行）
- **现状**：自己实现了一套 JSON 配置读写，替代 NeoForge 的 `ModConfigSpec`。
- **问题**：项目已经依赖 **Forge Config API Port**（提供 `ForgeConfigSpec`，
  API 与 NeoForge 几乎一致），这里等于重写了一遍且没有 GUI 支持、没有 TOML 格式。
- **建议**：删掉它，`Config.java` / `StartupConfig.java` / `GunsmithClientConfig.java`
  改用 `net.minecraftforge.common.ForgeConfigSpec`，用 FCAP 的注册 API。
- **影响面**：3 个配置文件 + 一处入口注册，改动集中、风险低、收益大。

### 2. RPC 替身会"假装成功"

- **位置**：`cn/chloeprime/commons/rpc/RPC.java`、`RPCTarget.java`
- **现状**：上游的客户端↔服务端远程调用被改成**本地直接执行**。
- **问题**：单机（集成服务端同进程）等价；**多人游戏下 `to(player)` / `near(entity)`
  的目标玩家不会收到任何东西**，且调用方无法感知失败——排查起来会非常困惑。
- **建议**：把 `RPCTarget` 改成真正的 Fabric 包（`C2SSwitchVariant` 已经是现成范例），
  或者至少在 debug 日志里打印"跨玩家 RPC 被忽略"。

### 3. 事件总线用反射派发

- **位置**：`fcompat/SimpleEventBus.java`
- **现状**：每个监听器调用都走 `Method.invoke`。
- **问题**：`EntityTickEvent` 每 tick 对**每个生物**派发两次，生物多时开销明显
  （目前用 `hasListeners()` 做了短路，但一旦有人注册监听就会开始反射调用）。
- **建议**：注册期生成 `Consumer<Event>` 的方法句柄（`LambdaMetafactory`）缓存起来，
  或按事件类型预编译调用链。

## 已知的语义偏差（不是 bug，但会让人困惑）

| 位置 | 偏差 |
|---|---|
| `fcompat/DeferredHolder` | 实现了 `Holder` 接口，但 `unwrapKey()` 恒返回 `Optional.empty()`、`kind()` 恒为 `REFERENCE`。目前够用，接入需要真实 key 的逻辑（数据生成、序列化）会踩坑 |
| `fcompat/FakePlayerFactory` | 恒返回 `null`（Fabric 无假玩家）。调用方若不做空检查会 NPE，只是当前路径没触发 |
| `cn/chloeprime/commons/rpg/DamageSources` | `injectIs` / `injectIsNot` 是**空实现**（Fabric 的 `DamageSource` 不可变）。依赖伤害类型标签注入的枪包行为会不对 |
| `cn/chloeprime/commons/async/TickScheduler` | `delay(int)` 返回的 `CompletableFuture` 在任务被取消时**永远不会完成**，用 `.thenRun` 会静默挂住 |
| `fcompat/ItemAttributeModifierEvent` | 只保留 `Holder<Attribute>` + modifier，槽位信息丢失（原版按 `EquipmentSlotGroup` 分组） |
| `cn/chloeprime/commons/lang4/StringName` | 上游是 record，这里改成了普通类 + Gson 适配器（为了兼容枪包的字符串写法），`equals`/`hashCode` 语义一致但类型不同 |

## 故意降级的功能（详见 README）

代码里所有降级点都标了 `// PORT:`（共 30 处），可用下面命令定位：

```bash
grep -rn "// PORT:" src/main/java
```

其中**永远做不回来**的只有 3 类：AAA Particles 粒子、NeoForge 专属渲染时机、
被彻底删除且无替代的原版 API。其余都是"要做但没做"。

## 已下线的 mixin（16 个）

`src/main/resources/gunsmithlib.mixins.json` 里当前启用 49 / 上游 65。
下线的大多是因为目标方法在 1.21.11 改名或删除，逐个恢复的方法：

1. 用 `javap -p` 查新签名；
2. 改 `@Inject/@WrapOperation` 的 `method` 与回调参数列表；
3. 重新加回 json 并启动验证（mixin 失败会直接崩，反馈很快）。

## 结构说明（为什么不是一坨）

```
src/main/java/
├── mod/chloeprime/gunsmithlib/     上游代码（339 文件），几乎原样
│   ├── api/ common/ client/ mixin/ network/   上游原有分层
│   ├── fcompat/                    ★ 新增：NeoForge API 替身（约 60 个小类，中位数 40 行）
│   └── fabric/                     ★ 新增：Fabric 胶水（入口点、事件钩子、注册器、属性注入）
├── cn/chloeprime/commons/          ★ 新增：上游私有库 kuroutils2 的替身
├── com/rbasamoyai/…                ★ 新增：第三方库桩类
└── net/neoforged/fml/…             ★ 新增：单个枚举桩
```

新增层合计约 **3,000 行**，相对于上游 26,000 行是薄薄一层；每个类职责单一，
命名与上游的 NeoForge 包路径一一对应，方便对照查证。

## 想贡献的话，性价比最高的顺序

1. `ModConfigSpec` → Forge Config API Port（删代码，最爽）
2. Lua 事件桥接（把 TaCZ 的 Fabric `Event` 转发到 `fcompat` 总线）
3. 多人游戏 RPC 换真包
4. 逐个恢复 16 个下线的 mixin
5. 激光/准星等渲染恢复（需要重写，成本最高）
