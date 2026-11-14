package cn.chloeprime.commons.rpg;

import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.Collection;

/**
 * NeoForge allowed mutating damage-type tags on a DamageSource instance.
 * Fabric's DamageSource is immutable, so this is a no-op on the Fabric port.
 */
public final class DamageSources {
    public static void injectIs(DamageSource source, TagKey<DamageType> tag) {
    }

    public static void injectIs(DamageSource source, Collection<TagKey<DamageType>> tags) {
    }

    public static void injectIsNot(DamageSource source, TagKey<DamageType> tag) {
    }

    public static void injectIsNot(DamageSource source, Collection<TagKey<DamageType>> tags) {
    }

    private DamageSources() {}
}
