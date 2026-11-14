package mod.chloeprime.gunsmithlib.fcompat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SimpleEventBus implements IEventBus {
    private record Listener(EventPriority priority, Consumer<Event> action) {}

    private final Map<Class<?>, List<Listener>> listeners = new ConcurrentHashMap<>();

    public int listenerCount(Class<?> type) {
        var l = listeners.get(type);
        return l == null ? 0 : l.size();
    }

    public int totalListeners() {
        return listeners.values().stream().mapToInt(List::size).sum();
    }

    @Override
    public <T extends Event> T post(T event) {
        for (Class<?> c = event.getClass(); c != null && Event.class.isAssignableFrom(c); c = c.getSuperclass()) {
            var list = listeners.get(c);
            if (list == null) continue;
            for (Listener l : new ArrayList<>(list)) {
                try {
                    l.action().accept(event);
                } catch (Throwable t) {
                    // NeoForge 遇到监听器异常只记录日志，不会中断事件派发和玩家登录
                    GunsmithLibLog.warn("Event listener for {} failed (ignored)", c.getName(), t);
                }
            }
        }
        return event;
    }

    @Override
    public void register(Object target) {
        // 注意：这里可能收到的是 Class 对象（事件订阅类扫描），此时要用它本身而不是 java.lang.Class
        var targetType = target instanceof Class<?> clazz ? clazz : target.getClass();
        for (Method m : targetType.getDeclaredMethods()) {
            var ann = m.getAnnotation(SubscribeEvent.class);
            if (ann == null || m.getParameterCount() != 1) continue;
            if (!Event.class.isAssignableFrom(m.getParameterTypes()[0])) continue;
            m.setAccessible(true);
            boolean isStatic = Modifier.isStatic(m.getModifiers());
            @SuppressWarnings("unchecked")
            Consumer<Event> action = e -> {
                try {
                    m.invoke(isStatic ? null : target, e);
                } catch (Throwable ex) {
                    GunsmithLibLog.warn("Failed to dispatch {} to {}", e.getClass().getName(), m, ex);
                }
            };
            var priority = ann.priority() != EventPriority.NORMAL ? ann.priority() : ann.value();
            addListenerRaw(m.getParameterTypes()[0], priority, action);
        }
    }

    @Override
    public <T extends Event> void addListener(Consumer<T> listener) {
        addListener(EventPriority.NORMAL, listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void addListener(EventPriority priority, Consumer<T> listener) {
        // Type erasure: generic type is unknown, so such listeners are silently ignored.
        mod.chloeprime.gunsmithlib.fcompat.GunsmithLibLog.warn("Event listener {} registered without an event class; ignored", listener);
    }

    public <T extends Event> void addListener(Class<T> type, Consumer<T> listener) {
        addListener(type, EventPriority.NORMAL, listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void addListener(Class<T> type, EventPriority priority, Consumer<T> listener) {
        addListenerRaw(type, priority, (Consumer<Event>) (Consumer<?>) listener);
    }

    private void addListenerRaw(Class<?> type, EventPriority priority, Consumer<Event> action) {
        var list = listeners.computeIfAbsent(type, k -> new ArrayList<>());
        synchronized (list) {
            list.add(new Listener(priority, action));
            list.sort(Comparator.comparingInt(l -> l.priority().ordinal()));
        }
    }
}
