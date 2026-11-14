package cn.chloeprime.commons.rpc;

import net.minecraft.server.level.ServerPlayer;

public final class RPCContext {
    private static ServerPlayer senderPlayer;
    private static boolean calledThroughRpc;

    public static boolean isCalledThroughRPC() {
        return calledThroughRpc;
    }

    public static ServerPlayer getSenderPlayer() {
        return senderPlayer;
    }

    /** 本地直调时把"调用者"设置进来，让上游代码的 RPCContext 分支能拿到玩家。 */
    public static void withSender(ServerPlayer player, Runnable action) {
        var prev = senderPlayer;
        var prevFlag = calledThroughRpc;
        senderPlayer = player;
        calledThroughRpc = true;
        try {
            action.run();
        } finally {
            senderPlayer = prev;
            calledThroughRpc = prevFlag;
        }
    }

    private RPCContext() {}
}
