package ru.kamuzta.rollfactorymgr.utils.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Class for exception message choose
 */
@Slf4j
public class ExceptionChain {

    /**
     * Show warning if exception has not its own message
     */
    boolean showWarn = false;

    Callable<String> defaultCallMessage;

    List<HashMap.Entry<Class<? extends Throwable>, Callable<String>>> chain = new ArrayList<>();

    public ExceptionChain() {
    }

    public ExceptionChain(boolean showWarn) {
        this.showWarn = showWarn;
    }

    /**
     * Add exception class and message
     *
     * @param th      exception class
     * @param message message
     * @return exception chain
     */
    public ExceptionChain addMessage(Class<? extends Throwable> th, String message) {
        chain.add(new HashMap.SimpleEntry<>(th, () -> message));
        return this;
    }

    /**
     * Add exception class and message generation function
     *
     * @param th          exception class
     * @param callMessage function of message generation
     * @return exception chain
     */
    public ExceptionChain addCallMessage(Class<? extends Throwable> th, Callable<String> callMessage) {
        chain.add(new HashMap.SimpleEntry<>(th, callMessage));
        return this;
    }

    /**
     * Set default message
     *
     * @param defaultMessage message by default
     * @return exception chain
     */
    public ExceptionChain setDefaultMessage(String defaultMessage) {
        this.defaultCallMessage = () -> defaultMessage;
        return this;
    }

    /**
     * Set default message
     *
     * @param callDefaultMessage default message generation function
     * @return exception chain
     */
    public ExceptionChain setDefaultMessage(Callable<String> callDefaultMessage) {
        this.defaultCallMessage = callDefaultMessage;
        return this;
    }


    /**
     * Process exception chain and choose first suitable message for exception
     *
     * @param t exception class
     * @return suitable message
     */
    public String execMessage(Throwable t) {
        try {
            for (Map.Entry<Class<? extends Throwable>, Callable<String>> e : chain) {

                if (equalThowable(e.getKey(), t)) {
                    return e.getValue().call();
                }
            }

            if (showWarn) {
                log.warn(" Throwable " + t + " has no specified message, use default...");
                t.printStackTrace();
            }

            return defaultCallMessage.call();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error on ExceptionChain process";
        }
    }

    private boolean equalThowable(Class<? extends Throwable> clazz, Throwable t) {
        if (t.getClass().equals(clazz)) {
            return true;
        }

        if (t.getCause() != null && t.getCause().getClass().equals(clazz)) {
            return true;
        }

        return false;
    }
}
