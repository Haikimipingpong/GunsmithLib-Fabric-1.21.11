package mod.chloeprime.gunsmithlib.client.laser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tacz.guns.api.TimelessAPI;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.common.entity.MagicLaser;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import mod.chloeprime.gunsmithlib.fcompat.EntityRenderersEvent;
import mod.chloeprime.gunsmithlib.fcompat.SubscribeEvent;
import mod.chloeprime.gunsmithlib.fcompat.Dist;
import mod.chloeprime.gunsmithlib.fcompat.EventBusSubscriber;

import javax.annotation.ParametersAreNonnullByDefault;

@EventBusSubscriber(Dist.CLIENT)
public final class MagicLaserRenderer {
    public static final Identifier TEXTURE_LOCATION = GunsmithLib.loc("textures/entity/magic_laser.png");
    public static final boolean SUPPORTS_PRECISE_MUZZLE_POS = false;

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // PORT: 1.21.2 起实体渲染拆成 createRenderState/extractRenderState/submit 三段，
        // 旧的 EntityRenderer#render 与 EntityModel 都被重写，激光的模型渲染暂未恢复，
        // 先用 NoopRenderer 保证实体能存在且不崩（激光逻辑本身仍在跑）。
        event.registerEntityRenderer(GunsmithLib.EntityTypes.MAGIC_LASER.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
    }

    private MagicLaserRenderer() {}
}
