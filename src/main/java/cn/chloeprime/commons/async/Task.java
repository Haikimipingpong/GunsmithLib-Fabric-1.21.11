package cn.chloeprime.commons.async;

public interface Task {
    void stop();

    default void cancel() {
        stop();
    }

    default boolean isCancelled() {
        return false;
    }
}
