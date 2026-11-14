package cn.chloeprime.commons;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ContextUtil {
    public static RegistryAccess getRegistryAccess() {
        var server = mod.chloeprime.gunsmithlib.fcompat.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.registryAccess();
        }
        return getRegistryAccessSafely();
    }

    public static RegistryAccess getRegistryAccessSafely() {
        var client = net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType()
                == net.fabricmc.api.EnvType.CLIENT ? clientRegistryAccess() : null;
        if (client != null) return client;
        return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }

    private static RegistryAccess clientRegistryAccess() {
        try {
            var level = net.minecraft.client.Minecraft.getInstance().level;
            return level != null ? level.registryAccess() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private ContextUtil() {}
}
