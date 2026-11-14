package mod.chloeprime.gunsmithlib.proxies;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.chloeprime.gunsmithlib.mixin.common.minecraft.LevelAccessor;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.AddReloadListenerEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(Dist.CLIENT)
class ClientProxyImpl {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final PoseStack POSE = new PoseStack();
    private static RegistryAccess fallbackRegistryAccess;

    static float getPartialTick() {
        return MC.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    public static Optional<RegistryAccess> getRegistryAccess() {
        var result = Optional.ofNullable(MC.level).map(Level::registryAccess).orElse(fallbackRegistryAccess);
        return Optional.ofNullable(result);
    }

    @SubscribeEvent
    private static void updateFallbackRegistryAccessOnResourceReload(AddReloadListenerEvent event) {
        fallbackRegistryAccess = Optional.ofNullable(MC.level).map(Level::registryAccess).orElse(fallbackRegistryAccess);
    }

    static Optional<Level> clientLevel() {
        return Optional.ofNullable(MC.level);
    }

    static Vec3 bobCompensation(Vec3 original) {
        if (MC.options.getCameraType() != CameraType.FIRST_PERSON || !MC.options.bobView().get()) {
            return original;
        }
        var player = MC.player;
        if (player == null) {
            return original;
        }

        var pPartialTicks = getPartialTick();
        // PORT: 1.21.11 把 walkDist/oBob/bob 合并进了 WalkAnimationState
        float f1 = -player.walkAnimation.position(pPartialTicks) * (float) Math.PI;
        float f2 = player.walkAnimation.speed(pPartialTicks) * 0.5F;
        Matrix4f pose;
        POSE.pushPose();
        {
            POSE.translate(Mth.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(Mth.cos(f1 * (float) Math.PI) * f2), 0.0F);
            POSE.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f1 * (float) Math.PI) * f2 * 3.0F));
            POSE.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));
            pose = POSE.last().pose();
        }
        POSE.popPose();

        var affineVec = new Vector4f((float) original.x, (float) original.y, (float) original.z, 1);
        var transformed = pose.transform(affineVec);
        if (transformed.w == 0) {
            return original;
        }
        transformed.div(transformed.w);
        return new Vec3(transformed.x, transformed.y, transformed.z);
    }

    static @Nullable Entity getEntityByUuid(Level level, UUID uuid) {
        return ((LevelAccessor) level).invokeGetEntities().get(uuid);
    }

    static void receiveNoParticleExplodePacket(Vec3 pos, float power, BlockPos[] toBlow, Vec3 knockback, Explosion.BlockInteraction blockInteraction) {
        var level = MC.level;
        var player = MC.player;
        if (level == null || player == null) {
            return;
        }
        // PORT: 1.21.11 的 Explosion 变成抽象类，客户端不能再直接构造并 finalizeExplosion；
        // 这里只保留击退和音效，爆炸粒子交给服务端广播的原版包。
        level.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, 1.0F);
        player.setDeltaMovement(player.getDeltaMovement().add(knockback.x(), knockback.y(), knockback.z()));
    }

    public static long getGameTime() {
        return Optional.ofNullable(MC.level)
                .map(Level::getGameTime)
                .orElse(0L);
    }

    public static float getPartialTicks() {
        return MC.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }
}
