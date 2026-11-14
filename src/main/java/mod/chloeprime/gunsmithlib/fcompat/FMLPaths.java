package mod.chloeprime.gunsmithlib.fcompat;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class FMLPaths {
    public static final Path CONFIGDIR = FabricLoader.getInstance().getConfigDir();
    public static final Path GAMEDIR = FabricLoader.getInstance().getGameDir();

    private FMLPaths() {}
}
