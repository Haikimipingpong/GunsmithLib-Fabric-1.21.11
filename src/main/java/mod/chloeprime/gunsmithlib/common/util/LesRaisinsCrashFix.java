package mod.chloeprime.gunsmithlib.common.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import mod.chloeprime.gunsmithlib.fcompat.IEventBus;
import mod.chloeprime.gunsmithlib.fcompat.ModList;
import mod.chloeprime.gunsmithlib.fcompat.DeferredRegister;

import java.util.function.Supplier;

public class LesRaisinsCrashFix {
    public static final String LR_ID = "lrtactical";
    public static final Component TECH_HINT = Component.translatable("gunsmithlib.item.technical.bugfix");

    public static void init(IEventBus bus) {
        if (ModList.get().isLoaded(LR_ID)) {
            return;
        }
        var dfr = DeferredRegister.createItems(LR_ID);
        var factory = (Supplier<Item>) () -> new Item(new Item.Properties().component(DataComponents.ITEM_NAME, TECH_HINT));
        dfr.register("throwable", factory);
        dfr.register("melee", factory);
        dfr.register("flash_shield", factory);
        dfr.register(bus);
    }

    private LesRaisinsCrashFix() {
    }
}
