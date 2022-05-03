package ru.kamuzta.rollfactorymgr.ui.main;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.internal.viewloader.View;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.event.*;
import ru.kamuzta.rollfactorymgr.exception.UserFriendlyException;
import ru.kamuzta.rollfactorymgr.ui.*;
import ru.kamuzta.rollfactorymgr.ui.client.ClientCreateView;
import ru.kamuzta.rollfactorymgr.ui.client.ClientCreateViewModel;
import ru.kamuzta.rollfactorymgr.ui.client.ClientEditView;
import ru.kamuzta.rollfactorymgr.ui.client.ClientEditViewModel;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogAlert;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogHelper;
import ru.kamuzta.rollfactorymgr.ui.dialog.WaitDialogView;
import ru.kamuzta.rollfactorymgr.ui.dialog.WaitDialogViewModel;
import ru.kamuzta.rollfactorymgr.ui.error.ErrorDialogView;
import ru.kamuzta.rollfactorymgr.ui.error.ErrorDialogViewModel;
import ru.kamuzta.rollfactorymgr.ui.javafx.EventConsumerType;
import ru.kamuzta.rollfactorymgr.ui.javafx.PlatformUtil;
import ru.kamuzta.rollfactorymgr.ui.menu.MenuView;
import ru.kamuzta.rollfactorymgr.ui.message.MessageDialogView;
import ru.kamuzta.rollfactorymgr.ui.message.MessageDialogViewModel;
import ru.kamuzta.rollfactorymgr.ui.roll.RollCreateView;
import ru.kamuzta.rollfactorymgr.ui.roll.RollCreateViewModel;
import ru.kamuzta.rollfactorymgr.ui.roll.RollEditView;
import ru.kamuzta.rollfactorymgr.ui.roll.RollEditViewModel;
import ru.kamuzta.rollfactorymgr.ui.underlay.UnderlayView;
import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;
import ru.kamuzta.rollfactorymgr.model.IdentityPair;
import ru.kamuzta.rollfactorymgr.model.Pair;
import ru.kamuzta.rollfactorymgr.utils.UserNotificationsLog;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;
import static ru.kamuzta.rollfactorymgr.ui.javafx.EventConsumerType.HANDLER;

@Slf4j
@Singleton
public class MainView {
    public static final String DIALOG_WRAPPER_STYLE = "dialog-wrapper";
    public static final String UNDERLAY_PANE_STYLE = "underlay-pane";
    public static final String ROOT_CSS_CLASS = "root";

    public static final double MINIMUM_WINDOW_WIDTH = 800;
    public static final double MINIMUM_WINDOW_HEIGHT = 600;


    private final KeyEventFilter keyEventFilter;
    private final EventBus eventBus;
    private final DialogHelper dialogHelper;

    private Map<IdentityPair<EventConsumerType, EventType<KeyEvent>>, EventHandler<KeyEvent>> keyEventsConsumers;

    private final Table<Screen, KeyCodeCombination, Pair<Node, Runnable>> keyBindings = HashBasedTable.create();

    private final List<ViewAndViewModel> screens = newArrayList();

    private Stage stage;
    private StackPane rootPane;

    @Inject
    protected MainView(KeyEventFilter keyEventFilter, EventBus eventBus, DialogHelper dialogHelper) {
        this.keyEventFilter = keyEventFilter;
        this.eventBus = eventBus;
        this.dialogHelper = dialogHelper;
        this.eventBus.register(this);

    }

    @Subscribe
    public void onDead(DeadEvent deadEvent) {
        log.warn("Got dead event: {}", deadEvent.getEvent());
    }

    public void init(Stage stage) throws IOException {
        this.stage = stage;
        // very strange hack for Linux and 1024×768 resolution
        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (stage.getX() == 0) {
                    stage.setX(0);
                }
                if (stage.getY() == 0) {
                    stage.setY(0);
                }
            }
        });

        final Parent rootView = FXMLLoader.load(getClass().getResource("MainView.fxml"));

        final Scene rootScene = new Scene(rootView, MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT);
        this.rootPane = (StackPane) rootView;

        this.stage.titleProperty().set("Roll Factory Manager");
        this.stage.setScene(rootScene);
        this.stage.setMaximized(false);
        this.stage.setResizable(false);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(new Image(getClass().getResourceAsStream("../../../../../img/icon.png")));

        keyEventsConsumers = ImmutableMap.<IdentityPair<EventConsumerType, EventType<KeyEvent>>, EventHandler<KeyEvent>>builder()
                .put(new IdentityPair<>(HANDLER, KeyEvent.KEY_PRESSED), keyEventFilter)
                .build();

        keyEventsConsumers.forEach((key, value) -> key.getFirst().addToWindow(this.stage, key.getSecond(), value));

        //if left outside the parent element - return focus to the first element
        rootScene.focusOwnerProperty().addListener(
                (observable, oldValue, newValue) -> ofNullable(getCurrentViewAndViewModel())
                        .ifPresent(viewAndViewModel -> viewAndViewModel.getFocusController().tryReturnFocus(newValue)));

        this.stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            final String message = BooleanUtils.isTrue(newValue) ? ">>> Main window got focus" : ">>> Main window lost focus";
            log.debug(message);
        });

        this.stage.setOnCloseRequest(event -> {
            Platform.exit();
        });
    }

    public void start() {
        stage.show();
        onStart();
    }

    @SuppressWarnings("SameParameterValue")
    private <V extends FxmlView<M>, M extends ViewModel> void showScreenWithoutUnderlay(Screen screen, Class<V> viewClass) {
        showFullScreen(screen, viewClass, false);
    }

    private <V extends FxmlView<M>, M extends ViewModel> void showFullScreen(Screen screen, Class<V> viewClass) {
        showFullScreen(screen, viewClass, true);
    }

    private <V extends FxmlView<M>, M extends ViewModel> void showFullScreen(Screen screen, Class<V> viewClass, boolean forceUnderlay) {
        ofNullable(getCurrentViewAndViewModel()).ifPresent(vAndVM -> vAndVM.getFocusController().setLastFocusedNodeBeforeHide(stage.getScene().getFocusOwner()));

        removeOtherView(screen);

        final ViewAndViewModel rawVVM = loadView(screen, viewClass);
        final ViewAndViewModel newViewAndModel = forceUnderlay ? underlay(rawVVM) : rawVVM;

        addScreen(newViewAndModel);

        Parent view = newViewAndModel.getView();

        rootPane.getChildren().add(view);

        log.info("Screen [{}] showed. Provided by {}", screen, newViewAndModel.getModel().getClass());
        newViewAndModel.getView().toFront();

        if (screen.isFullScreen()) {
            eventBus.post(new ScreenChangedEvent(screen));
        }
    }

    private <V extends FxmlView<M>, M extends ViewModel> M showDialog(Screen screen, Class<V> viewClass) {
        ofNullable(getCurrentViewAndViewModel()).ifPresent(vAndVM -> vAndVM.getFocusController().setLastFocusedNodeBeforeHide(stage.getScene().getFocusOwner()));

        final ViewAndViewModel rawVVM = loadView(screen, viewClass);
        final ViewAndViewModel newViewAndModel = underlay(rawVVM);

        addScreen(newViewAndModel);
        Parent view = newViewAndModel.getView();
        rootPane.getChildren().add(view);
        log.info("Dialog [{}] showed. Provided by {}", screen, newViewAndModel.getModel().getClass());
        view.toFront();
        return (M) newViewAndModel.getModel();
    }

    private <V extends FxmlView<M>, M extends ViewModel> ViewAndViewModel loadView(Screen screen, Class<V> viewClass) {
        getScreen(screen).ifPresent(viewAndViewModel -> {
            log.warn("Screen {} already exists - wasn't removed properly before - so remove it now", screen);
            removeScreen(screen);
        });

        return createViewAndViewModel(screen, viewClass);
    }

    private <V extends FxmlView<M>, M extends ViewModel> ViewAndViewModel createViewAndViewModel(Screen screen, Class<V> viewClass) {
        try {
            final ViewTuple<V, M> view =
                    FluentViewLoader.fxmlView(viewClass)
                            .load();
            view.getView().addEventHandler(ScreenEvent.SCREEN_CLOSE_REQUEST, event -> {
                removeScreen(screen, event.getResult());
                if (event.isRequireFocusOnParent()) {
                    final boolean shouldShowMainMenu = screen != Screen.WAIT_DIALOG && (screens.isEmpty() || (screens.size() == 1 && screens.stream().anyMatch(_screen -> Screen.NOTIFICATION.equals(_screen.getScreen()))));
                    if (shouldShowMainMenu) {
                        showMainView();
                    } else {
                        ofNullable(getCurrentViewAndViewModel()).ifPresent(viewAndViewModel -> viewAndViewModel.getFocusController().requestFocusOnLastOrFirst());
                    }
                }
            });
            return new ViewAndViewModel(screen, view.getView(), view.getViewModel());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            disposeFailedView(viewClass);
            dialogHelper.showError(e);
            throw ExceptionUtils.wrapNotRuntimeException(e);
        }
    }

    private ViewAndViewModel underlay(ViewAndViewModel viewAndViewModel) {
        final Parent underlayView = createUnderlayFor(viewAndViewModel.getView());
        viewAndViewModel.setView(underlayView);
        return viewAndViewModel;
    }

    private Parent createUnderlayFor(Parent view) {
        final StackPane result = new StackPane();

        final Pane borderPane;
        borderPane = new BorderPane();
        borderPane.getStylesheets().add("mystyle.css");
        borderPane.getStyleClass().add(UNDERLAY_PANE_STYLE);

        result.getChildren().add(borderPane);
        result.getChildren().add(view);
        return result;
    }

    private void addScreen(ViewAndViewModel viewAndViewModel) {
        screens.add(viewAndViewModel);
    }

    private void removeScreen(Screen screen, ButtonType result) {
        getScreen(screen).ifPresent(viewAndViewModel -> {
            ofNullable(viewAndViewModel.getModel()).ifPresent(viewModel -> eventBus.post(new DisposeEvent<>(viewModel.getClass())));
            ofNullable(viewAndViewModel.getDialog()).ifPresent(dialog -> {
                dialog.completeDialog(ofNullable(result).orElse(ButtonType.CLOSE));
                /* when closing the dialog box, check that there are no dialogs among other open windows.
                 * if there is, then do not close the gray background from behind. */
                if (screens.stream()
                        .filter(openedViewAndViewModel -> !openedViewAndViewModel.equals(viewAndViewModel))
                        .noneMatch(openedViewAndViewModel -> openedViewAndViewModel.getDialog() != null)) {
                    removeScreen(Screen.UNDERLAY);
                }
            });
            rootPane.getChildren().remove(viewAndViewModel.getView());
            screens.remove(viewAndViewModel);
            log.info("Screen [{}] removed. Provided by {}", screen,
                    Optional.ofNullable(viewAndViewModel.getModel())
                            .map(_model -> _model.getClass().toString())
                            .orElse(StringUtils.EMPTY));

        });
    }

    private void removeScreen(Screen screen) {
        removeScreen(screen, null);
    }

    private void removeOtherView(Screen currentScreen) {
            Arrays.stream(Screen.values()).forEach(this::removeScreen);
    }

    /**
     * Get topmost window
     */
    private Optional<ViewAndViewModel> getScreen(Screen screen) {
        return screens.stream()
                .filter(viewAndViewModel -> screen.equals(viewAndViewModel.getScreen()))
                .reduce((prev, curr) -> curr);
    }

    @NotNull
    private ViewModel getCurrentViewModel() {
        ViewAndViewModel mainView = getCurrentViewAndViewModel();
        if (mainView != null) {
            return getCurrentViewAndViewModel().getModel();
        } else {
            throw new UserFriendlyException("MainView is null");
        }
    }

    @Nullable
    private Screen getCurrentScreen() {
        return ofNullable(getCurrentViewAndViewModel()).map(ViewAndViewModel::getScreen).orElse(null);
    }

    @Nullable
    private ViewAndViewModel getCurrentViewAndViewModel() {
        return Iterables.getLast(screens, null);
    }

    private void showNotification(String text) {
        UserNotificationsLog.logNotification(text);
        final ViewAndViewModel<NotificationViewModel> view = loadView(Screen.NOTIFICATION, NotificationView.class);
        view.getModel().setText(text);

        if (!rootPane.getChildren().contains(view.getView())) {
            rootPane.getChildren().add(view.getView());
        }

        screens.stream()
                .filter(screen -> Objects.equals(screen.getView(), view.getView()))
                .findAny()
                .ifPresent(screens::remove);

        addScreen(view);

        alignViewOnCenter(view.getView());
        view.getView().toFront();
    }

    private void alignViewOnCenter(Parent view) {
        StackPane.setAlignment(view, Pos.BOTTOM_CENTER);
        Platform.runLater(() -> {
            final double topMargin = rootPane.getHeight() - ((StackPane) view).getMinHeight();
            StackPane.setMargin(view, new Insets(topMargin, 0, 0, 0));
        });
    }

    @Nullable
    private Screen findScreenForNodeInScreens(Node node) {
        ArrayList<ViewAndViewModel> reversedScreens = newArrayList(screens);
        Collections.reverse(reversedScreens);

        return reversedScreens
                .stream()
                .filter(viewAndViewModel -> inNodeInParent(node, viewAndViewModel.getView()))
                .findFirst()
                .map(ViewAndViewModel::getScreen)
                .orElse(null);
    }


    private boolean inNodeInParent(Node node, Parent parent) {
        Node nodeParent = node;
        while (nodeParent != null) {
            if (nodeParent == parent) {
                return true;
            }
            nodeParent = nodeParent.getParent();
        }
        return false;
    }

    private void pause(long pause) {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            log.error("interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }


    private void onStart() {
        showMainView();
        final WaitDialogTaskWithoutResult task = new WaitDialogTaskWithoutResult(this::isReadyToWork);
        dialogHelper.executeLongtimeOperationAndWait(task, "initialization...");
    }

    private void showMainView() {
        eventBus.post(new MainMenuOpenEvent());
    }

    // --------------------------------- Events -----------------------------------------------------


    @Subscribe
    public void onEvent(@NotNull MainMenuOpenEvent event) {
        Platform.runLater(() -> {
            showFullScreen(Screen.MENU, MenuView.class);
        });
    }

    @Subscribe
    public <V extends FxmlView<M>, M extends ViewModel> void onEvent(@NotNull ShowFullScreenEvent<V,M> event) {
        showFullScreen(event.getScreen(), event.getClazz());
    }

    @Subscribe
    public void onEvent(@NotNull ShowEditRollEvent event) {
        RollEditViewModel model = showDialog(Screen.ROLL_EDIT, RollEditView.class);
        model.setRollProperty(event.getRollProperty());
    }

    @Subscribe
    public void onEvent(@NotNull ShowCreateRollEvent event) {
        RollCreateViewModel model = showDialog(Screen.ROLL_CREATE, RollCreateView.class);
    }

    @Subscribe
    public void onEvent(@NotNull ShowEditClientEvent event) {
        ClientEditViewModel model = showDialog(Screen.CLIENT_EDIT, ClientEditView.class);
        model.setClientProperty(event.getClientProperty());
    }

    @Subscribe
    public void onEvent(@NotNull ShowCreateClientEvent event) {
        ClientCreateViewModel model = showDialog(Screen.CLIENT_CREATE, ClientCreateView.class);
    }

    @Subscribe
    public void onEvent(@NotNull KeyPressedEvent event) {
        final ViewModel currentViewModel = getCurrentViewModel();
        //todo
    }


    @Subscribe
    public void onEvent(@NotNull NotificationEvent event) {
        Platform.runLater(() -> showNotification(event.getText()));
    }

    @Subscribe
    public void onEvent(@NotNull ErrorDialogOpenEvent event) {
        try {
            showDialog(Screen.ERROR_DIALOG, ErrorDialogView.class);
            ErrorDialogViewModel viewModel = (ErrorDialogViewModel) getCurrentViewModel();
            viewModel.onError(event);
        } catch (ClassCastException e) {
            log.error("Error on opening error dialog: ", e);
            removeScreen(Screen.ERROR_DIALOG);
            onEvent(event);
        }
    }


    @Subscribe
    public void onEvent(@NotNull MessageDialogOpenEvent event) {
        PlatformUtil.executeInJavaFxThreadSilent(() -> {
            if (event.getDialog() == null) {
                showFullScreen(Screen.MESSAGE_DIALOG, MessageDialogView.class);
                MessageDialogViewModel viewModel = (MessageDialogViewModel) getCurrentViewModel();
                viewModel.onMessageOpen(event);
            } else {
                initMessageDialog(event);
            }
        });
    }

    @Subscribe
    public void onEvent(@NotNull WaitDialogOpenEvent event) {
        if (event.getDialog() == null) {
            showFullScreen(Screen.WAIT_DIALOG, WaitDialogView.class);

            WaitDialogViewModel waitDialogViewModel = (WaitDialogViewModel) getCurrentViewModel();
            waitDialogViewModel.onLongtimeOperation(event);
        } else {
            initWaitDialog(event);
        }
    }

    @Subscribe
    public void onEvent(@NotNull UnderlayOpenEvent event) {
        showFullScreen(Screen.UNDERLAY, UnderlayView.class);
    }

    @Subscribe
    public void onEvent(@NotNull UnderlayCloseEvent event) {
        removeScreen(Screen.UNDERLAY);
    }

    private void invokeFxThread(Runnable runner) {
        if (Platform.isFxApplicationThread()) {
            runner.run();
        } else {
            Platform.runLater(runner);
        }
    }

    public void initMessageDialog(@NotNull MessageDialogOpenEvent event) {
        initDialog(event.getDialog(), event.getTitle(), Screen.MESSAGE_DIALOG, MessageDialogView.class,
                model -> model.onMessageOpen(event));
    }

    public void initWaitDialog(@NotNull WaitDialogOpenEvent event) {
        initDialog(event.getDialog(), event.getMessage(), Screen.WAIT_DIALOG, WaitDialogView.class, model -> model.onLongtimeOperation(event));
    }

    private <V extends FxmlView<M>, M extends ViewModel> void initDialog(DialogAlert dialog, String title, Screen screen,
                                                                         Class<V> viewClass, Consumer<M> initModelAction) {
        showFullScreen(Screen.UNDERLAY, UnderlayView.class);
        ViewAndViewModel<M> viewAndViewModel = loadView(screen, viewClass);
        viewAndViewModel.setDialog(dialog);
        addScreen(viewAndViewModel);
        ofNullable(initModelAction).ifPresent(action -> action.accept(viewAndViewModel.getModel()));
        fillDialogContent(dialog, viewAndViewModel);
        log.info("Dialog [{}] with title [{}] showed. Provided by {}", screen, title, viewAndViewModel.getModel().getClass());
    }

    @SuppressWarnings("SameParameterValue")
    private <V extends FxmlView<M>, M extends ViewModel> void initDialog(DialogAlert dialog, String title, Screen screen, Class<V> viewClass) {
        initDialog(dialog, title, screen, viewClass, null);
    }

    private void fillDialogContent(Alert dialog, ViewAndViewModel viewAndViewModel) {
        Parent view = viewAndViewModel.getView();
        view.getStyleClass().add(ROOT_CSS_CLASS);
        dialog.initOwner(stage);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(view);
        if (viewAndViewModel.getModel() instanceof DialogManaged) {
            ((DialogManaged) viewAndViewModel.getModel()).setDialog(dialog);
        }

        keyEventsConsumers.forEach((key, value) -> key.getFirst().addToPane(dialogPane, key.getSecond(), value));
    }

    private <V extends FxmlView<M>, M extends ViewModel> void disposeFailedView(Class<V> viewClass) {
        Class<? extends ViewModel> viewModelClass = (Class<? extends ViewModel>) TypeResolver.resolveRawArgument(View.class, viewClass);
        eventBus.post(new DisposeEvent<>(viewModelClass));
    }

    private boolean isOnMenuScreen() {
        return isOnScreen(Screen.MENU);
    }

    private boolean isOnScreen(Screen screen) {
        return Objects.equals(screen, getCurrentScreen());
    }

    private void isReadyToWork() {
        //todo
    }
}
