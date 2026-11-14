package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal replacement for NeoForge capabilities: item capabilities are looked up by
 * (capability type, item) and the provider is created per query.
 */
public final class Capabilities {
    public record CapabilityType<T>(String name) {}

    public static final class ItemHandler {
        public static final CapabilityType<IItemHandler> ITEM = new CapabilityType<>("item_handler/item");
        public static final CapabilityType<IItemHandler> ENTITY = new CapabilityType<>("item_handler/entity");

        private ItemHandler() {}
    }

    public static final class EnergyStorage {
        public static final CapabilityType<IEnergyStorage> ITEM = new CapabilityType<>("energy/item");
        public static final CapabilityType<IEnergyStorage> BLOCK = new CapabilityType<>("energy/block");

        private EnergyStorage() {}
    }

    private Capabilities() {}
}
