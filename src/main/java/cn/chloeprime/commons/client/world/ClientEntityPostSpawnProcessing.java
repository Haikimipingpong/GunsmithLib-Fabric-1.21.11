package cn.chloeprime.commons.client.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Runs a callback once the given client entity ids exist. */
public final class ClientEntityPostSpawnProcessing {
    public static final int MAX_TRIAGES = 20;

    private record Pending(int id, Consumer<Entity> action, int tries) {}

    private static final List<Pending> PENDING = new ArrayList<>();

    public static void process(int id, Consumer<Entity> action) {
        process(new int[]{id}, action);
    }

    public static void process(int[] ids, Consumer<Entity> action) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        for (var id : ids) {
            var entity = level.getEntity(id);
            if (entity != null) {
                action.accept(entity);
            } else {
                PENDING.add(new Pending(id, action, MAX_TRIAGES));
            }
        }
    }

    /** Called from the client tick hook. */
    public static void tick() {
        if (PENDING.isEmpty()) return;
        var level = Minecraft.getInstance().level;
        if (level == null) {
            PENDING.clear();
            return;
        }
        PENDING.removeIf(pending -> {
            var entity = level.getEntity(pending.id());
            if (entity != null) {
                try {
                    pending.action().accept(entity);
                } catch (Throwable t) {
                    mod.chloeprime.gunsmithlib.fcompat.GunsmithLibLog.warn(
                            "ClientEntityPostSpawnProcessing callback failed for entity {}", entity, t);
                }
                return true;
            }
            return pending.tries() <= 0;
        });
        if (!PENDING.isEmpty()) {
            PENDING.replaceAll(p -> new Pending(p.id(), p.action(), p.tries() - 1));
        }
    }

    private ClientEntityPostSpawnProcessing() {}
}
