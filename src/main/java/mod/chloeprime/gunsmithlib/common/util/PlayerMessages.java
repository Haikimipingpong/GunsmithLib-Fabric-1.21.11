package mod.chloeprime.gunsmithlib.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class PlayerMessages {
    public static void send(Player player, Component message) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(message);
        } else if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
            localPlayer.displayClientMessage(message, false);
        }
    }

    private PlayerMessages() {}
}
