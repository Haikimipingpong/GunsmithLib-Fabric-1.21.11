package mod.chloeprime.gunsmithlib.api.common.recipe;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import mod.chloeprime.gunsmithlib.fcompat.IEventBus;
import mod.chloeprime.gunsmithlib.fcompat.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class GunRecipeSerializers {
    @ApiStatus.Internal
    private static final DeferredRegister<RecipeSerializer<?>> DFR = DeferredRegister.create(Registries.RECIPE_SERIALIZER, GunsmithLib.MOD_ID);

    /**
     * 锻造台改变内容 id 的配方。
     * 示例配方：{@code run/tacz/debug/data/gunsmithlib/recipes/smith_example.json}
     */

    @ApiStatus.Internal
    public static void init(IEventBus bus) {
        DFR.register(bus);
    }
}
