package com.rbasamoyai.ritchiesprojectilelib.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

/** Stand-in for the Ritchies Projectile Lib helper (library itself is unavailable on Fabric). */
public final class LinearAlgebraTypes {
    public static Vector3d moj2joml(Vec3 vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vec3 joml2moj(Vector3d vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static Vec3 joml2moj(Vector3f vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object value) {
        return (T) value;
    }

    private LinearAlgebraTypes() {}
}
