package mod.chloeprime.gunsmithlib.client.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

/**
 * Bridges the pre-1.21.9 immediate-mode GUI code to the new Matrix3x2f / RenderPipeline based API.
 */
public final class GuiCompat {
    public static void push(GuiGraphics graphics) {
        graphics.pose().pushMatrix();
    }

    public static void pop(GuiGraphics graphics) {
        graphics.pose().popMatrix();
    }

    public static void translate(GuiGraphics graphics, float x, float y) {
        graphics.pose().translate(x, y);
    }

    public static void scale(GuiGraphics graphics, float sx, float sy) {
        graphics.pose().scale(sx, sy);
    }

    /**
     * Draws a texture region with normalised (0..1) UVs, like the old
     * {@code GuiGraphics#blit(Identifier, x, y, w, h, u0, v0, u1, v1)}.
     */
    public static void blit(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height,
                            float u0, float v0, float u1, float v1) {
        graphics.blit(texture, x, y, width, height, u0, v0, u1, v1);
    }

    public static void blit(GuiGraphics graphics, Identifier texture, int x, int y, int width, int height) {
        blit(graphics, texture, x, y, width, height, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    private GuiCompat() {}
}
