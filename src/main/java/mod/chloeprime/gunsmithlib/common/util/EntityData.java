package mod.chloeprime.gunsmithlib.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.WeakHashMap;

/** Replaces NeoForge's {@code Entity#getPersistentData} with a lifetime-scoped custom data map. */
public final class EntityData {
    private static final Map<Entity, CompoundTag> DATA = java.util.Collections.synchronizedMap(new WeakHashMap<>());

    public static CompoundTag of(Entity entity) {
        return DATA.computeIfAbsent(entity, e -> new CompoundTag());
    }

    private EntityData() {}
}
