package cn.chloeprime.commons.async;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Simple tick-driven scheduler; ticks are advanced by GunsmithLib's level/tick events. */
public final class TickScheduler implements TaskScheduler {
    private record Entry(Entry self, int ticksLeft, int period, Consumer<Task> action) {}

    private static final class TaskImpl implements Task {
        private boolean cancelled;
        private int ticksLeft;
        private final int period;
        private final Consumer<Task> action;

        TaskImpl(int ticksLeft, int period, Consumer<Task> action) {
            this.ticksLeft = ticksLeft;
            this.period = period;
            this.action = action;
        }

        @Override
        public void stop() {
            cancelled = true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }

    private static final List<TaskImpl> TASKS = new ArrayList<>();

    private static void ensureHook() {
        // 由 GunsmithLib 的 tick 钩子统一驱动
    }

    /** 由 EventHooks 在每个服务端/客户端 tick 调用。 */
    public static void tickAll() {
        List<TaskImpl> snapshot;
        synchronized (TASKS) {
            snapshot = new ArrayList<>(TASKS);
        }
        for (var task : snapshot) {
            if (task.cancelled) {
                synchronized (TASKS) { TASKS.remove(task); }
                continue;
            }
            if (--task.ticksLeft > 0) continue;
            try {
                task.action.accept(task);
            } catch (Throwable t) {
                task.stop();
            }
            if (task.cancelled) {
                synchronized (TASKS) { TASKS.remove(task); }
            } else {
                task.ticksLeft = task.period;
            }
        }
    }

    @Override
    public Task delay(int ticks, Consumer<Task> action) {
        ensureHook();
        var task = new TaskImpl(Math.max(1, ticks), Integer.MAX_VALUE, action);
        synchronized (TASKS) { TASKS.add(task); }
        return task;
    }

    @Override
    public Task repeat(int period, Consumer<Task> action) {
        ensureHook();
        var task = new TaskImpl(Math.max(1, period), Math.max(1, period), action);
        synchronized (TASKS) { TASKS.add(task); }
        return task;
    }

    @Override
    public Task countdown(int ticks, int period, Consumer<Task> action) {
        return delay(ticks, action);
    }
}
