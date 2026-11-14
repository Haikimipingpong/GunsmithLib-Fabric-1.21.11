package mod.chloeprime.gunsmithlib.client.gunpack_extension.screen_shake;

import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.screen_shake.ScreenShakeEntryData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

/**
 * Self-contained replacement for the Ritchies Projectile Lib based handler.
 *
 * @since 7.0.0
 */
public class ScreenShakeHandler {
    @FunctionalInterface
    public interface MoveFunc {
        void move(double dx, double dy, double dz);
    }

    private @Nullable ScreenShakeEntryData data;
    private final Random random = new Random();

    // rotation (degrees) spring
    private Vec3 displacement = Vec3.ZERO;
    private Vec3 velocity = Vec3.ZERO;

    // camera translation spring
    private final Vector3d translateDisplacement = new Vector3d();
    private final Vector3d translateVelocity = new Vector3d();

    public void addEffect(double yaw, double pitch, double roll, double x, double y, double z) {
        this.velocity = this.velocity.add(jitter(yaw), jitter(pitch), jitter(roll));
        immediatelyAddTranslateEffect(x, y, z);
    }

    private double jitter(double magnitude) {
        return magnitude * (random.nextDouble() - 0.5);
    }

    public void applyTranslateEffects(MoveFunc moveFunc) {
        var partialTicks = deltaTicks();
        moveFunc.move(translateDisplacement.x + translateVelocity.x * partialTicks,
                translateDisplacement.y + translateVelocity.y * partialTicks,
                translateDisplacement.z + translateVelocity.z * partialTicks);
    }

    public double getYaw() {
        return displacement.x;
    }

    public double getPitch() {
        return displacement.y;
    }

    public double getRoll() {
        return displacement.z;
    }

    public void bindData(ScreenShakeEntryData data) {
        this.data = data;
    }

    public double getIntensity() {
        return Optional.ofNullable(data).map(ScreenShakeEntryData::getIntensity).orElse(1.0);
    }

    private double getRestitution() {
        return Optional.ofNullable(data).map(ScreenShakeEntryData::getRestitution).orElse(45.0);
    }

    private double getDrag() {
        return Optional.ofNullable(data).map(ScreenShakeEntryData::getDrag).orElse(8.0);
    }

    public void tick(Minecraft minecraft) {
        final int steps = 10;
        final double dt = 1.0 / steps;
        var restitution = Math.max(0.005, getRestitution());
        var drag = Math.max(0.005, getDrag());
        for (int i = 0; i < steps; i++) {
            var a = displacement.scale(-restitution).subtract(velocity.scale(drag));
            velocity = velocity.add(a.scale(dt));
            displacement = displacement.add(velocity.scale(dt));
            constraintRotation();

            translateVelocity.x += -translateDisplacement.x * restitution * dt;
            translateVelocity.y += -translateDisplacement.y * restitution * dt;
            translateVelocity.z += -translateDisplacement.z * restitution * dt;
            translateDisplacement.fma(dt, translateVelocity);
            constraintTranslation();
        }
        if (displacement.lengthSqr() < 1e-4 && velocity.lengthSqr() < 1e-4
                && translateDisplacement.lengthSquared() < 1e-4 && translateVelocity.lengthSquared() < 1e-4) {
            clearEffects();
        }
    }

    public void clearEffects() {
        this.data = null;
        this.displacement = Vec3.ZERO;
        this.velocity = Vec3.ZERO;
        this.translateDisplacement.zero();
        this.translateVelocity.zero();
    }

    private void constraintRotation() {
        var max = 45 * getIntensity();
        this.displacement = new Vec3(clamp(displacement.x, max), clamp(displacement.y, max), clamp(displacement.z, max));
    }

    private void constraintTranslation() {
        var max = 1.8 * getIntensity();
        translateDisplacement.x = clamp(translateDisplacement.x, max);
        translateDisplacement.y = clamp(translateDisplacement.y, max);
        translateDisplacement.z = clamp(translateDisplacement.z, max);
    }

    private static double clamp(double value, double max) {
        return Math.max(-max, Math.min(max, value));
    }

    private void immediatelyAddTranslateEffect(double xp, double yp, double zp) {
        if (Math.abs(xp) + Math.abs(yp) + Math.abs(zp) <= 3e-6) {
            return;
        }
        translateVelocity.x += jitter(xp);
        translateVelocity.y += jitter(yp);
        translateVelocity.z += jitter(zp);
    }

    private static float deltaTicks() {
        try {
            return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        } catch (Throwable ignored) {
            return 1.0F;
        }
    }
}
