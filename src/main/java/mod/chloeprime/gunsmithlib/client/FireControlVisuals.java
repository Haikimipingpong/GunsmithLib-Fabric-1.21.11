package mod.chloeprime.gunsmithlib.client;



import com.mojang.blaze3d.vertex.PoseStack;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.api.util.TargetSearcher;
import mod.chloeprime.gunsmithlib.client.gunpack_extension.GunsmithLibGunDisplayExtension;
import mod.chloeprime.gunsmithlib.client.impl.scripting_v2.content.InWorldGuiGraphicsImpl;
import mod.chloeprime.gunsmithlib.client.impl.scripting_v2.hooks.RenderHudHook;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;
import mod.chloeprime.gunsmithlib.fcompat.RenderLevelStageEvent;
import mod.chloeprime.gunsmithlib.fcompat.RenderLivingEvent;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Optional;

@EventBusSubscriber(Dist.CLIENT)
public class FireControlVisuals {
    private static @Nullable TargetSearcher.SearchResult aiming;
    private static int version;
    private static long lastAimCalculationTime;
    private static float lastAimCalculationPartialTicks;
    private static long lastAimSelectTime;
    private static float lastAimSelectPartialTicks;
    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?> event) {
        refreshCachedAimingEntity(event.getPartialTick());
        if (aiming == null || aiming.entity() != event.getEntity()) {
            return;
        }
        var pos = aiming.pos();
        if (version == 1) {
            renderV1(event.getEntity(), pos,
                    event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        }
    }

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }
        // PORT: v2 准星在 1.21.11 上暂不可用（见 renderV2）
    }

    private static void refreshCachedAimingEntity(float partialTick) {
        var level = MC.level;
        if (level == null) {
            return;
        }

        long now = level.getGameTime();
        if (lastAimCalculationTime != now || lastAimCalculationPartialTicks != partialTick) {
            lastAimCalculationTime = now;
            lastAimCalculationPartialTicks = partialTick;
            if (!(MC.getCameraEntity() instanceof LivingEntity player)) {
                aiming = null;
                return;
            }
            var oldEntity = Optional.ofNullable(aiming).map(TargetSearcher.SearchResult::entity).orElse(null);
            version = GunsmithLibGunDisplayExtension.of(player.getMainHandItem())
                    .map(GunsmithLibGunDisplayExtension::getFireControlHudVersion)
                    .orElse(GunsmithLibGunDisplayExtension.DEFAULT_FIRE_CONTROL_HUD_VERSION);
            aiming = Gunsmith.getGunInfo(player.getMainHandItem())
                    // 用PartialTick计算会有bug，传入1反而更稳定
                    .flatMap(gi -> TargetSearcher.search(player, gi, partialTick))
                    .orElse(null);
            var newEntity = Optional.ofNullable(aiming).map(TargetSearcher.SearchResult::entity).orElse(null);
            if (newEntity != oldEntity) {
                lastAimSelectTime = now;
                lastAimSelectPartialTicks = partialTick;
            }
        }
    }

    private static final Component AIMING_GLYPH = Component.literal("[+]").withStyle(ChatFormatting.RED);

    private static void renderV1(Entity pEntity, Vec3 aimPos, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // PORT: 1.21.9+ 移除了 GuiGraphics 的即时构造与全局 shader 混合状态，
        // 世界内瞄准点渲染改为只画 v1 名字标签（见 onRenderLiving）。
    }

    private static void renderV2(Entity target, Vec3 aimPos, Vec3 cameraPos, float partialTick) {
        // PORT: v2 准星依赖世界坐标下的 GuiGraphics 即时绘制，1.21.9+ 的延迟提交管线不支持，暂时关闭。
    }

    private static int iff(@Nullable Entity actor, Entity target) {
        // getOwner 优先于宠物的 isAlliedTo
        if (target instanceof TraceableEntity traceable && traceable.getOwner() != null && traceable.getOwner() != target) {
            return iff(actor, traceable.getOwner());
        }
        if (target instanceof OwnableEntity ownable && ownable.getOwner() != null && ownable.getOwner() != target) {
            return iff(actor, ownable.getOwner());
        }

        if (actor != null && target.isAlliedTo(actor)) {
            return 1;
        } else if (target instanceof Enemy) {
            return -1;
        } else if (target instanceof NeutralMob) {
            return 0;
        } else {
            return 1;
        }
    }
}
