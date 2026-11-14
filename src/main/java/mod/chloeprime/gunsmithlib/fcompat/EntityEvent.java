package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.entity.Entity;

public class EntityEvent extends Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class SizeChanged extends EntityEvent {
        public SizeChanged(Entity entity) {
            super(entity);
        }
    }
}
