package cn.chloeprime.commons.math;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class LinearAlgebraTypes {
    public static Vec3 cast(Vec3i vec) {
        return new Vec3(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector2f moj2joml(Vec2 vec) {
        return new Vector2f(vec.x, vec.y);
    }

    public static Vector3d moj2joml(Vec3 vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vector3i moj2joml(Vec3i vec) {
        return new Vector3i(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec2 joml2moj(Vector2f vec) {
        return new Vec2(vec.x, vec.y);
    }

    public static Vec3 joml2moj(Vector3d vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static Vec3 joml2moj(Vector3i vec) {
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public LinearAlgebraTypes() {}
}
