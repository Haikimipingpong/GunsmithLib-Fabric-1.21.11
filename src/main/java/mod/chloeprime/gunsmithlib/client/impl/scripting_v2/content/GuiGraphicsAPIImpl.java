package mod.chloeprime.gunsmithlib.client.impl.scripting_v2.content;

import mod.chloeprime.gunsmithlib.api.client.scripting_v2.content.GuiGraphicsAPI;
import mod.chloeprime.gunsmithlib.client.util.GuiCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;

/**
 * {@link GuiGraphicsAPI} 的实现，直接委托给原版的 {@link GuiGraphics}。
 *
 * @since 7.0.0
 */
public class GuiGraphicsAPIImpl extends AbstractGuiGraphicsAPIImpl {
    private final GuiGraphics impl;
    @SuppressWarnings("unused")
    private Color currentColor = Color.WHITE;

    /**
     * @param impl 被封装的原版 {@link GuiGraphics}
     */
    public GuiGraphicsAPIImpl(GuiGraphics impl) {
        this.impl = Objects.requireNonNull(impl, "impl");
    }

    @Override
    public int width() {
        return impl.guiWidth();
    }

    @Override
    public int height() {
        return impl.guiHeight();
    }

    @Override
    public void push_pose() {
        impl.pose().pushMatrix();
    }

    @Override
    public void pop_pose() {
        impl.pose().popMatrix();
    }

    @Override
    public void translate(float x, float y, float z) {
        // PORT: GUI 的变换矩阵已经是 2D 的 Matrix3x2fStack，不再有 z 轴平移；z 参数被忽略。
        impl.pose().translate(x, y);
    }

    @Override
    public void rotate(String axis, float degrees) {
        // PORT: GUI 的变换矩阵已经是 2D 的 Matrix3x2fStack，只能绕 z 轴旋转，
        // 绕 x/y 轴旋转（3D 倾斜）无法等价实现，这里直接忽略。
        var radians = (float) Math.toRadians(degrees);
        switch (axis.toLowerCase(Locale.ROOT)) {
            case "z" -> impl.pose().rotate(radians);
            case "x", "y" -> {
            }
            default -> throw new IllegalArgumentException("Invalid rotation axis: " + axis + " (expected x, y or z)");
        }
    }

    @Override
    public void scale(float x, float y, float z) {
        // PORT: 2D 矩阵只有 x/y 方向缩放，z 参数被忽略。
        impl.pose().scale(x, y);
    }

    @Override
    public void fill(int min_x, int min_y, int max_x, int max_y, Color color) {
        impl.fill(min_x, min_y, max_x, max_y, color.getRGB());
    }

    @Override
    public void fill_gradient(int x1, int y1, int x2, int y2, Color color_from, Color color_to) {
        impl.fillGradient(x1, y1, x2, y2, color_from.getRGB(), color_to.getRGB());
    }

    @Override
    public void h_line(int min_x, int max_x, int y, Color color) {
        impl.hLine(min_x, max_x, y, color.getRGB());
    }

    @Override
    public void v_line(int x, int min_y, int max_y, Color color) {
        impl.vLine(x, min_y, max_y, color.getRGB());
    }

    @Override
    public void render_outline(int x, int y, int width, int height, Color color) {
        impl.renderOutline(x, y, width, height, color.getRGB());
    }

    @Override
    public void set_color(Color color) {
        // PORT: 1.21.9+ 移除了 GuiGraphics#setColor（全局着色），贴图颜色改由 RenderPipeline 决定，
        // 没有运行期可改的全局颜色状态。这里只记住颜色，绘制结果不再被染色。
        this.currentColor = color;
    }

    @Override
    public int draw_string(String text, float x, float y, Color color, boolean drop_shadow) {
        // PORT: drawString 现在返回 void，这里改用 Font#width 复现原来的「返回绘制宽度」语义。
        var component = parseText(text);
        impl.drawString(font(), component, Mth.floor(x), Mth.floor(y), color.getRGB(), drop_shadow);
        return font().width(component);
    }

    @Override
    public void draw_string_centered(String text, int x, int y, Color color) {
        impl.drawCenteredString(font(), parseText(text), x, y, color.getRGB());
    }

    @Override
    public void draw_string_wrapped(String text, int x, int y, int line_width, Color color) {
        impl.drawWordWrap(font(), parseText(text), x, y, line_width, color.getRGB());
    }

    @Override
    public void blit(String texture, int x, int y, int u, int v, int width, int height) {
        // 旧的 blit(Identifier, x, y, u, v, w, h, texW, texH) 重载已不存在，
        // 改成「像素 UV → 0..1 归一化 UV」的 blit。
        blitRegion1211(parseTexture(texture), x, y, width, height, u, v, width, height,
                DEFAULT_TEXTURE_SIZE, DEFAULT_TEXTURE_SIZE);
    }

    @Override
    public void blit(String texture, int x, int y, int width, int height, int z, float u, float v, float u_width, float v_height) {
        // 这个重载的 (u, v, u_width, v_height) 本来就是 0..1 归一化 UV，
        // 直接用归一化 UV 的 blit 即可；z（blitOffset）在新管线里没有对应概念，忽略。
        GuiCompat.blit(impl, parseTexture(texture), x, y, width, height, u, v, u + u_width, v + v_height);
    }

    @Override
    protected void blitRegion1211(
            Identifier texture, int x, int y, int width, int height,
            int u, int v, int u_width, int v_height,
            int texture_width, int texture_height
    ) {
        // 旧的 10 参 blit（像素 UV + 贴图尺寸）已不存在，换算成 0..1 归一化 UV 再画。
        var texW = texture_width <= 0 ? DEFAULT_TEXTURE_SIZE : (float) texture_width;
        var texH = texture_height <= 0 ? DEFAULT_TEXTURE_SIZE : (float) texture_height;
        GuiCompat.blit(impl, texture, x, y, width, height,
                u / texW, v / texH, (u + u_width) / texW, (v + v_height) / texH);
    }

    @Override
    public void render_item(ItemStack stack, int x, int y) {
        impl.renderItem(stack, x, y);
    }

    @Override
    public void render_item_decorations(ItemStack stack, int x, int y) {
        impl.renderItemDecorations(font(), stack, x, y);
    }

    @Override
    public void enable_scissor(int min_x, int min_y, int max_x, int max_y) {
        impl.enableScissor(min_x, min_y, max_x, max_y);
    }

    @Override
    public void disable_scissor() {
        impl.disableScissor();
    }

    private static Font font() {
        return Objects.requireNonNull(Minecraft.getInstance().font, "font");
    }
}
