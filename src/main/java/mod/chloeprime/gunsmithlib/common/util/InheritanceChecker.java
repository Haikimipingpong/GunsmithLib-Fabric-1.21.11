package mod.chloeprime.gunsmithlib.common.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InheritanceChecker<T> {
    public InheritanceChecker(Class<T> baseType, String methodName, Class<?>... paramTypes) {
        this.givenBaseType = baseType;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.realBaseType = getDeclareClass(baseType, methodName, paramTypes);
    }

    public Class<T> getBaseType() {
        return givenBaseType;
    }

    public boolean isInherited(Class<? extends T> typeToCheck) {
        return CACHE.get(typeToCheck);
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Class<T> givenBaseType;
    private final String methodName;
    private final Class<?>[] paramTypes;
    private final Class<? super T> realBaseType;
    private final Set<Class<?>> badClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final ClassValue<Boolean> CACHE = new ClassValue<>() {
        @Override
        @SuppressWarnings("unchecked")
        protected Boolean computeValue(@Nonnull Class<?> type) {
            if (badClasses.contains(type)) {
                return false;
            }
            try {
                return !realBaseType.equals(getDeclareClass((Class<T>) type, methodName, paramTypes));
            } catch (OutOfMemoryError oom) {
                throw oom;
            } catch (Throwable ex) {
                LOGGER.warn("{} failed to inference method inheritance:", InheritanceChecker.this.getClass().getSimpleName(), ex);
                badClasses.add(type);
                return false;
            }
        }
    };

    @SuppressWarnings("unchecked")
    private Class<? super T> getDeclareClass(Class<T> type, String methodName, Class<?>[] paramTypes) {
        // PORT: 1.21.11 里部分方法（如 Block#useWithoutItem）声明位置变了，
        // 这里直接沿继承链查找第一个声明该方法的类，找不到则以 Object 兜底。
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return (Class<? super T>) c.getDeclaredMethod(methodName, paramTypes).getDeclaringClass();
            } catch (NoSuchMethodException ignored) {
                // continue walking up
            }
        }
        return (Class<? super T>) Object.class;
    }
}
