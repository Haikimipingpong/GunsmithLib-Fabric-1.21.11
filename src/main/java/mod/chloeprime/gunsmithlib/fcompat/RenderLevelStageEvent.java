package mod.chloeprime.gunsmithlib.fcompat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public class RenderLevelStageEvent extends Event {
    public enum Stage {
        AFTER_SKY, AFTER_SOLID_BLOCKS, AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS, AFTER_CUTOUT_BLOCKS,
        AFTER_ENTITIES, AFTER_BLOCK_ENTITIES, AFTER_TRANSLUCENT_BLOCKS, AFTER_TRIPWIRE_BLOCKS,
        AFTER_PARTICLES, AFTER_WEATHER, AFTER_LEVEL
    }

    private final Stage stage;
    private final PoseStack poseStack;
    private final Camera camera;
    private final float partialTick;
    private final Matrix4f projectionMatrix;

    public RenderLevelStageEvent(Stage stage, PoseStack poseStack, Camera camera, float partialTick, Matrix4f projectionMatrix) {
        this.stage = stage;
        this.poseStack = poseStack;
        this.camera = camera;
        this.partialTick = partialTick;
        this.projectionMatrix = projectionMatrix;
    }

    public Stage getStage() {
        return stage;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getModelViewMatrix() {
        return poseStack.last().pose();
    }
}
