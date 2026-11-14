package mod.chloeprime.gunsmithlib.mixin.common.minecraft;

import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import mod.chloeprime.gunsmithlib.fcompat.PlayerTickEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires PlayerTickEvent, which NeoForge patches into Player#tick. */
@Mixin(Player.class)
public abstract class PlayerTickMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void gunsmithlib$tickPre(CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Pre((Player) (Object) this));
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void gunsmithlib$tickPost(CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post((Player) (Object) this));
    }
}
