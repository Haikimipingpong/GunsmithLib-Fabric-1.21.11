package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

public final class RegisterCapabilitiesEvent extends Event {
    public static final Map<Capabilities.CapabilityType<?>, Map<Item, ICapabilityProvider<net.minecraft.world.item.ItemStack, ?, ?>>> ITEM_PROVIDERS = new HashMap<>();

    public <T, C> void registerItem(Capabilities.CapabilityType<T> type,
                                    ICapabilityProvider<net.minecraft.world.item.ItemStack, Void, T> provider,
                                    Item... items) {
        for (var item : items) {
            ITEM_PROVIDERS.computeIfAbsent(type, k -> new HashMap<>()).put(item, provider);
        }
    }

    public <T, C> void registerBlockEntity(Capabilities.CapabilityType<T> type,
                                           ICapabilityProvider<?, ?, T> provider,
                                           BlockEntityType<?>... types) {
        // Block entity capabilities are not used by GunsmithLib yet.
    }
}
