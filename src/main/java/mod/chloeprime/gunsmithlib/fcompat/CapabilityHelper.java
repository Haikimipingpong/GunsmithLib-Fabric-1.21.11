package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class CapabilityHelper {
    @SuppressWarnings("unchecked")
    public static <T> T get(ItemStack stack, Capabilities.CapabilityType<T> type) {
        var byItem = RegisterCapabilitiesEvent.ITEM_PROVIDERS.get(type);
        if (byItem == null || stack.isEmpty()) return null;
        var provider = byItem.get(stack.getItem());
        if (provider == null) return null;
        return ((ICapabilityProvider<ItemStack, Void, T>) provider).getCapability(stack, null);
    }

    public static <T> T get(Entity entity, Capabilities.CapabilityType<T> type) {
        return null;
    }

    private CapabilityHelper() {}
}
