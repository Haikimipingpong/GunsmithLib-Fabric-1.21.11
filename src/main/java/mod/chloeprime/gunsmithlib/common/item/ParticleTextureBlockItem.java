package mod.chloeprime.gunsmithlib.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

public class ParticleTextureBlockItem extends BlockItem {
    public static final Component USAGE_HINT = Component
            .translatable("gunsmithlib.message.particle_block_hint")
            .withStyle(ChatFormatting.DARK_GRAY);

    public ParticleTextureBlockItem(Supplier<? extends Block> block, Properties properties) {
        super(block.get(), properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        if (flag.isAdvanced() && !hasLore(stack)) {
            lines.accept(USAGE_HINT);
        }
    }

    private static boolean hasLore(ItemStack stack) {
        return stack.get(DataComponents.LORE) != null;
    }
}
