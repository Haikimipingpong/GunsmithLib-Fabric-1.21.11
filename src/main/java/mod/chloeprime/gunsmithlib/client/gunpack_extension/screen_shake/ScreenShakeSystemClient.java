package mod.chloeprime.gunsmithlib.client.gunpack_extension.screen_shake;

import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.screen_shake.ScreenShakeData;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.screen_shake.ScreenShakeEntryData;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.screen_shake.ScreenShakeType;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import mod.chloeprime.gunsmithlib.mixin.client.minecraft.CameraAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.ViewportEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * @since 7.0.0
 */
@EventBusSubscriber(Dist.CLIENT)
public final class ScreenShakeSystemClient {
    public static void applyScreenShakeAt(Vec3 pos, LivingEntity shooter, Identifier gunId, ScreenShakeType.Field field) {
        var cameraPos = Optional.ofNullable(Minecraft.getInstance().getCameraEntity())
                .or(() -> Optional.ofNullable(Minecraft.getInstance().player))
                .map(Entity::getEyePosition)
                .orElse(null);
        if (cameraPos == null) {
            return;
        }
        var config = GsHelper.gunInfoFromShooter(shooter, gunId)
                .flatMap(ScreenShakeData::of)
                .flatMap(field)
                .orElse(null);
        if (config == null) {
            return;
        }
        var handler = ScreenShakeHandlers.next();
        var power = (float) (getPower(config, pos.distanceToSqr(cameraPos)) * config.getIntensity());
        handler.bindData(config);
        handler.addEffect(config.getYawMagnitude() * power, config.getPitchMagnitude() * power, config.getRollMagnitude() * power,
                config.getTranslationMagnitudeX() * power, config.getTranslationMagnitudeY() * power, config.getTranslationMagnitudeZ() * power);
    }

    private static double getPower(ScreenShakeEntryData data, double distanceSqr) {
        var range = data.getRange();
        if (range == 0) {
            return 0;
        }
        var f = Math.max(1 - distanceSqr / (range * range), 0);
        return Math.min(45, f * data.getBasePower());
    }

    @SubscribeEvent
    public static void moveCamera(ViewportEvent.ComputeCameraAngles event) {
        var camera = (CameraAccessor) event.getCamera();
        ScreenShakeHandlers.all().forEach(handler -> {
            handler.applyTranslateEffects((x, y, z) -> camera.invokeMove((float) z, (float) y, (float) x));
            event.setYaw(event.getYaw() + handler.getYaw());
            event.setPitch(event.getPitch() + handler.getPitch());
            event.setRoll(event.getRoll() + handler.getRoll());
        });
    }

    @ApiStatus.Internal
    public static void init() {
        ScreenShakeHandlers.init();
    }

    private ScreenShakeSystemClient() {
    }
}
