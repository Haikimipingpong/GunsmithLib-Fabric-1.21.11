package mod.chloeprime.gunsmithlib.mixin.common.minecraft;

import mod.chloeprime.gunsmithlib.fcompat.EntityTickEvent;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires EntityTickEvent, which NeoForge patches into Entity#tick. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityTickMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void gunsmithlib$tickPre(CallbackInfo ci) {
        if (!EntityTickEvent.hasListeners()) {
            return;
        }
        NeoForge.EVENT_BUS.post(new EntityTickEvent.Pre((LivingEntity) (Object) this));
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void gunsmithlib$tickPost(CallbackInfo ci) {
        if (!EntityTickEvent.hasListeners()) {
            return;
        }
        NeoForge.EVENT_BUS.post(new EntityTickEvent.Post((LivingEntity) (Object) this));
    }
}
