package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class OnDatapackSyncEvent extends Event {
    private final MinecraftServer server;
    private final ServerPlayer player;

    public OnDatapackSyncEvent(MinecraftServer server, ServerPlayer player) {
        this.server = server;
        this.player = player;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public PlayerList getPlayerList() {
        return server.getPlayerList();
    }

    public java.util.List<ServerPlayer> getRelevantPlayers() {
        return player != null ? java.util.List.of(player) : getPlayerList().getPlayers();
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
