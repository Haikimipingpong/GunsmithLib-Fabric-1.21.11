package mod.chloeprime.gunsmithlib.fcompat;

import java.util.function.Consumer;

public interface IEventBus {
    <T> T post(T event);

    void register(Object target);

    <T> void addListener(Consumer<T> listener);

    <T> void addListener(EventPriority priority, Consumer<T> listener);

    <T> void addListener(Class<T> type, Consumer<T> listener);
}
