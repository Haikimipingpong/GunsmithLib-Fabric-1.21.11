package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/** Data attachments do not exist on Fabric; GunsmithLib only registers empty attachment types. */
public final class NeoForgeRegistries {
    public static final class Keys {
        private Keys() {}
    }

    private NeoForgeRegistries() {}
}
