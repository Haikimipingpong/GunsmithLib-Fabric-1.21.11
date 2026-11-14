package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemAttributeModifierEvent extends Event {
    public record Entry(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlot slot) {}

    private final ItemStack itemStack;
    private final List<Entry> entries = new ArrayList<>();

    public ItemAttributeModifierEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void addModifier(Attribute attribute, AttributeModifier modifier) {
        addModifier(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifier, null);
    }

    public void addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        addModifier(attribute, modifier, null);
    }

    public void addModifier(EquipmentSlot slot, AttributeModifier modifier) {
        // 旧 API：只带槽位，无法确定属性，忽略
    }

    private void addModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlot slot) {
        if (attribute == null || modifier == null) return;
        entries.add(new Entry(attribute, modifier, slot));
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
