package codes.reactor.sdk.scheduler.tick;

import java.util.Arrays;

final class LaterTasks {
    private Runnable[] tasks = new Runnable[16];
    private int[] delays = new int[16];
    private int size = 0;

    void addTask(final Runnable task, final int delay) {
        if (size >= tasks.length) {
            int newSize = tasks.length * 2;
            tasks = Arrays.copyOf(tasks, newSize);
            delays = Arrays.copyOf(delays, newSize);
        }
        tasks[size] = task;
        delays[size] = delay;
        size++;
    }

    void executeAll() {
        if (size == 0) return;

        int writeIndex = 0;
        for (int readIndex = 0; readIndex < size; readIndex++) {
            Runnable task = tasks[readIndex];
            if (task == null) continue;

            if (delays[readIndex] <= 0) {
                task.run();
                tasks[readIndex] = null;
                continue;
            }

            delays[readIndex]--;
            if (writeIndex != readIndex) {
                tasks[writeIndex] = tasks[readIndex];
                delays[writeIndex] = delays[readIndex];
                tasks[readIndex] = null;
            }
            writeIndex++;
        }
        size = writeIndex;
    }
}