package mod.chloeprime.gunsmithlib.fcompat;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public enum LogicalSide {
    CLIENT, SERVER;

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isServer() {
        return this == SERVER;
    }

    public static LogicalSide of(EnvType type) {
        return type == EnvType.CLIENT ? CLIENT : SERVER;
    }

    public static LogicalSide current() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? CLIENT : SERVER;
    }
}
