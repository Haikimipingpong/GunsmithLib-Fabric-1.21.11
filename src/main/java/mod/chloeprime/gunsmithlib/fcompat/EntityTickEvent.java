package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.entity.Entity;

public class EntityTickEvent extends EntityEvent {
    /** 避免对每个生物每 tick 都做反射派发：没有监听者时直接跳过。 */
    public static boolean hasListeners() {
        return NeoForge.EVENT_BUS.listenerCount(EntityTickEvent.Pre.class) > 0
                || NeoForge.EVENT_BUS.listenerCount(EntityTickEvent.Post.class) > 0;
    }

    public EntityTickEvent(Entity entity) {
        super(entity);
    }

    public static class Pre extends EntityTickEvent {
        public Pre(Entity entity) {
            super(entity);
        }
    }

    public static class Post extends EntityTickEvent {
        public Post(Entity entity) {
            super(entity);
        }
    }
}
