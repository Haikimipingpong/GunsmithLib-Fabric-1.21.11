package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class DeferredRegister<T> {
    private record Entry<T>(Identifier id, Supplier<? extends T> factory, Consumer<T> binder) {}

    private final Registry<T> registry;
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String modid;
    private final List<Entry<T>> entries = new ArrayList<>();
    private boolean registered;

    private DeferredRegister(Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey, String modid) {
        this.registry = registry;
        this.registryKey = registryKey;
        this.modid = modid;
    }

    @SuppressWarnings("unchecked")
    private Registry<T> registry() {
        if (registry != null) return registry;
        return (Registry<T>) net.minecraft.core.registries.BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
    }

    public static <B> DeferredRegister<B> create(Registry<B> registry, String modid) {
        return new DeferredRegister<>(registry, null, modid);
    }

    public static DeferredRegister<net.minecraft.world.level.block.Block> createBlocks(String modid) {
        return create(net.minecraft.core.registries.BuiltInRegistries.BLOCK, modid);
    }

    public static DeferredRegister<net.minecraft.world.item.Item> createItems(String modid) {
        return create(net.minecraft.core.registries.BuiltInRegistries.ITEM, modid);
    }

    public static <B> DeferredRegister<B> create(ResourceKey<? extends Registry<B>> key, String modid) {
        return new DeferredRegister<>(null, key, modid);
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> sup) {
        var id = Identifier.fromNamespaceAndPath(modid, name);
        var holder = new DeferredHolder<T, I>(id, sup);
        var entry = new Entry<T>(id, sup, holder::bind);
        entries.add(entry);
        if (registered) {
            bind(entry);
        }
        return holder;
    }

    public void register(IEventBus bus) {
        if (registered) return;
        registered = true;
        for (var entry : entries) {
            bind(entry);
        }
    }

    private void bind(Entry<T> entry) {
        var value = entry.factory().get();
        Registry.register(registry(), entry.id(), value);
        entry.binder().accept(value);
    }
}
