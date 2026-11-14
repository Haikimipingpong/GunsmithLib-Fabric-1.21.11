package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BuildCreativeModeTabContentsEvent extends Event {
    private final CreativeModeTab tab;
    private final List<ItemStack> accepted = new ArrayList<>();

    public BuildCreativeModeTabContentsEvent(CreativeModeTab tab) {
        this.tab = tab;
    }

    public CreativeModeTab getTab() {
        return tab;
    }

    public net.minecraft.resources.ResourceKey<CreativeModeTab> getTabKey() {
        return net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).orElseThrow();
    }

    public void accept(ItemStack stack) {
        accepted.add(stack);
    }

    public List<ItemStack> getAccepted() {
        return accepted;
    }
}
