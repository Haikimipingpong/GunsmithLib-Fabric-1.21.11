package mod.chloeprime.gunsmithlib.mixin.common.tacz;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.util.ExplodeUtil;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.damage_source_control.DamageSourceControlSystem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * PORT: 1.21.11 的 TaCZ（Fabric 移植版）重写了 {@code ExplodeUtil.createExplosion}，
 * 它不再构造 {@code com.tacz.guns.util.block.ProjectileExplosion}，
 * 而是走 {@code ServerLevel#explode(Entity, double, double, double, float, boolean, ExplosionInteraction)}
 * 并自己遍历 AABB 内的实体施加伤害。
 * <p>
 * 因此原来这里的两处针对爆炸粒子的注入点已经不存在了（功能降级为 no-op）：
 * <ul>
 *     <li>{@code @Inject} 在 {@code ServerLevel#players()} 调用点（createExplosion 里已没有该调用）</li>
 *     <li>{@code @WrapOperation} on {@code lambda$createExplosion$1}（该方法已被删除）</li>
 * </ul>
 * 影响：枪包里配置了自定义命中粒子（HitParticleSystem）的枪械，其爆炸时原版爆炸粒子不再被抑制。
 * 若要恢复，需要在 {@code net.minecraft.world.level.ServerExplosion} 发送
 * {@code ClientboundExplodePacket} 的位置新增一个单独的 Mixin（本次改动不允许新建文件）。
 * 另外 1.21.11 的 {@code ClientboundExplodePacket} 已变为 record，不再携带方块列表与
 * {@code Explosion.BlockInteraction}，旧的“无粒子爆炸包”方案本身也不再可行。
 * <p>
 * 伤害类型控制仍然可以等价实现：现在爆炸伤害的 DamageSource 由 createExplosion 内的
 * {@code DamageSources#explosion(Entity, Entity)} 生成，改为包裹这个调用即可。
 *
 * @since 6.4.0
 */
@Mixin(value = ExplodeUtil.class, remap = false)
public class MixinExplodeUtil {

    // 修改爆炸伤害类型

    @WrapOperation(
            method = "createExplosion",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/damagesource/DamageSources;explosion(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private static DamageSource setDamageSource(DamageSources damageSources, Entity exploder, Entity owner, Operation<DamageSource> original) {
        return DamageSourceControlSystem.modifyExplosionDamageSource(
                owner, exploder, original.call(damageSources, exploder, owner));
    }
}
