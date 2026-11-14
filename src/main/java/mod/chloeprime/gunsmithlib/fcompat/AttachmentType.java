package mod.chloeprime.gunsmithlib.fcompat;

import com.mojang.serialization.Codec;

import java.util.function.Supplier;

/** Placeholder for NeoForge data attachments (unused on Fabric builds). */
public final class AttachmentType<T> {
    public static <T> Builder<T> builder(Supplier<AttachmentType<T>> supplier) {
        return new Builder<>();
    }

    public static final class Builder<T> {
        public Builder<T> serialize(Codec<T> codec) {
            return this;
        }

        public Builder<T> copyOnDeath() {
            return this;
        }

        public AttachmentType<T> build() {
            return new AttachmentType<>();
        }
    }
}
