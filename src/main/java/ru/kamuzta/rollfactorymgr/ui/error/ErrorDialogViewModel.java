package ru.kamuzta.rollfactorymgr.ui.error;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.kamuzta.rollfactorymgr.event.ErrorDialogOpenEvent;
import ru.kamuzta.rollfactorymgr.utils.exception.ExceptionUtils;

public class ErrorDialogViewModel implements ViewModel {

    private final StringProperty errorMessage = new SimpleStringProperty();
    private final StringProperty errorStackTrace = new SimpleStringProperty();

    protected ErrorDialogViewModel() {
    }

    public void onError(ErrorDialogOpenEvent event) {
        Throwable unwrapped = ExceptionUtils.unwrapWrappedNotRuntimeException(event.getThrowable());
        setErrorMessage(ExceptionUtils.createErrorMessage(unwrapped, event.getMessage()));
        setErrorStackTrace(ExceptionUtils.createStackTrace(unwrapped));
    }

    public String getErrorMessage() {
        return errorMessage.get();
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    public String getErrorStackTrace() {
        return errorStackTrace.get();
    }

    public StringProperty errorStackTraceProperty() {
        return errorStackTrace;
    }

    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace.set(errorStackTrace);
    }
}
