package mod.chloeprime.gunsmithlib.fcompat;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class FMLLoader {
    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static EnvType getDist() {
        return FabricLoader.getInstance().getEnvironmentType();
    }

    private FMLLoader() {}
}
