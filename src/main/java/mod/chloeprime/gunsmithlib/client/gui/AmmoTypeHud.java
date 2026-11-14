package mod.chloeprime.gunsmithlib.client.gui;

import com.mojang.blaze3d.vertex.*;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.resource.index.ClientAmmoIndex;
import com.tacz.guns.client.resource.pojo.display.gun.AmmoCountStyle;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.client.GunsmithClientConfig;
import mod.chloeprime.gunsmithlib.client.GunsmithLibClient;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import mod.chloeprime.gunsmithlib.compat.ModInstallationStatus;
import mod.chloeprime.gunsmithlib.client.util.GuiCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.RegisterShadersEvent;

import java.io.IOException;
import java.util.Optional;

@EventBusSubscriber(Dist.CLIENT)
public final class AmmoTypeHud {
    public static final int ICON_WIDTH = 16;
    public static final int ICON_HEIGHT = 16;
    public static final String SPLIT_TEXT = "×";



    public static boolean isIconEnabled() {
        // 弹种图标与 TaCZ Presence 不兼容
        if (ModInstallationStatus.TACZ_PRESENCE_INSTALLED) {
            return false;
        }
        return GunsmithClientConfig.HUD_AMMO_TYPE_ICON_OPTION.get() != GunsmithClientConfig.AmmoTypeHudMode.DISABLE;
    }

    public static boolean isIconShaded() {
        // 弹种图标与 TaCZ Presence 不兼容
        if (ModInstallationStatus.TACZ_PRESENCE_INSTALLED) {
            return false;
        }
        return GunsmithClientConfig.HUD_AMMO_TYPE_ICON_OPTION.get() == GunsmithClientConfig.AmmoTypeHudMode.STYLED;
    }

    public static float totalIconWidth() {
        return ICON_WIDTH + 4 + Minecraft.getInstance().font.width(SPLIT_TEXT) + iconXOffset();
    }

    public static float iconXOffset() {
        var isPercentDisplay = GunsmithLibClient.currentGunDisplay()
                .filter(display -> display.getAmmoCountStyle() == AmmoCountStyle.PERCENT)
                .isPresent();
        return isPercentDisplay ? 1.5f * Minecraft.getInstance().font.width("%") : 0;
    }

    public static void renderIcon(GuiGraphics graphics, int width, int height) {
        if (!isIconEnabled()) {
            return;
        }
        var slotTexture = GunsmithLibClient.currentGunInfo()
                .map(GsHelper::getAmmoId)
                .flatMap(TimelessAPI::getClientAmmoIndex)
                .map(ClientAmmoIndex::getSlotTextureLocation)
                .orElse(null);
        if (slotTexture == null) {
            return;
        }
        var textScale = 1.0f;
        var font = Minecraft.getInstance().font;
        GuiCompat.push(graphics);
        {
            GuiCompat.translate(graphics, iconXOffset(), 0);
            GuiCompat.scale(graphics, textScale, textScale);
            graphics.drawString(font, SPLIT_TEXT, (int) ((width - 20) / textScale), (int) ((height - 40f) / textScale), 0xfff0f0f0, false);
            GuiCompat.scale(graphics, 1 / textScale, 1 / textScale);

            GuiCompat.translate(graphics, 0, -2);
            blitAmmoTypeIcon(graphics, slotTexture, width - 18 + (int) (textScale * font.width(SPLIT_TEXT)), height - 44, ICON_WIDTH, ICON_HEIGHT);
        }
        GuiCompat.pop(graphics);
    }

    private static void blitAmmoTypeIcon(GuiGraphics graphics, Identifier atlas, int x, int y, int width, int height) {
        GuiCompat.blit(graphics, atlas, x, y, width, height, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    public static Optional<String> getAmmoName() {
        return Optional.ofNullable(Minecraft.getInstance().player)
                .map(LivingEntity::getMainHandItem)
                .flatMap(Gunsmith::getGunInfo)
                .map(GsHelper::getAmmoId)
                .flatMap(TimelessAPI::getClientAmmoIndex)
                .map(ClientAmmoIndex::getName)
                .map(I18n::get);
    }



    private AmmoTypeHud() {
    }
}
