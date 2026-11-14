package mod.chloeprime.gunsmithlib.mixin.common.minecraft;

import mod.chloeprime.gunsmithlib.fabric.AttributeHooks;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeSupplier.Builder.class)
public abstract class AttributeSupplierBuilderMixin {
    @Inject(method = "build", at = @At("HEAD"))
    private void gunsmithlib$injectAttributes(CallbackInfoReturnable<AttributeSupplier> cir) {
        AttributeHooks.inject((AttributeSupplier.Builder) (Object) this);
    }
}
