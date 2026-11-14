package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingShieldBlockEvent extends Event {
    private final LivingEntity entity;
    private final DamageSource damageSource;
    private final float originalBlockedDamage;
    private float blockedDamage;

    public LivingShieldBlockEvent(LivingEntity entity, DamageSource damageSource, float blockedDamage) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.originalBlockedDamage = blockedDamage;
        this.blockedDamage = blockedDamage;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public float getOriginalBlockedDamage() {
        return originalBlockedDamage;
    }

    public float getBlockedDamage() {
        return blockedDamage;
    }

    public void setBlockedDamage(float blockedDamage) {
        this.blockedDamage = blockedDamage;
    }
}
