package mod.chloeprime.gunsmithlib.mixin.common.minecraft;

import mod.chloeprime.gunsmithlib.fcompat.ItemAttributeModifierEvent;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

/**
 * Fires GunsmithLib's ItemAttributeModifierEvent when vanilla collects an item's attribute
 * modifiers, so guns can grant attribute bonuses while held (NeoForge fires this itself).
 */
@Mixin(ItemStack.class)
public abstract class ItemStackAttributeMixin {
    @Inject(
            method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At("TAIL"))
    private void gunsmithlib$injectAttributeModifiers(EquipmentSlot slot,
                                                      BiConsumer<Holder<Attribute>, AttributeModifier> consumer,
                                                      CallbackInfo ci) {
        if (slot != EquipmentSlot.MAINHAND) {
            return;
        }
        var stack = (ItemStack) (Object) this;
        var event = new ItemAttributeModifierEvent(stack);
        NeoForge.EVENT_BUS.post(event);
        for (var entry : event.getEntries()) {
            try {
                consumer.accept(entry.attribute(), entry.modifier());
            } catch (Throwable ignored) {
            }
        }
    }
}
