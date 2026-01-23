package codes.reactor.sdk.scheduler.tick;

import java.util.Arrays;

final class ScheduleTasks {
    private Runnable[] tasks = new Runnable[16];
    private int[] ids = new int[16];
    private int[] delays = new int[16];
    private int[] countdowns = new int[16];

    private int taskIdCount = 0;
    private int size = 0;

    void executeAll() {
        if (size == 0) return;
        for (int i = 0; i < size; i++) {
            Runnable runnable = tasks[i];
            if (runnable == null) continue;

            if (countdowns[i] > 0) {
                countdowns[i]--;
                continue;
            }
            countdowns[i] = delays[i];
            runnable.run();
        }
    }

    int addTask(final Runnable runnable, final int startDelay, final int delay) {
        ensureCapacity();
        int id = ++taskIdCount;
        tasks[size] = runnable;
        ids[size] = id;
        delays[size] = delay;
        countdowns[size] = startDelay;
        size++;
        return id;
    }

    boolean removeTask(int id) {
        for (int i = 0; i < size; i++) {
            if (ids[i] == id) {
                tasks[i] = null;
                compact(i);
                return true;
            }
        }
        return false;
    }

    private void compact(int fromIndex) {
        for (int i = fromIndex; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
            ids[i] = ids[i + 1];
            delays[i] = delays[i + 1];
            countdowns[i] = countdowns[i + 1];
        }
        size--;
        tasks[size] = null;
    }

    private void ensureCapacity() {
        if (size >= tasks.length) {
            int newSize = tasks.length * 2;
            tasks = Arrays.copyOf(tasks, newSize);
            ids = Arrays.copyOf(ids, newSize);
            delays = Arrays.copyOf(delays, newSize);
            countdowns = Arrays.copyOf(countdowns, newSize);
        }
    }
}