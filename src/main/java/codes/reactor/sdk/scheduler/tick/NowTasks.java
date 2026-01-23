package codes.reactor.sdk.scheduler.tick;

import java.util.Arrays;

final class NowTasks {
    private Runnable[] tasks = new Runnable[16];
    private Runnable[] backTasks = new Runnable[16];
    private int pos = 0;

    void addTask(Runnable task) {
        if (pos >= tasks.length) {
            int newSize = tasks.length * 2;
            tasks = Arrays.copyOf(tasks, newSize);
            backTasks = new Runnable[newSize];
        }
        tasks[pos++] = task;
    }

    void executeAll() {
        if (pos == 0) return;

        Runnable[] temp = tasks;
        int currentPos = pos;

        tasks = backTasks;
        pos = 0;

        for (int i = 0; i < currentPos; i++) {
            if (temp[i] != null) {
                temp[i].run();
                temp[i] = null;
            }
        }
        backTasks = temp;
    }
}