package mod.chloeprime.gunsmithlib.fabric;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import mod.chloeprime.gunsmithlib.fcompat.GunsmithLibLog;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;

/**
 * 把 TaCZ（Fabric 移植版）的 Fabric 事件转发到 GunsmithLib 自己的事件总线。
 * <p>
 * 上游 GunsmithLib 是 NeoForge 模组，直接订阅 TaCZ 的 NeoForge 事件；
 * 而 Fabric 版 TaCZ 把事件换成了 {@code net.fabricmc.fabric.api.event.Event}，
 * 所以必须在这里搭桥，否则所有依赖枪械事件的枪包功能（追踪、药水云、命中特效……）都不会触发。
 */
public final class TaCZBridges {
    public static void register() {
        guard("EntityHurtByGunEvent.PRE", () -> EntityHurtByGunEvent.PRE.register(NeoForge.EVENT_BUS::post));
        guard("EntityHurtByGunEvent.POST", () -> EntityHurtByGunEvent.POST.register(NeoForge.EVENT_BUS::post));
        guard("GunFireEvent", () -> GunFireEvent.CALLBACK.register(NeoForge.EVENT_BUS::post));
        guard("GunShootEvent", () -> GunShootEvent.CALLBACK.register(NeoForge.EVENT_BUS::post));
        guard("GunReloadEvent", () -> GunReloadEvent.CALLBACK.register(NeoForge.EVENT_BUS::post));
        guard("GunDrawEvent", () -> GunDrawEvent.CALLBACK.register(NeoForge.EVENT_BUS::post));
        guard("EntityKillByGunEvent", () -> EntityKillByGunEvent.CALLBACK.register(NeoForge.EVENT_BUS::post));
        guard("AttachmentPropertyEvent", () -> AttachmentPropertyEvent.CALLBACK.register(NeoForge.EVENT_BUS::post));
    }

    private static void guard(String name, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            GunsmithLibLog.warn("Failed to bridge TaCZ event {}", name, t);
        }
    }

    private TaCZBridges() {}
}
