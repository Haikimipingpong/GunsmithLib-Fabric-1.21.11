package mod.chloeprime.gunsmithlib.fabric;

import mod.chloeprime.gunsmithlib.fcompat.EntityRenderersEvent;
import mod.chloeprime.gunsmithlib.fcompat.NeoForge;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class GunsmithLibClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EventHooks.registerClient();
        registerEntityRenderers();
    }

    /**
     * NeoForge 会自己消费 EntityRenderersEvent；Fabric 需要在客户端初始化时
     * 手动把收集到的渲染器 / 模型层注册进 Fabric 的注册表。
     * 漏掉这一步会导致自定义实体（魔法激光、测距标记、3x3 药水云）渲染时 NPE。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerEntityRenderers() {
        NeoForge.EVENT_BUS.post(new EntityRenderersEvent.RegisterRenderers());
        EntityRenderersEvent.RegisterRenderers.RENDERERS.forEach((type, provider) ->
                EntityRendererRegistry.register((net.minecraft.world.entity.EntityType) type,
                        (net.minecraft.client.renderer.entity.EntityRendererProvider) provider));

        NeoForge.EVENT_BUS.post(new EntityRenderersEvent.RegisterLayerDefinitions());
        EntityRenderersEvent.RegisterLayerDefinitions.LAYERS.forEach((location, supplier) ->
                EntityModelLayerRegistry.registerModelLayer(location, supplier::get));
    }
}
