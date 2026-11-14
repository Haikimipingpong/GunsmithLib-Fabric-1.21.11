package mod.chloeprime.gunsmithlib.client.gunpack_extension;

import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.explosive.AirburstSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import mod.chloeprime.gunsmithlib.fcompat.ModList;

import java.util.Optional;

public class AirburstHUD {
    private static final Minecraft MC=Minecraft.getInstance();
    private static final Component EXPLOSION_EMOJI = ModList.get().isLoaded("modernui")
            ? Component.literal("\uD83D\uDCA5")
            : Component.literal("\uD83D\uDCA5").withStyle(ChatFormatting.GOLD);

    public static void render(GuiGraphics graphics, int width, int height) {
        var gun = Optional.ofNullable(MC.player)
                .map(LivingEntity::getMainHandItem)
                .flatMap(Gunsmith::getGunInfo)
                .orElse(null);
        if (gun == null) {
            return;
        }
        var distance = AirburstSystem.getSelectedDistance(gun);
        if (distance.isEmpty()) {
            return;
        }
        var font = MC.font;
        var text = EXPLOSION_EMOJI.copy().append(Component.literal("%.0fm".formatted(distance.getAsDouble())).withStyle(ChatFormatting.GRAY));
        graphics.drawString(
                MC.font, text,
                width - 78 - font.width(text), height - 30, 0xff_aa_aa_aa
        );
        prepareForIconRendering();
    }

    private static void prepareForIconRendering() {
        // PORT: 1.21.9+ 移除了立即模式渲染状态。原版 HUD 现在由 RenderPipeline 统一提交，
        // 混合（blend）/深度测试（depth test）/着色器（shader）都由每次 draw 使用的 pipeline 决定，
        // 不再有全局可切换的状态，所以这里没有需要预先设置的全局渲染状态了。
    }
}
