package mod.chloeprime.gunsmithlib.fcompat;

import java.util.function.Consumer;

public interface IEventBus {
    <T extends Event> T post(T event);

    void register(Object target);

    <T extends Event> void addListener(Consumer<T> listener);

    <T extends Event> void addListener(EventPriority priority, Consumer<T> listener);

    <T extends Event> void addListener(Class<T> type, Consumer<T> listener);
}
