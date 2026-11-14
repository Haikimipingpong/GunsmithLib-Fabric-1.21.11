package mod.chloeprime.gunsmithlib.common;

import mod.chloeprime.gunsmithlib.api.common.BulletCreateEvent;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.util.InternalBulletCreateEvent;
import mod.chloeprime.gunsmithlib.mixin.common.tacz.EntityKineticBulletAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import mod.chloeprime.gunsmithlib.fcompat.EntityJoinLevelEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventPriority;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;

@EventBusSubscriber
public class BulletCreateEventDistributor {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onBulletCreate(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Projectile bullet)) {
            return;
        }
        if (!(bullet.getOwner() instanceof LivingEntity shooter)) {
            return;
        }

        var gunId = bullet instanceof EntityKineticBulletAccessor accessor
                ? accessor.getGunId()
                : null;
        var gun = Gunsmith.getGunInfo(shooter.getMainHandItem())
                .filter(gi -> gunId == null || gunId.equals(gi.gunId()))
                .or(() -> Gunsmith.getGunInfo(Gunsmith.createGunItemFromId(gunId, bullet.registryAccess())))
                .orElse(null);
        if (gun == null) {
            return;
        }

        var bcEvent = new BulletCreateEvent(bullet, shooter, gun);
        NeoForge.EVENT_BUS.post(bcEvent);
        NeoForge.EVENT_BUS.post(new InternalBulletCreateEvent(bcEvent));
    }
}
