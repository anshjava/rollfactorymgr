package ru.kamuzta.rollfactorymgr.event;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kamuzta.rollfactorymgr.ui.Screen;

@AllArgsConstructor
@Getter
public class ShowFullScreenEvent<V extends FxmlView<M>, M extends ViewModel> extends UIEvent {
    private Screen screen;
    private Class<V> clazz;
}
