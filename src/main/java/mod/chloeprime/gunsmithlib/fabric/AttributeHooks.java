package mod.chloeprime.gunsmithlib.fabric;

import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.fire_control.FireControlAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/** Injects GunsmithLib's custom attributes into every LivingEntity's AttributeSupplier. */
public final class AttributeHooks {
    private static volatile boolean injected;

    public static void inject(AttributeSupplier.Builder builder) {
        // 这些属性原本通过 NeoForge 的 EntityAttributeModificationEvent 添加，
        // Fabric 没有该事件，这里在属性表构建时统一注入。
        builder.add(GunAttributes.BULLET_DAMAGE);
        builder.add(GunAttributes.ARMOR_PIERCING_RATIO);
        builder.add(GunAttributes.HEADSHOT_MULTIPLIER);
        builder.add(GunAttributes.BULLET_SPEED);
        builder.add(GunAttributes.V_RECOIL);
        builder.add(GunAttributes.H_RECOIL);
        builder.add(GunAttributes.RPM);
        builder.add(GunAttributes.RELOAD_SPEED);
        builder.add(FireControlAttributes.AIM_LOCK_RANGE);
        builder.add(FireControlAttributes.AIM_LOCK_ANGLE);
    }

    private AttributeHooks() {}
}
