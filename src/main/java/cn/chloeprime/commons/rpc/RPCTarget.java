package cn.chloeprime.commons.rpc;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Fabric port: RPC is executed locally instead of over the wire.
 * In singleplayer (integrated server) this is equivalent; on a real server only
 * local effects apply.
 */
public abstract class RPCTarget {
    public static RPCTarget toServer() {
        return new RPCTarget() {
            @Override
            public Iterable<ServerPlayer> getTarget() {
                var server = mod.chloeprime.gunsmithlib.fcompat.ServerLifecycleHooks.getCurrentServer();
                return server != null ? server.getPlayerList().getPlayers() : java.util.List.of();
            }

            @Override
            public void send(CustomPacketPayload payload) {
                // 本地直调模式下不发包
            }
        };
    }

    public static RPCTarget to(ServerPlayer player) {
        return new RPCTarget() {
            @Override
            public boolean isClientBound() {
                return true;
            }

            @Override
            public Iterable<ServerPlayer> getTarget() {
                return java.util.List.of(player);
            }

            @Override
            public void send(CustomPacketPayload payload) {
            }
        };
    }

    public static RPCTarget near(Entity entity) {
        return to(null);
    }

    public static RPCTarget reply() {
        return toServer();
    }

    public abstract Iterable<ServerPlayer> getTarget();

    /** true 表示这次调用原本是"服务端 → 客户端"，应放到客户端线程执行。 */
    public boolean isClientBound() {
        return false;
    }

    public abstract void send(CustomPacketPayload payload);

    public boolean isServer() {
        return true;
    }
}
