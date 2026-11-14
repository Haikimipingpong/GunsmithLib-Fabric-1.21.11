package cn.chloeprime.commons.async;

import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Fabric port: tick-based scheduler driven by GunsmithLib's own tick events.
 */
public interface TaskScheduler {
    Task delay(int ticks, Consumer<Task> action);

    Task repeat(int period, Consumer<Task> action);

    Task countdown(int ticks, int period, Consumer<Task> action);

    default TaskScheduler withCondition(BooleanSupplier condition) {
        var parent = this;
        return new TaskScheduler() {
            @Override
            public Task delay(int ticks, Consumer<Task> action) {
                return parent.delay(ticks, task -> {
                    if (condition.getAsBoolean()) action.accept(task);
                    else task.cancel();
                });
            }

            @Override
            public Task repeat(int period, Consumer<Task> action) {
                return parent.repeat(period, task -> {
                    if (condition.getAsBoolean()) action.accept(task);
                    else task.cancel();
                });
            }

            @Override
            public Task countdown(int ticks, int period, Consumer<Task> action) {
                return parent.countdown(ticks, period, task -> {
                    if (condition.getAsBoolean()) action.accept(task);
                    else task.cancel();
                });
            }
        };
    }

    default java.util.concurrent.CompletableFuture<Void> delay(int ticks) {
        var future = new java.util.concurrent.CompletableFuture<Void>();
        delay(ticks, task -> future.complete(null));
        return future;
    }

    static TaskScheduler createTickBased(LogicalSide side) {
        return new TickScheduler();
    }

    static TaskScheduler createRealTimeBased(LogicalSide side) {
        return new TickScheduler();
    }

    static TaskScheduler createManuallyUpdated() {
        return new TickScheduler();
    }
}
