package mod.chloeprime.gunsmithlib.compat.aaap;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * AAA Particles 只有 Forge/NeoForge 版，Fabric 上这里全部是空实现。
 * 影响：命中/开火/弹道的 AAA 粒子效果不会出现。
 */
public final class AaaParticleProxyImpl {
    public static void addParticle(Level level, boolean force, Identifier id, Vec3 pos, Vec3 normal, float scale, Object data) {
    }

    public static void addParticleZP(Level level, boolean force, Identifier id, Vec3 pos, Vec3 normal, float scale, Object data) {
    }

    public static void bindParticleZP(Entity entity, Identifier id, Vec3 pos, float scale, Object data) {
    }

    private AaaParticleProxyImpl() {}
}
