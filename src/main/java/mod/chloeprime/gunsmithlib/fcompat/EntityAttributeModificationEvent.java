package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.List;

public class EntityAttributeModificationEvent extends Event {
    private record Entry(EntityType<?> type, Holder<Attribute> attribute, Double value) {}

    private final List<Entry> entries = new ArrayList<>();

    public void add(EntityType<?> type, Holder<Attribute> attribute) {
        entries.add(new Entry(type, attribute, null));
    }

    public void add(EntityType<?> type, Holder<Attribute> attribute, double value) {
        entries.add(new Entry(type, attribute, value));
    }

    @SuppressWarnings("unchecked")
    public java.util.Set<EntityType<? extends LivingEntity>> getTypes() {
        java.util.Set<EntityType<? extends LivingEntity>> types = new java.util.LinkedHashSet<>();
        net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(t -> LivingEntity.class.isAssignableFrom(t.getBaseClass()))
                .forEach(t -> types.add((EntityType<? extends LivingEntity>) t));
        return types;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
