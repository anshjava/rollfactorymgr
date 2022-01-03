package ru.kamuzta.rollfactorymgr.event;

import de.saxsys.mvvmfx.ViewModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DisposeEvent<M extends ViewModel>  extends UIEvent {

    private final Class<M> modelClass;

    public DisposeEvent(Class<M> modelClass) {
        this.modelClass = modelClass;
    }

    public boolean isSuitableFor(ViewModel viewModel) {
        if (viewModel == null) {
            log.error("model is null!!!");
            return false;
        } else {
            return modelClass.equals(viewModel.getClass());
        }
    }
}
