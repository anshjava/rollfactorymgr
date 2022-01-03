package ru.kamuzta.rollfactorymgr.ui;

import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.Parent;
import lombok.Getter;
import lombok.Setter;
import ru.kamuzta.rollfactorymgr.ui.dialog.DialogAlert;

@Getter
@Setter
public class ViewAndViewModel<M extends ViewModel> {
    private final Screen screen;
    private Parent view;
    private M model;
    private DialogAlert dialog;
    private final FocusController focusController;

    public ViewAndViewModel(Screen screen, Parent view, M model, DialogAlert dialog) {
        this.screen = screen;
        this.view = view;
        this.model = model;
        this.dialog = dialog;
        focusController = new FocusController(view);
    }

    public ViewAndViewModel(Parent view, M model) {
        this(Screen.ANY, view, model);
    }

    public ViewAndViewModel(Screen screen, Parent view, M model) {
        this(screen, view, model, null);
    }



    public static <M extends ViewModel> ViewAndViewModel<M> createEmpty(Screen screen) {
        return new ViewAndViewModel<M>(screen, null, null);
    }

    public boolean isEmpty() {
        return model == null && view == null;
    }

    public void fillFrom(ViewAndViewModel<M> viewAndViewModel) {
        setModel(viewAndViewModel.getModel());
        setView(viewAndViewModel.getView());
        setDialog(viewAndViewModel.getDialog());
    }

}
