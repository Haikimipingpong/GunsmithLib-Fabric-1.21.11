package mod.chloeprime.gunsmithlib.client.impl.scripting_v2.content;

import mod.chloeprime.gunsmithlib.api.client.scripting_v2.content.InWorldGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;

/**
 * 世界坐标 GUI。
 * <p>
 * PORT: 1.21.9+ 的 GUI 绘制改成了延迟提交的 RenderPipeline，绘制指令不再绑定当时的矩阵，
 * 因此「在世界坐标锚点处压一次矩阵再绘制」的旧实现不再成立。
 * 这里退化为屏幕坐标绘制（锚点被忽略），脚本画出来的内容会固定在 HUD 原点上。
 *
 * @since 7.0.0
 */
public class InWorldGuiGraphicsImpl extends GuiGraphicsAPIImpl implements InWorldGuiGraphics {
    public static InWorldGuiGraphicsImpl at(GuiGraphics impl, Vec3 worldPos, float scale, boolean distanceScaled) {
        return new InWorldGuiGraphicsImpl(impl);
    }

    private InWorldGuiGraphicsImpl(GuiGraphics impl) {
        super(impl);
    }

    @Override
    public double distance() {
        return 0;
    }

    @Override
    public boolean visible() {
        return true;
    }
}
