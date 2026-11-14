package mod.chloeprime.gunsmithlib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.util.AttachmentDataUtils;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.client.gunpack_extension.CurrentAmmoDisplayType;
import mod.chloeprime.gunsmithlib.client.gunpack_extension.EnhancedGunDisplay;
import mod.chloeprime.gunsmithlib.client.gunpack_extension.GunsmithLibGunDisplayExtension;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.energy.EnergyWeaponBehavior;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.energy.EnergyWeaponData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import mod.chloeprime.gunsmithlib.fcompat.Capabilities;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Optional;
import java.util.function.IntSupplier;

public final class EnergyWeaponVisuals {
    public static final class HUD {
        public static boolean isEnabled() {
            var gun = Optional.ofNullable(Minecraft.getInstance().player)
                    .map(LivingEntity::getMainHandItem)
                    .orElse(ItemStack.EMPTY);
            return isEnabledFor(gun);
        }

        @SuppressWarnings("deprecation")
        public static boolean isEnabledFor(ItemStack gun) {
            var displayType = TimelessAPI.getGunDisplay(gun)
                    .map(instance -> ((EnhancedGunDisplay) instance))
                    .flatMap(EnhancedGunDisplay::gunsmith$getGunsmithLibExtension)
                    .map(GunsmithLibGunDisplayExtension::getCurrentAmmoDisplayType)
                    .orElse(CurrentAmmoDisplayType.DEFAULT);

            return switch (displayType) {
                case DEFAULT -> EnergyWeaponBehavior.isEnergyWeapon(gun);
                case BATTERY -> true;
                case COUNTER -> false;
            };
        }

        public static int modifyCurrentAmmoDisplay(GuiGraphics gui, float x, float y, int width, int height, IntSupplier oldBehavior) {
            var gun = Optional.ofNullable(Minecraft.getInstance().player)
                    .map(LivingEntity::getMainHandItem)
                    .orElse(ItemStack.EMPTY);

            var isEnergy = isEnabledFor(gun);

            if (isEnergy) {
                gui.pose().pushMatrix();
                gui.pose().translate((float) (32768), (float) (0));
            }

            var original = oldBehavior.getAsInt();

            if (isEnergy) {
                gui.pose().popMatrix();
                renderBattery(gui, gun, x, y, width, height);
            }

            return original;
        }

        public static final Identifier BATTERY_BG = GunsmithLib.loc("textures/gui/battery_hud_back.png");
        public static final Identifier BATTERY_FG = GunsmithLib.loc("textures/gui/battery_hud_front.png");

        @SuppressWarnings("unused")
        public static void renderBattery(GuiGraphics gui, ItemStack gunStack, float x, float y, int width, int height) {
            var gun = Gunsmith.getGunInfo(gunStack).orElse(null);
            if (gun == null) {
                return;
            }

            var curAmmo = gun.getTotalAmmo() + gun.getDummyAmmoAmount();
            var maxAmmo = AttachmentDataUtils.getAmmoCountWithAttachment(gun.gunStack(), gun.index().getGunData());
            var isEmpty = curAmmo == 0;
            var warnLowAmmo = !isEmpty && curAmmo < maxAmmo / 4;

            gui.pose().pushMatrix();
            {
                var w = 272;
                var h = 112;
                var texW = 272;
                var texH = 112;
                var scaleX = 1F / 16;
                var scaleY = 1F / 16;

                var batteryPercent = Mth.clamp((float) curAmmo / maxAmmo, 0, 1);
                var rx = (int) ((width - 70) / scaleX / 1.5F);
                var ry = (int) ((height - 43) / scaleY / 1.5F);
                var batteryW = (int) Mth.lerp(batteryPercent, 32, 224);

                gui.pose().scale((float) (scaleX), (float) (scaleY));

                // PORT: 1.21.9+ 不再支持全局 shader 着色，低电量染色改为省略（只保留图标本身）
                mod.chloeprime.gunsmithlib.client.util.GuiCompat.blit(gui, BATTERY_BG, rx, ry, w, h, 0.0F, 0.0F, 1.0F, 1.0F);
                mod.chloeprime.gunsmithlib.client.util.GuiCompat.blit(gui, BATTERY_FG, rx, ry, batteryW, h, 0.0F, 0.0F, (float) batteryW / texW, (float) h / texH);
            }
            gui.pose().popMatrix();
        }

        @SuppressWarnings("deprecation")
        public static void modifyBackupAmmoDisplay(ItemStack gun, MutableInt field) {
            if (!EnergyWeaponBehavior.isEnergyWeapon(gun)) {
                return;
            }

            var battery = mod.chloeprime.gunsmithlib.fcompat.CapabilityHelper.get(gun, Capabilities.EnergyStorage.ITEM);
            if (battery != null) {
                EnergyWeaponData.runtime(gun).ifPresent(info -> {
                    int shots = battery.getEnergyStored() / info.energy().energyPerShot();
                    field.setValue(shots);
                });
            }
        }
    }
}
