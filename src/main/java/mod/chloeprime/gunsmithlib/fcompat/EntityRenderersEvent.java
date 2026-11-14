package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityRenderersEvent extends Event {
    private EntityRenderersEvent() {}

    public static class RegisterRenderers extends EntityRenderersEvent {
        public static final Map<EntityType<?>, EntityRendererProvider<?>> RENDERERS = new HashMap<>();

        public <T extends net.minecraft.world.entity.Entity> void registerEntityRenderer(EntityType<? extends T> type,
                                                                                        EntityRendererProvider<T> provider) {
            RENDERERS.put(type, provider);
        }
    }

    public static class RegisterLayerDefinitions extends EntityRenderersEvent {
        public static final Map<ModelLayerLocation, Supplier<LayerDefinition>> LAYERS = new HashMap<>();

        public void registerLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> supplier) {
            LAYERS.put(location, supplier);
        }
    }
}
