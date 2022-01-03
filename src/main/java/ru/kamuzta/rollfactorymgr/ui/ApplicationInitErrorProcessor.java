package ru.kamuzta.rollfactorymgr.ui;

import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.kamuzta.rollfactorymgr.exception.ApplicationInitException;

import java.util.concurrent.CountDownLatch;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationInitErrorProcessor {

    public static void processException(@NotNull Throwable e) {
        log.error("Error on application init:", e);
        try {
            final String header;
            final String content;
            if (e instanceof ApplicationInitException) {
                final ApplicationInitException applicationInitException = (ApplicationInitException) e;
                header = applicationInitException.getTitle();
                content = applicationInitException.getContent();
            } else {
                header = "Something goes wrong...";
                content = e.getMessage();
            }

            showAlert(Strings.nullToEmpty(header), Strings.nullToEmpty(content));
        } finally {
            ApplicationFinalizer.exit(ExitStatus.INIT_ERROR);
        }
    }

    private static void showAlert(@NotNull String header, @NotNull String content) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Runnable runnable = () -> {
            try {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.titleProperty().set("Error during initialization");
                alert.headerTextProperty().set(header);
                alert.contentTextProperty().set(content);
                alert.showAndWait();
            } finally {
                countDownLatch.countDown();
            }
        };
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {

            }
        }
    }
}

