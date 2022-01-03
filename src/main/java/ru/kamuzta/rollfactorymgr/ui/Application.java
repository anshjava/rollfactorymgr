package ru.kamuzta.rollfactorymgr.ui;

import com.google.inject.Module;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.exception.ApplicationInitException;
import ru.kamuzta.rollfactorymgr.modules.EventBusModule;
import ru.kamuzta.rollfactorymgr.ui.lock.AlreadyLockedException;
import ru.kamuzta.rollfactorymgr.ui.lock.ApplicationLock;
import ru.kamuzta.rollfactorymgr.ui.lock.CouldNotLockException;
import ru.kamuzta.rollfactorymgr.ui.lock.CouldNotReleaseLockException;
import ru.kamuzta.rollfactorymgr.ui.main.MainView;

import java.util.List;

@Slf4j
public class Application extends MvvmfxGuiceApplication {

    public static void launch(String... args) {
        MvvmfxGuiceApplication.launch(args);
    }

    public static void main(String[] args) {
        try {
            ApplicationFinalizer.prepareToAbnormalShutdown();
            Application.launch(args);
        } catch (Exception e) {
            ApplicationInitErrorProcessor.processException(e);
        }
    }

    @Override
    public void initGuiceModules(List<Module> modules) throws Exception {
        try {
            modules.add(new EventBusModule());
        } catch (Exception e) {
            ApplicationInitErrorProcessor.processException(e);
        }
    }

    @Override
    public void startMvvmfx(Stage stage) {
        try {
            startApp(stage);
        } catch (Exception e) {
            ApplicationInitErrorProcessor.processException(e);
        }
    }

    private void startApp(Stage stage) throws Exception {
        final LogAndShowDialogExceptionHandler exceptionHandler = getInjector().getInstance(LogAndShowDialogExceptionHandler.class);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        initApplicationLock();
        final MainView mainView = getInjector().getInstance(MainView.class);
        mainView.init(stage);
        mainView.start();
        startSchedulers();
    }

    @Override
    public void stopMvvmfx() throws Exception {
        log.info("About to exit the application...");
        ApplicationFinalizer.exit(ExitStatus.SUCCESS);
    }

    private void startSchedulers() {
        //todo
    }

    private void initApplicationLock() {
        final ApplicationLock applicationLock = getInjector().getInstance(ApplicationLock.class);

        try {
            applicationLock.enter();
        } catch (CouldNotLockException e) {
            log.warn("Could not set lock", e);
            throw new ApplicationInitException("Ошибка", "Приложение уже запущено");
        } catch (AlreadyLockedException e) {
            log.warn("Already locked by other instance", e);
            throw new ApplicationInitException("Ошибка", "Приложение уже запущено");
        }

        // Schedule release lock
        releaseLockOnShutdown(applicationLock);
    }

    private static void releaseLockOnShutdown(@NotNull ApplicationLock applicationLock) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                applicationLock.release();
            } catch (CouldNotReleaseLockException e) {
                log.warn("Could not release lock", e);
            }
        }));
    }
}

