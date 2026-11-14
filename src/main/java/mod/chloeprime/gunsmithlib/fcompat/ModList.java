package mod.chloeprime.gunsmithlib.fcompat;

import net.fabricmc.loader.api.FabricLoader;

public final class ModList {
    public static ModList get() {
        return INSTANCE;
    }

    public static final ModList INSTANCE = new ModList();

    public boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
