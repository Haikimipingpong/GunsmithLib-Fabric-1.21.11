package mod.chloeprime.gunsmithlib.mixin.common.tacz;

import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.gunsmithlib.common.internal.EnhancedKineticBullet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityKineticBullet.class, remap = false)
public interface EntityKineticBulletAccessor extends EnhancedKineticBullet {
    @Accessor int getLife();
    @Accessor Identifier getGunId();
    @Accessor void setArmorIgnore(float value);
    @Accessor void setExplosionDelayCount(int value);

    @Accessor boolean getExplosion();
    @Accessor float getExplosionRadius();

    @Accessor float getGravity();
    @Accessor void setGravity(float value);
}
