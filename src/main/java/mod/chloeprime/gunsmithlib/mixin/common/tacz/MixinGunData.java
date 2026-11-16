package mod.chloeprime.gunsmithlib.mixin.common.tacz;

import com.google.gson.annotations.SerializedName;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import mod.chloeprime.gunsmithlib.common.MiscAttributeAdapter;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.EnhancedGunData;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.GunsmithLibGunDataExtension;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.arcana_check.ArcanaExtras;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.fire_control.OldFireControlData;
import mod.chloeprime.gunsmithlib.common.util.GunpackProperty;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(value = GunData.class, remap = false)
public class MixinGunData implements EnhancedGunData {
    @ModifyExpressionValue(
            method = "getShootInterval",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;")
    )
    private Object adjustRpmByAttribute(Object baseRpm, LivingEntity shooter, FireMode fireMode) {
        // 只有玩家确实处于"枪械属性同步"状态时才用属性值覆盖，
        // 否则属性默认值（300）会把所有枪的射速一律压低。
        if (shooter instanceof mod.chloeprime.gunsmithlib.common.internal.GunAttributeSyncState syncState
                && syncState.gunsmith$isInGunMode()) {
            var rpm = MiscAttributeAdapter.rpm(shooter);
            if (rpm > 0) {
                return (int) rpm;
            }
        }
        return baseRpm;
    }

    @GunpackProperty
    @SerializedName("gunsmithlib_extension")
    private @Unique @Nullable GunsmithLibGunDataExtension gunsmith$extension;

    @GunpackProperty
    @SerializedName("extras")
    private @Unique @Nullable ArcanaExtras gunsmith$arcana_extras;

    @Deprecated
    @GunpackProperty
    @SerializedName("tacz_fire_control_extension:fire_control_system")
    private @Unique @Nullable OldFireControlData gunsmith$oldFireControlData;

    @Override
    public Optional<GunsmithLibGunDataExtension> gunsmith$getGunsmithLibExtension() {
        return Optional.ofNullable(gunsmith$extension);
    }

    @Override
    public Optional<ArcanaExtras> gunsmith$getArcanaExtras() {
        return Optional.ofNullable(gunsmith$arcana_extras);
    }

    @Override
    @Deprecated
    public Optional<OldFireControlData> gunsmith$getOldFireControlSystemData() {
        return Optional.ofNullable(gunsmith$oldFireControlData);
    }
}
