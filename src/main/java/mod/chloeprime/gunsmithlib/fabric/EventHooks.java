package mod.chloeprime.gunsmithlib.fabric;

import mod.chloeprime.gunsmithlib.fcompat.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.InactiveProfiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Fires the fcompat events that NeoForge would normally dispatch itself.
 * Without this the whole event bus stays silent and GunsmithLib does nothing.
 */
public final class EventHooks {
    /** Listeners collected from {@link AddReloadListenerEvent}; none of them run by themselves. */
    private static final List<PreparableReloadListener> RELOAD_LISTENERS = new ArrayList<>();

    public static void registerCommon() {
        // 先收集 datapack 监听器（变体集、脚本等都在这里注册），再在重载时手动执行
        var collectEvent = new AddReloadListenerEvent();
        NeoForge.EVENT_BUS.post(collectEvent);
        RELOAD_LISTENERS.addAll(collectEvent.getListeners());

        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleHooks::setCurrentServer);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            NeoForge.EVENT_BUS.post(new LevelEvent.Unload(server.overworld()));
            ServerLifecycleHooks.setCurrentServer(null);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            cn.chloeprime.commons.async.TickScheduler.tickAll();
            for (var level : server.getAllLevels()) {
                NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
            }
        });
        // 枪包功能（子弹追踪、药水云、空爆……）依赖"实体进入世界"事件来初始化
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register(
                (entity, world) -> NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, world)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_UNLOAD.register(
                (entity, world) -> NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity, world)));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer())));

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath("gunsmithlib", "reload_hook");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                runReloadListeners(manager);
                var server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    NeoForge.EVENT_BUS.post(new OnDatapackSyncEvent(server, null));
                }
            }
        });
    }

    /** Runs every collected {@link PreparableReloadListener} synchronously (prepare + apply). */
    private static void runReloadListeners(ResourceManager manager) {
        var shared = new PreparableReloadListener.SharedState(manager);
        var barrier = new PreparableReloadListener.PreparationBarrier() {
            @Override
            public <T> CompletableFuture<T> wait(T value) {
                return CompletableFuture.completedFuture(value);
            }
        };
        for (var listener : RELOAD_LISTENERS) {
            try {
                listener.prepareSharedState(shared);
                listener.reload(shared, Runnable::run, barrier, Runnable::run).join();
            } catch (Throwable t) {
                GunsmithLibLog.warn("Reload listener {} failed", listener.getClass().getName(), t);
            }
        }
    }

    public static void registerClient() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> NeoForge.EVENT_BUS.post(new ClientTickEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            NeoForge.EVENT_BUS.post(new ClientTickEvent.Post());
            cn.chloeprime.commons.client.world.ClientEntityPostSpawnProcessing.tick();
        });
    }

    private EventHooks() {}
}
