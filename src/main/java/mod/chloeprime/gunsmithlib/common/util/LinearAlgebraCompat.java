package mod.chloeprime.gunsmithlib.common.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

/** Replaces Ritchies Projectile Lib's LinearAlgebraTypes (not available on Fabric). */
public final class LinearAlgebraCompat {
    public static Vector3d moj2joml(Vec3 vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vec3 joml2moj(Vector3d vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static Vec3 joml2moj(Vector3f vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static Vector3f cast(Vector3f vec) {
        return vec;
    }

    public static Vector3d cast(Vector3d vec) {
        return vec;
    }

    public static Vector3f castToF(Vector3d vec) {
        return new Vector3f((float) vec.x, (float) vec.y, (float) vec.z);
    }

    private LinearAlgebraCompat() {}
}
