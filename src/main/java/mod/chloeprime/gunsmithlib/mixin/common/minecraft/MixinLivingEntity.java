package mod.chloeprime.gunsmithlib.mixin.common.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.api.item.IGun;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.shield.ShieldBehavior;
import mod.chloeprime.gunsmithlib.common.internal.GunAttributeSyncState;
import mod.chloeprime.gunsmithlib.common.internal.MobEffectForceApplicable;
import mod.chloeprime.gunsmithlib.common.util.FloatConsumer;
import mod.chloeprime.gunsmithlib.common.util.HurtFunction1;
import mod.chloeprime.gunsmithlib.common.util.HurtFunction2;
import mod.chloeprime.gunsmithlib.common.util.SpecialHurtable;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import mod.chloeprime.gunsmithlib.fcompat.MobEffectEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements
        SpecialHurtable,
        GunAttributeSyncState,
        MobEffectForceApplicable {

    // 枪盾
    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void gunShield(CallbackInfoReturnable<Boolean> cir) {
        var self = (LivingEntity) (Object) this;
        var testSourcePos = self.getEyePosition().add(self.getLookAngle());
        if (ShieldBehavior.canBlockVanillaDamage(self, testSourcePos, self.getMainHandItem())) {
            cir.setReturnValue(true);
        }
    }

    // forceAddEffect Prime

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void gunsmith$forceAddEffectPrime(MobEffectInstance effect, @Nullable Entity cause) {
        var existing = this.activeEffects.get(effect.getEffect());
        @SuppressWarnings("DataFlowIssue")
        var addedEvent = new MobEffectEvent.Added((LivingEntity) (Object) this, existing, effect, cause);
        NeoForge.EVENT_BUS.post(addedEvent);

        if (existing == null) {
            this.activeEffects.put(effect.getEffect(), effect);
            this.onEffectAdded(effect, cause);
        } else if (existing.update(effect)) {
            this.onEffectUpdated(existing, true, cause);
        }
    }

    @Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;
    @Shadow protected abstract void onEffectUpdated(MobEffectInstance pEffectInstance, boolean pForced, @Nullable Entity pEntity);
    @Shadow protected abstract void onEffectAdded(MobEffectInstance pEffectInstance, @Nullable Entity pEntity);

    // 特殊伤害机制
    @Shadow public abstract void setHealth(float value);

    private @Unique boolean gunsmith$isInGunAttributeMode;
    private @Unique boolean gunsmith$isDoingSpecialHurtProcedure;

    @Override
    public boolean gunsmith$usingSpecialHurt() {
        return false;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public HurtFunction1 getSpecialHurtFunction1() {
        // 1.21.11 的 Entity#hurt 返回 void，这里包一层保持旧接口的 boolean 语义
        return (source, amount) -> {
            ((LivingEntity) (Object) this).hurt(source, amount);
            return true;
        };
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public HurtFunction2 getSpecialHurtFunction2() {
        // 1.21.11 的 actuallyHurt 需要 ServerLevel 参数
        return (source, amount) -> {
            var self = (LivingEntity) (Object) this;
            if (self.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                this.actuallyHurt(serverLevel, source, amount);
            }
        };
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public FloatConsumer getSpecialSetHealthFunction() {
        return this::setHealth;
    }

    @Override
    public void gunsmith$beginSpecialHurt() {
        gunsmith$isDoingSpecialHurtProcedure = true;
    }

    @Override
    public void gunsmith$endSpecialHurt() {
        gunsmith$isDoingSpecialHurtProcedure = false;
    }

    @Override
    public boolean gunsmith$isInGunMode() {
        return gunsmith$isInGunAttributeMode;
    }

    @Override
    public void gunsmith$setInGunMode(boolean value) {
        gunsmith$isInGunAttributeMode = value;
    }

    @Shadow protected abstract void actuallyHurt(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount);
}
