package cn.chloeprime.commons.math;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Local/world basis used by target searchers. */
public record Basis(Vec3 origin, Vec3 forward, Vec3 up, Vec3 right, Vec3 ignored1, Vec3 ignored2) {
    public static Basis factory(Vec3 origin, Vec3 forward, Vec3 up) {
        var f = forward.normalize();
        var r = f.cross(up).normalize();
        var u = r.cross(f).normalize();
        return new Basis(origin, f, u, r, Vec3.ZERO, Vec3.ZERO);
    }

    public static Basis fromVectors(Vec3 origin, Vec3 forward, Vec3 up) {
        return factory(origin, forward, up);
    }

    public Vec3 toLocal(Vec3 world) {
        var d = world.subtract(origin);
        return new Vec3(d.dot(right), d.dot(up), d.dot(forward));
    }

    public Vec3 toGlobal(Vec3 local) {
        return origin.add(right.scale(local.x)).add(up.scale(local.y)).add(forward.scale(local.z));
    }

    public static Basis fromBodyRotation(float yawDegrees) {
        var yaw = -yawDegrees * Mth.DEG_TO_RAD;
        var forward = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw));
        return factory(Vec3.ZERO, forward, new Vec3(0, 1, 0));
    }

    public static Basis fromEntityBody(Entity entity) {
        var yaw = entity.getYRot();
        var basis = fromBodyRotation(yaw);
        return new Basis(entity.position(), basis.forward(), basis.up(), basis.right(), Vec3.ZERO, Vec3.ZERO);
    }
}
