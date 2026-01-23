package codes.reactor.sdk.scheduler;

import codes.reactor.sdk.scheduler.tick.TickScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class TickSchedulerTest {

    @Test
    void testNowTasks() {
        TickScheduler tickScheduler = new TickScheduler();
        AtomicInteger executed = new AtomicInteger();

        tickScheduler.runNow(executed::incrementAndGet);
        tickScheduler.runNow(executed::incrementAndGet);
        tickScheduler.runNow(executed::incrementAndGet);

        tickScheduler.tick();

        Assertions.assertEquals(3, executed.get());
    }

    @Test
    void testLaterTasks() {
        TickScheduler tickScheduler = new TickScheduler();
        AtomicInteger ticksElapsed = new AtomicInteger();

        AtomicBoolean task1 = new AtomicBoolean();
        AtomicBoolean task2 = new AtomicBoolean();
        AtomicBoolean task3 = new AtomicBoolean();

        tickScheduler.runAfterDelay(() -> task1.set(true), 0);
        tickScheduler.runAfterDelay(() -> task2.set(true), 2);
        tickScheduler.runAtTick(() -> task3.set(true), 3);
        for (int i = 1; i <= 4; i++) {
            tickScheduler.tick();
            ticksElapsed.set(i);

            if (ticksElapsed.get() < 1) {
                Assertions.assertFalse(task1.get(), "Task1 executed too early");
            }
            if (ticksElapsed.get() < 3) { // 2 delay + 1
                Assertions.assertFalse(task2.get(), "Task2 executed too early at tick " + ticksElapsed.get());
            }
            if (ticksElapsed.get() < 3) {
                Assertions.assertFalse(task3.get(), "Task3 executed too early at tick " + ticksElapsed.get());
            }
        }

        Assertions.assertTrue(task1.get(), "Task1 should have executed");
        Assertions.assertTrue(task2.get(), "Task2 should have executed");
        Assertions.assertTrue(task3.get(), "Task3 should have executed");
    }

    @Test
    void testScheduleAfterTasks() {
        TickScheduler tickScheduler = new TickScheduler();
        AtomicInteger task1 = new AtomicInteger();
        AtomicInteger task2 = new AtomicInteger();

        int firstTaskId = tickScheduler.scheduleWithDelayBetween(
            task1::incrementAndGet,
            2,
            3
        );

        int secondTaskId = tickScheduler.scheduleAtTick(
            task2::incrementAndGet,
            1,
            3
        );

        // Tick 1 (before starting)
        tickScheduler.tick();
        Assertions.assertEquals(0, task1.get(), "Task1: Should not execute on first tick");
        Assertions.assertEquals(1, task2.get(), "Task2: Should execute on its starting tick");

        // Tick 2
        tickScheduler.tick();
        Assertions.assertEquals(0, task1.get(), "Task1: Should not execute yet");

        // Tick 3 (first execution of task 1)
        tickScheduler.tick();
        Assertions.assertEquals(1, task1.get(), "Task1: First execution on tick 3");

        // Tick 4
        tickScheduler.tick();
        Assertions.assertEquals(1, task1.get(), "Task1: Should not repeat yet");
        Assertions.assertEquals(2, task2.get(), "Task2: Should have repeated on tick 4");

        // Tick 5
        tickScheduler.tick();
        Assertions.assertEquals(1, task1.get(), "Task1: Should not repeat yet");

        // Tick 6
        tickScheduler.tick();
        Assertions.assertEquals(1, task1.get(), "Task1: Should not repeat yet");

        // Tick 7 (second execution - 3 ticks after first execution)
        tickScheduler.tick();
        Assertions.assertEquals(2, task1.get(), "Task1: Second execution on tick 7");
        Assertions.assertEquals(3, task2.get(), "Task2: Third execution on tick 7");

        // Tick 8
        tickScheduler.tick();
        Assertions.assertEquals(2, task1.get(), "Task1: Should not repeat yet");

        // Cancel the tasks
        tickScheduler.cancelScheduleTask(firstTaskId);
        tickScheduler.cancelScheduleTask(secondTaskId);

        // Tick 9 and beyond (should not execute after cancellation)
        for (int i = 0; i < 10; i++) {
            tickScheduler.tick();
        }
        Assertions.assertEquals(2, task1.get(), "Task1: Should not execute after cancellation");
        Assertions.assertEquals(3, task2.get(), "Task2: Should not execute after cancellation");
    }
}