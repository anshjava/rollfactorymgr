package ru.kamuzta.rollfactorymgr.ui;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.kamuzta.rollfactorymgr.notifier.NotificationEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for simulating timeout countdown.
 * Used to update the waiting interface in order to reassure the user that the program is not frozen ;)
 * The class launches Runnable jobs at intervals = period seconds.
 * Executes update methods in JFXT(JavaFX Thread) (with period = period seconds), enable - after enableTime seconds, disable - after timeout
 */
@Slf4j
public abstract class PeriodicalInvoker {

    private final ScheduledExecutorService executorService;

    /**
     * period in sec for update method
     */
    private final int period;

    /**
     * Time (sec), after which the enable method will be called once.
     * If < 0, then enable will not be executed.
     */
    private final int enableTime;


    protected PeriodicalInvoker(int period, int enableTime) {
        this.period = period;
        this.enableTime = enableTime;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Запускаем менеджер потоков на выполнение по расписанию.
     * Он создает Runnable и пуляет их в очередь на выполнение JFXT.
     * Отсчитывает в обратном порядке до нуля значение timeout.
     *
     * @param event.getTimeout() - таймаут обратного отсчета.
     */
    public void start(NotificationEvent event) {
        final AtomicInteger tcount = new AtomicInteger(event.getTimeout());

        // you can not call, but wait until the Runnable below does this, but then the dialog will shake
        // (there will be an inscription about Esc, then not)
        if (enableTime == 0) {
            enable(event);
        }

        executorService.scheduleAtFixedRate(() -> Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!executorService.isShutdown()) {
                        int count = tcount.getAndDecrement();
                        update(event, count);
                        if (count == event.getTimeout() - enableTime && enableTime >= 0) {
                            enable(event);
                        }
                        // When the timer expires, just turn off
                        if (count <= 0) {
                            executorService.shutdown();
                            disable(event);
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                    executorService.shutdownNow();
                    disable(event);
                }
            }
        }), 0, period, TimeUnit.SECONDS);
    }

    /**
     * Turn off the flow manager.
     */
    public void stop() {
        executorService.shutdown();
    }

    /**
     * Interface update. The code will be called with period = period seconds.
     * The code is executed in JFXT.
     *
     * @param event
     * @param count
     */
    protected abstract void update(NotificationEvent event, int count);

    /**
     * The method is called once after (approximately) enableTime seconds have elapsed since the start of the thread manager.
     * The code is executed in JFXT.
     */
    protected abstract void enable(NotificationEvent event);

    /**
     * The method is called when the timer expires or in case of an error.
     * The code is executed in JFXT.
     */
    protected abstract void disable(NotificationEvent event);
}
