package mod.chloeprime.gunsmithlib.client.gui;

import cn.chloeprime.commons.rpc.RPC;
import cn.chloeprime.commons.rpc.RPCTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Either;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.client.gunpack_extension.EnhancedGunDisplay;
import mod.chloeprime.gunsmithlib.client.gunpack_extension.GunsmithLibGunDisplayExtension;
import mod.chloeprime.gunsmithlib.client.input.SwitchPartOrAmmoTypeKey;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.ammo_variant.AmmoVariantSystem;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 感谢黄毛白鼠（hamsterbaron）提供的轮盘菜单代码 ~
 */
public class GunVariantSelectWheelScreen extends Screen {
    public static final Identifier FALLBACK_TEXTURE = GunsmithLib.loc("textures/gui/unknown_entry.png");

    private final List<Component> names;
    private final List<Either<ItemStack, Identifier>> icons;
    private final List<Identifier> gunIds;
    private final IntList ammoCountInBackpack;
    private int selectedIndex = -1;

    public GunVariantSelectWheelScreen(List<Identifier> gunIds) {
        this(gunIds, Component.literal("Ammo Select"));
    }

    public GunVariantSelectWheelScreen(List<Identifier> gunIds, Component title) {
        super(title);
        this.gunIds = gunIds;
        this.names = new ArrayList<>(gunIds.size());
        this.icons = new ArrayList<>(gunIds.size());
        this.ammoCountInBackpack = new IntArrayList(gunIds.size());
        initNamesAndIcons();
        fetchBackpackAmmoCounts();
    }

    private void initNamesAndIcons() {
        for (var gunId : gunIds) {
            var index = TimelessAPI.getClientGunIndex(gunId).orElse(null);

            var gunData = Optional.ofNullable(index).map(ClientGunIndex::getGunData).orElse(null);
            if (index == null || gunData == null) {
                names.add(Component.literal(gunId.toString()).withStyle(ChatFormatting.DARK_RED));
                icons.add(Either.right(FALLBACK_TEXTURE));
                continue;
            }
            var enhanced = Optional.ofNullable(index.getDefaultDisplay())
                    .map(EnhancedGunDisplay.class::cast)
                    .flatMap(EnhancedGunDisplay::gunsmith$getGunsmithLibExtension);
            var display = enhanced;
            var name = display.map(GunsmithLibGunDisplayExtension::getVariantName).orElse(null);
            if (name != null) {
                names.add(Component.translatable(name));
            } else {
                var ammo = AmmoItemBuilder.create().setId(gunData.getAmmoId()).build();
                names.add(ammo.getHoverName());
            }

            var icon = display.map(GunsmithLibGunDisplayExtension::getVariantIcon).orElse(null);
            // 贴图不存在或加载失败时退回子弹图标，避免画出白色/紫黑方块
            if (icon != null && ensureTextureLoaded(icon)) {
                icons.add(Either.right(icon));
            } else {
                var bullet = AmmoItemBuilder.create()
                        .setId(gunData.getAmmoId())
                        .setCount(1)
                        .build();
                icons.add(Either.left(bullet));
            }
        }
    }

    /**
     * 如果背包内弹药过多或有无限弹药则返回 {@link Integer#MAX_VALUE}
     */
    private void fetchBackpackAmmoCounts() {
        ammoCountInBackpack.size(gunIds.size());
        for (int i = 0; i < gunIds.size(); i++) {
            // 如果没有自定义图标（用的子弹图标）则向服务端请求子弹数量
            if (icons.get(i).left().isPresent()) {
                RPC.call(RPCTarget.toServer(), AmmoVariantSystem::fetchBackpackAmmoCountFromServer, gunIds.get(i), i);
            }
        }
    }

    /**
     * 如果背包内弹药过多或有无限弹药则 {@param count} 的值为 {@link Integer#MAX_VALUE}
     */
    public static void receiveBackpackAmmoCount(int slot, int count) {
        if (Minecraft.getInstance().screen instanceof GunVariantSelectWheelScreen screen) {
            if (slot >= 0 && slot < screen.ammoCountInBackpack.size()) {
                screen.ammoCountInBackpack.set(slot, count);
            }
        }
    }

    public static boolean isMouseOverSlot(
            double mouseX, double mouseY, double centerX, double centerY,
            double innerR, double outerR, double startAngle, double endAngle
    ) {
        var dx = mouseX - centerX;
        var dy = mouseY - centerY;

        var distSq = dx * dx + dy * dy;
        var dist = Math.sqrt(distSq);

        if (dist < innerR || dist > outerR) {
            return false;
        }

        var angle = Math.atan2(dy, dx);
        if (angle < -Math.PI / 2) {
            angle += 2 * Math.PI;
        }

        if (startAngle < -Math.PI / 2) {
            startAngle += 2 * Math.PI;
        }
        if (endAngle < -Math.PI / 2) {
            endAngle += 2 * Math.PI;
        }

        return endAngle < startAngle
                ? (angle >= startAngle || angle <= endAngle)
                : (angle >= startAngle && angle <= endAngle);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        selectedIndex = -1;

        var centerX = width / 2;
        var centerY = height / 2;
        var segmentsPerSlot = 20;

        var player = Minecraft.getInstance().player;

        for (int i = 0; i < icons.size(); i++) {
            var angleStep = 2 * Math.PI / icons.size();
            var startAngle = angleStep * i - Math.PI / 2 - angleStep / 2;
            var endAngle = startAngle + angleStep;

            int radius = 80;
            int innerRadius = radius - 32;
            int outerRadius = radius + 32;
            var isSelected = isMouseOverSlot(
                    mouseX, mouseY, centerX, centerY,
                    innerRadius, outerRadius, startAngle, endAngle);
            if (isSelected) {
                selectedIndex = i;
            }

            var sectorColor = isSelected ? -0x3f000001 : 0x40FFFFFF;

            drawSector(
                    graphics,
                    centerX,
                    centerY,
                    innerRadius,
                    outerRadius,
                    startAngle,
                    endAngle,
                    segmentsPerSlot,
                    sectorColor
            );

            var itemAngle = (startAngle + endAngle) / 2;
            var x = centerX + radius * Math.cos(itemAngle);
            var y = centerY + radius * Math.sin(itemAngle);

            var itemScale = 2.F;
            var icon = icons.get(i);
            // 渲染物品图标
            graphics.pose().pushMatrix();
            {
                graphics.pose().translate((float) x, (float) y);
                graphics.pose().scale(itemScale, itemScale);

                icon.ifLeft(ammoItem -> graphics.renderItem(ammoItem, -8, -8));
                icon.ifRight(png -> {
                    var size = TEXTURE_SIZES.get(png);
                    var texWidth = size != null ? size[0] : 16;
                    var texHeight = size != null ? size[1] : 16;
                    // 12 参重载真实顺序: (x, y, u, v, width, height, uWidth, vHeight, texW, texH)
                    // width/height 是屏幕尺寸，uWidth/vHeight 才是纹理取样区
                    graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, png,
                            -8, -8, 0.0F, 0.0F, 16, 16, texWidth, texHeight, texWidth, texHeight);
                });
            }
            graphics.pose().popMatrix();

            int ammoCount;
            var willRenderCount = icon.left().isPresent();
            if (willRenderCount) {
                String ammoCountText;
                if (player == null) {
                    ammoCount = 0;
                    ammoCountText = "";
                } else {
                    var gun = Gunsmith.getGunInfo(GunItemBuilder.create().setId(gunIds.get(i)).build()).orElse(null);
                    if (gun == null) {
                        ammoCount = 0;
                        ammoCountText = "";
                    } else {
                        var invAmmo = GsHelper.scanInventoryAmmo(player, gun);
                        var bpAmmo = ammoCountInBackpack.getInt(i);
                        if (invAmmo.isEmpty() || bpAmmo == Integer.MAX_VALUE) {
                            ammoCount = 9999;
                            ammoCountText = "∞";
                        } else {
                            ammoCount = invAmmo.getAsInt() + bpAmmo;
                            ammoCountText = String.valueOf(ammoCount);
                        }
                    }
                }
                var textWidth = font.width(ammoCountText);
                var textHeight = font.lineHeight;

                graphics.pose().pushMatrix();
                {
                    var scale = 1F;
                    graphics.pose().translate((float) (x), (float) (y + 20));
                    graphics.pose().scale((float) (scale), (float) (scale));
                    graphics.drawString(
                            font,
                            ammoCountText,
                            -textWidth / 2,
                            -textHeight / 2,
                            ammoCount <= 0 ? 0xffff0000 : 0xffffffff,
                            true);
                }
                graphics.pose().popMatrix();
            } else {
                // 渲染图标时使用有子弹时的配色
                ammoCount = 1;
            }
            if (isSelected) {
                var name = names.get(i);
                var nameWidth = font.width(name);
                var nameColor = ammoCount <= 0 ? 0xffff0000 : 0xffffffff;
                graphics.drawString(font, name, (width - nameWidth) / 2, (height - font.lineHeight) / 2, nameColor, true);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static final java.util.Map<Identifier, int[]> TEXTURE_SIZES = new java.util.HashMap<>();

    /**
     * 1.21.9+ 的纹理必须先在 TextureManager 里注册过，否则 blit 拿到的是白色占位纹理。
     * 枪包里的变体图标是独立 PNG，不经过图集，所以这里按需手动加载一次。
     */
    private static boolean ensureTextureLoaded(Identifier texture) {
        var mc = Minecraft.getInstance();
        try {
            var manager = mc.getTextureManager();
            if (TEXTURE_SIZES.containsKey(texture)) {
                return true;
            }
            var resource = mc.getResourceManager().getResource(texture);
            if (resource.isEmpty()) {
                return false;
            }
            try (var stream = resource.get().open()) {
                var image = com.mojang.blaze3d.platform.NativeImage.read(stream);
                var width = image.getWidth();
                var height = image.getHeight();
                manager.register(texture, new net.minecraft.client.renderer.texture.DynamicTexture(texture::toString, image));
                TEXTURE_SIZES.put(texture, new int[]{width, height});
            }
            return true;
        } catch (Throwable t) {
            mod.chloeprime.gunsmithlib.fcompat.GunsmithLibLog.warn("Failed to load variant icon {}", texture, t);
            return false;
        }
    }

    /** 把任意角度归一化到 [0, 2π) */
    private static double normalizeAngle(double angle) {
        var twoPi = Math.PI * 2;
        var a = angle % twoPi;
        return a < 0 ? a + twoPi : a;
    }

    private void drawSector(
            net.minecraft.client.gui.GuiGraphics graphics, int centerX, int centerY,
            double innerR, double outerR,
            double startRad, double endRad,
            int segments, int color
    ) {
        // PORT: 1.21.9+ 不能提交自定义顶点缓冲，这里用扫描线 + GuiGraphics#fill 画出扇环。
        var a = (color >> 24) & 0xFF;
        var r = (color >> 16) & 0xFF;
        var g = (color >> 8) & 0xFF;
        var b = color & 0xFF;
        var argb = (a << 24) | (r << 16) | (g << 8) | b;

        var start = normalizeAngle(startRad);
        var end = normalizeAngle(endRad);
        var twoPi = Math.PI * 2;
        var wraps = end < start;
        if (wraps) end += twoPi;

        var y0 = (int) Math.floor(centerY - outerR);
        var y1 = (int) Math.ceil(centerY + outerR);
        for (int y = y0; y < y1; y++) {
            double dy = y + 0.5 - centerY;
            if (Math.abs(dy) >= outerR) continue;
            double outerHalf = Math.sqrt(outerR * outerR - dy * dy);
            double innerHalf = Math.abs(dy) < innerR ? Math.sqrt(innerR * innerR - dy * dy) : 0;

            double[][] rings = {
                    {centerX - outerHalf, centerX - innerHalf},
                    {centerX + innerHalf, centerX + outerHalf}
            };
            for (var ring : rings) {
                double from = ring[0], to = ring[1];
                if (to <= from) continue;
                int runStart = Integer.MIN_VALUE;
                for (int x = (int) Math.floor(from); x <= (int) Math.ceil(to); x++) {
                    var angle = normalizeAngle(Math.atan2(dy, x + 0.5 - centerX));
                    if (wraps && angle < start) angle += twoPi;
                    boolean inside = angle >= start && angle <= end;
                    if (inside && runStart == Integer.MIN_VALUE) {
                        runStart = x;
                    } else if (!inside && runStart != Integer.MIN_VALUE) {
                        graphics.fill(runStart, y, x, y + 1, argb);
                        runStart = Integer.MIN_VALUE;
                    }
                }
                if (runStart != Integer.MIN_VALUE) {
                    graphics.fill(runStart, y, (int) Math.ceil(to), y + 1, argb);
                }
            }
        }
    }

    @Override
    public boolean keyReleased(net.minecraft.client.input.KeyEvent event) {
        if (SwitchPartOrAmmoTypeKey.KEY_MAPPING.matches(event)) {
            switchVariant();
            return true;
        }
        return super.keyReleased(event);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        if (SwitchPartOrAmmoTypeKey.KEY_MAPPING.matchesMouse(event)) {
            switchVariant();
            return true;
        }
        return super.mouseReleased(event);
    }

    private void switchVariant() {
        if (selectedIndex >= 0) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                    new mod.chloeprime.gunsmithlib.network.C2SSwitchVariant(
                            mod.chloeprime.gunsmithlib.network.C2SSwitchVariant.ACTION_SELECT_VARIANT, selectedIndex));
        }
        onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
