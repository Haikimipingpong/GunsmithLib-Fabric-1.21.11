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
public class IsAttachmentInstalledInDatabase implements LootItemCondition {
    private final Identifier attachmentId;

    public static final MapCodec<IsAttachmentInstalledInDatabase> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("attachment_id").forGetter(cond -> cond.attachmentId)
    ).apply(builder, IsAttachmentInstalledInDatabase::new));

    public IsAttachmentInstalledInDatabase(Identifier attachmentId) {
        this.attachmentId = attachmentId;
    }

    public static IsAttachmentInstalledInDatabase.Builder isAttachmentInstalledInDatabase(Identifier attachmentId) {
        return new Builder().attachmentId(attachmentId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private Identifier attachmentId;

        public Builder attachmentId(Identifier attachmentId) {
            this.attachmentId = attachmentId;
            return this;
        }

        @Nonnull
        public IsAttachmentInstalledInDatabase build() {
            return new IsAttachmentInstalledInDatabase(this.attachmentId);
        }
    }

    public Identifier getAttachmentId() {
        return this.attachmentId;
    }

    @Override
    public boolean test(LootContext context) {
        return TimelessAPI.getCommonAttachmentIndex(this.attachmentId).isPresent();
    }

    @Override
    public @Nonnull LootItemConditionType getType() {
        return GunLootFunctions.IS_ATTACHMENT_INSTALLED_IN_DATABASE.get();
    }
}
