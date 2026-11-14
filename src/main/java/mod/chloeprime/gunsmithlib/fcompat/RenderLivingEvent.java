package mod.chloeprime.gunsmithlib.fcompat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public class RenderLivingEvent<T extends LivingEntity> extends Event {
    private final T entity;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;

    public RenderLivingEvent(T entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.entity = entity;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
    }

    public T getEntity() {
        return entity;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return bufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public static class Post<T extends LivingEntity> extends RenderLivingEvent<T> {
        public Post(T entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            super(entity, partialTick, poseStack, bufferSource, packedLight);
        }
    }
}
