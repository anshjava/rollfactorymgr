package ru.kamuzta.rollfactorymgr.ui;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ru.kamuzta.rollfactorymgr.event.ScreenEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationView implements FxmlView<NotificationViewModel>, Initializable {

    private static final long ANIMATION_DURATION = 800;
    private static final long AUTO_CLOSE_DELAY = 3000 + ANIMATION_DURATION;
    private static final double MAX_OPACITY = 1;

    @FXML
    private ImageView closeImageView;

    @FXML
    private Pane mainPane;

    @FXML
    private Label textLabel;

    @InjectViewModel
    private NotificationViewModel viewModel;

    private Future closeTask;

    private ScheduledExecutorService executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textLabel.textProperty().bind(viewModel.textProperty());

        executor =  Executors.newSingleThreadScheduledExecutor();
        closeTask = executor.schedule(
                () -> onClosePressed(new ActionEvent(mainPane, mainPane)),
                AUTO_CLOSE_DELAY,
                TimeUnit.MILLISECONDS
        );

        playShowAnimation(null);

        viewModel.closeProperty().addListener((observable, oldValue, newValue) ->
            Event.fireEvent(mainPane, new ScreenEvent(ScreenEvent.SCREEN_CLOSE_REQUEST).withoutFocusOnParent())
        );
    }

    @FXML
    public void onClosePressed(Event event) {
        closeTask.cancel(true);
        executor.shutdownNow();
        playHideAnimation(event1 -> Event.fireEvent(event.getTarget(), new ScreenEvent(ScreenEvent.SCREEN_CLOSE_REQUEST).withoutFocusOnParent()));
    }

    private void playShowAnimation(EventHandler<ActionEvent> onFinishAnimationHandler) {
        playAnimation(0, MAX_OPACITY, onFinishAnimationHandler);
    }

    private void playHideAnimation(EventHandler<ActionEvent> onFinishAnimationHandler) {
        playAnimation(MAX_OPACITY, 0, onFinishAnimationHandler);
    }

    private void playAnimation(double fromValue, double toValue, EventHandler<ActionEvent> onFinishAnimationHandler) {
        FadeTransition ft = new FadeTransition(Duration.millis(ANIMATION_DURATION), mainPane);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);
        ft.setOnFinished(onFinishAnimationHandler);
        ft.play();
    }
}