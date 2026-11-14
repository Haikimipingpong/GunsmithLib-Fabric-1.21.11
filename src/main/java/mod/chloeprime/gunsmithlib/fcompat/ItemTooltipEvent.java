package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemTooltipEvent extends Event {
    private final ItemStack itemStack;
    private final List<Component> toolTip;
    private final Player entity;
    private final boolean advanced;

    public ItemTooltipEvent(ItemStack itemStack, List<Component> toolTip, @Nullable Player entity, boolean advanced) {
        this.itemStack = itemStack;
        this.toolTip = toolTip;
        this.entity = entity;
        this.advanced = advanced;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<Component> getToolTip() {
        return toolTip;
    }

    public Player getEntity() {
        return entity;
    }

    public boolean isAdvanced() {
        return advanced;
    }
}
