package mod.chloeprime.gunsmithlib.common.util;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.UUID;

/** UUIDs are stored as int arrays in 1.21.11 (CompoundTag#putUUID was removed). */
public final class NbtCompat {
    public static void putUUID(CompoundTag tag, String key, UUID uuid) {
        tag.putIntArray(key, toIntArray(uuid));
    }

    public static Optional<UUID> getUUID(CompoundTag tag, String key) {
        return tag.getIntArray(key).flatMap(NbtCompat::fromIntArray);
    }

    public static int[] toIntArray(UUID uuid) {
        return new int[]{
                (int) (uuid.getMostSignificantBits() >> 32),
                (int) uuid.getMostSignificantBits(),
                (int) (uuid.getLeastSignificantBits() >> 32),
                (int) uuid.getLeastSignificantBits()
        };
    }

    public static Optional<UUID> fromIntArray(int[] values) {
        if (values.length != 4) return Optional.empty();
        long msb = ((long) values[0] << 32) | (values[1] & 0xffffffffL);
        long lsb = ((long) values[2] << 32) | (values[3] & 0xffffffffL);
        return Optional.of(new UUID(msb, lsb));
    }

    private NbtCompat() {}
}
