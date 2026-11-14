package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MobEffectEvent extends Event {
    private final LivingEntity entity;
    private final MobEffectInstance effectInstance;

    public MobEffectEvent(LivingEntity entity, @Nullable MobEffectInstance effectInstance) {
        this.entity = entity;
        this.effectInstance = effectInstance;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public @Nullable MobEffectInstance getEffectInstance() {
        return effectInstance;
    }

    public static class Added extends MobEffectEvent {
        private final MobEffectInstance oldInstance;
        private final Entity source;

        public Added(LivingEntity entity, @Nullable MobEffectInstance oldInstance, MobEffectInstance newInstance, @Nullable Entity source) {
            super(entity, newInstance);
            this.oldInstance = oldInstance;
            this.source = source;
        }

        public @Nullable MobEffectInstance getOldEffectInstance() {
            return oldInstance;
        }

        public @Nullable Entity getSource() {
            return source;
        }
    }

    public static class Remove extends MobEffectEvent {
        public Remove(LivingEntity entity, MobEffectInstance effectInstance) {
            super(entity, effectInstance);
        }
    }

    public static class Applicable extends MobEffectEvent {
        public Applicable(LivingEntity entity, MobEffectInstance effectInstance) {
            super(entity, effectInstance);
        }
    }
}
