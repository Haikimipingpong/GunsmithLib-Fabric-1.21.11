package mod.chloeprime.gunsmithlib.common.internal;

import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import mod.chloeprime.gunsmithlib.fcompat.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SuspiciousBehaviorFirewall {
    public static boolean isUnderSuspiciousContext() {
        return STACK_WALKER.walk(stack -> stack
                .map(StackWalker.StackFrame::getDeclaringClass)
                .anyMatch(SCAN_CACHE::get));
    }

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final ClassValue<Boolean> SCAN_CACHE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(@NotNull Class<?> type) {
            return isSuspiciousClass(type);
        }
    };

    private static final Set<String> SAFE_PREFIXES = Set.of(
            "com.mojang.",
            "net.minecraft.",
            "net.minecraftforge.",
            "net.neoforged.",
            "net.fabricmc.",
            "com.tacz.",
            "mod.chloeprime.",
            "cn.chloeprime.");

    private static final Set<Class<?>> SUSPICIOUS_PARENTS = Set.of(
            Enemy.class,
            Goal.class,
            Behavior.class);

    private static boolean isSuspiciousClass(Class<?> clazz) {
        // PORT: Fabric 没有 NeoForge 的 game layer；bootstrap 类（classloader == null）直接放行。
        var loader = clazz.getClassLoader();
        if (loader == null || !loader.getClass().getName().toLowerCase().contains("knot")) {
            return false;
        }
        if (SAFE_PREFIXES.stream().anyMatch(clazz.getName()::startsWith)) {
            return false;
        }

        do {
            var class2 = clazz;
            if (SUSPICIOUS_PARENTS.stream().anyMatch(parent -> parent.isAssignableFrom(class2))) {
                return true;
            }
        } while ((clazz = clazz.getDeclaringClass()) != null);

        return false;
    }
}
