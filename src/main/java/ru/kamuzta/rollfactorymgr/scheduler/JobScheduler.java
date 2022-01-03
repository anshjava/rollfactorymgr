package ru.kamuzta.rollfactorymgr.scheduler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * One-Thread Scheduler
 * All tasks are executed in one thread in order of priority.
 * Can perform
 * -deferred tasks
 * -tasks with repetition in case of an error
 * -periodic tasks
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class JobScheduler {
    private static int i = 0;
    private static int THREAD_COUNT = 3;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_COUNT, r -> {
        final Thread t = new Thread(r);
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.setName(JobScheduler.class.getSimpleName() + " " + (i++));
        return t;
    });

    @SafeVarargs
    public static void scheduleNow(@NotNull ThrowingRunnable r, @NotNull Class<? extends Throwable>... retryOn) {
        schedule(r, 0, TimeUnit.SECONDS, retryOn);
    }

    @SafeVarargs
    public static void schedule(@NotNull ThrowingRunnable r, long t, @NotNull TimeUnit timeUnit, @NotNull Class<? extends Throwable>... retryOn) {
        executor.schedule(() -> {
            while (true) {
                try {
                    r.run();
                    break;
                } catch (Throwable e) {
                    boolean needRetry = false;
                    for (Class<? extends Throwable> clazz : retryOn) {
                        if (clazz.isAssignableFrom(e.getClass())) {
                            needRetry = true;
                            break;
                        }
                    }

                    if (needRetry) {
                        log.warn("Error while performing job", e);
                        log.warn("Rescheduling for {} {} later.", t, timeUnit.toString().toLowerCase());

                        try {
                            timeUnit.sleep(t);
                        } catch (InterruptedException e1) {
                            log.warn("Pause interrupted.", e1);
                        }
                    } else {
                        log.error("Failed while performing job.", e);
                        break;
                    }
                }
            }
            return null;
        }, t, timeUnit);
    }

    /**
     * periodic execution of tasks
     * @param job
     * @param initialDelay
     * @param period
     */
    public static void periodSchedule(@NotNull ThrowingRunnable job, int initialDelay, int period, @NotNull TimeUnit timeUnit) {
        executor.scheduleWithFixedDelay(() -> {
                    try {
                        job.run();
                    } catch (Exception e) {
                        log.warn("Error while performing job", e);
                    }
                }
                , initialDelay, period, timeUnit);
    }

    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}

