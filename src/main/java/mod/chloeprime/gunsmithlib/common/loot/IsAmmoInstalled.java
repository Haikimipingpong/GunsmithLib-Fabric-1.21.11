package mod.chloeprime.gunsmithlib.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.TimelessAPI;
import mod.chloeprime.gunsmithlib.api.common.GunLootFunctions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IsAmmoInstalled implements LootItemCondition {
    private final Identifier ammoId;

    public static final MapCodec<IsAmmoInstalled> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("ammo_id").forGetter(cond -> cond.ammoId)
    ).apply(builder, IsAmmoInstalled::new));

    public IsAmmoInstalled(Identifier ammoId) {
        this.ammoId = ammoId;
    }

    public static IsAmmoInstalled.Builder isAmmoInstalled(Identifier ammoId) {
        return new Builder().ammoId(ammoId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private Identifier ammoId;

        public Builder ammoId(Identifier ammoId) {
            this.ammoId = ammoId;
            return this;
        }

        @Nonnull
        public IsAmmoInstalled build() {
            return new IsAmmoInstalled(this.ammoId);
        }
    }

    public Identifier getAmmoId() {
        return this.ammoId;
    }

    @Override
    public boolean test(LootContext context) {
        return TimelessAPI.getCommonAmmoIndex(this.ammoId).isPresent();
    }

    @Override
    public @Nonnull LootItemConditionType getType() {
        return GunLootFunctions.IS_AMMO_INSTALLED.get();
    }
}
