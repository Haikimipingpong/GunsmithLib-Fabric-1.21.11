package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleHooks {
    private static volatile MinecraftServer currentServer;

    public static void setCurrentServer(MinecraftServer server) {
        currentServer = server;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    private ServerLifecycleHooks() {}
}
