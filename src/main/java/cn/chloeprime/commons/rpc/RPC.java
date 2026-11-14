package cn.chloeprime.commons.rpc;

/**
 * Fabric port of kuroutils2 RPC: calls are executed locally.
 * Client-to-server calls run on the integrated server thread when available.
 */
public final class RPC {
    public static void call(RPCTarget target, Fun0 fun) {
        run(target, fun::invoke);
    }

    public static <T1> void call(RPCTarget target, Fun1<T1> fun, T1 a) {
        run(target, () -> fun.invoke(a));
    }

    public static <T1, T2> void call(RPCTarget target, Fun2<T1, T2> fun, T1 a, T2 b) {
        run(target, () -> fun.invoke(a, b));
    }

    public static <T1, T2, T3> void call(RPCTarget target, Fun3<T1, T2, T3> fun, T1 a, T2 b, T3 c) {
        run(target, () -> fun.invoke(a, b, c));
    }

    public static <T1, T2, T3, T4> void call(RPCTarget target, Fun4<T1, T2, T3, T4> fun, T1 a, T2 b, T3 c, T4 d) {
        run(target, () -> fun.invoke(a, b, c, d));
    }

    public static <T1, T2, T3, T4, T5> void call(RPCTarget target, Fun5<T1, T2, T3, T4, T5> fun, T1 a, T2 b, T3 c, T4 d, T5 e) {
        run(target, () -> fun.invoke(a, b, c, d, e));
    }

    public static <T1, T2, T3, T4, T5, T6> void call(RPCTarget target, Fun6<T1, T2, T3, T4, T5, T6> fun, T1 a, T2 b, T3 c, T4 d, T5 e, T6 f) {
        run(target, () -> fun.invoke(a, b, c, d, e, f));
    }

    public static <T1, T2, T3, T4, T5, T6, T7> void call(RPCTarget target, Fun7<T1, T2, T3, T4, T5, T6, T7> fun, T1 a, T2 b, T3 c, T4 d, T5 e, T6 f, T7 g) {
        run(target, () -> fun.invoke(a, b, c, d, e, f, g));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> void call(RPCTarget target, Fun8<T1, T2, T3, T4, T5, T6, T7, T8> fun, T1 a, T2 b, T3 c, T4 d, T5 e, T6 f, T7 g, T8 h) {
        run(target, () -> fun.invoke(a, b, c, d, e, f, g, h));
    }

    private static void run(RPCTarget target, Runnable action) {
        // 服务端 → 客户端的调用：单机时直接丢到客户端线程执行（集成服务端同进程）
        if (target != null && target.isClientBound() && isClientSide()) {
            net.minecraft.client.Minecraft.getInstance().execute(() -> guarded(action));
            return;
        }
        var server = mod.chloeprime.gunsmithlib.fcompat.ServerLifecycleHooks.getCurrentServer();
        if (server != null && !server.isSameThread()) {
            server.execute(() -> guarded(action));
            return;
        }
        guarded(action);
    }

    private static void guarded(Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            mod.chloeprime.gunsmithlib.fcompat.GunsmithLibLog.warn("Local RPC failed", t);
        }
    }

    private static boolean isClientSide() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType()
                == net.fabricmc.api.EnvType.CLIENT;
    }

    private RPC() {}
}
