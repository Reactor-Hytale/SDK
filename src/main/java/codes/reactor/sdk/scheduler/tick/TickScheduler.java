package codes.reactor.sdk.scheduler.tick;

import codes.reactor.sdk.scheduler.Scheduler;
import org.jetbrains.annotations.NotNull;

public class TickScheduler implements Scheduler {

    private final NowTasks nowTasks = new NowTasks();
    private final LaterTasks laterTasks = new LaterTasks();
    private final ScheduleTasks scheduleTasks = new ScheduleTasks();

    public void tick() {
        nowTasks.executeAll();
        laterTasks.executeAll();
        scheduleTasks.executeAll();
    }

    @Override
    public void runNow(final @NotNull Runnable task) {
        nowTasks.addTask(task);
    }

    @Override
    public void runAtTick(final @NotNull Runnable task, int tickToExecute) {
        if (tickToExecute - 1 <= 0) {
            runNow(task);
            return;
        }
        laterTasks.addTask(task, tickToExecute - 1);
    }

    @Override
    public int scheduleAtTick(final @NotNull Runnable task, final int tickToStart, final int executeInTheTick) {
        int tickToStartValue = Math.max(0, tickToStart - 1);
        int delayBetween = Math.max(0, executeInTheTick - 1);
        return scheduleTasks.addTask(task, tickToStartValue, delayBetween);
    }

    @Override
    public void runAfterDelay(final @NotNull Runnable task, final int delay) {
        if (delay <= 0) {
            nowTasks.addTask(task);
            return;
        }
        laterTasks.addTask(task, delay);
    }

    @Override
    public int scheduleWithDelayBetween(final @NotNull Runnable task, final int delayFirstExecute, final int delayBetweenExecute) {
        return scheduleTasks.addTask(task, delayFirstExecute, delayBetweenExecute);
    }

    @Override
    public boolean cancelScheduleTask(int taskId) {
        return scheduleTasks.removeTask(taskId);
    }

    @Override
    public Scheduler createNewScheduler() {
        return new TickScheduler();
    }
}