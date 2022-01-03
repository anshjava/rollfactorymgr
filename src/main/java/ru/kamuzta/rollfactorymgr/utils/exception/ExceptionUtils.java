package ru.kamuzta.rollfactorymgr.utils.exception;

import com.google.common.base.Strings;
import com.google.inject.ProvisionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.exception.NotRuntimeExceptionWrapper;
import ru.kamuzta.rollfactorymgr.exception.UserFriendlyException;

import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.find;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ExceptionUtils {

    /**
     * Method for prepare error message
     *
     * @param throwable
     * @param msg
     * @return
     */
    public static String createErrorMessage(Throwable throwable, String msg) {
        if (!Strings.isNullOrEmpty(msg)) {
            return msg;
        }

        Throwable currentThrowable = throwable;
        StringBuilder sb = new StringBuilder();

        // if chain start with UserFriendlyException, show only UserFriendlyException
        final boolean onlyFriendlyExceptions = isUserFriendly(currentThrowable);

        do {
            if (sb.length() > 0) {
                sb.append("\n");
            }

            final String className;
            if (isUserFriendly(currentThrowable)) {
                // such exceptions have clear diagnostics, it is enough to print only the text of the message
                className = "";
            } else {
                className = currentThrowable.getClass().getCanonicalName();
            }

            final String message = currentThrowable.getMessage();

            sb.append(className);

            if ((!Strings.isNullOrEmpty(className) && !Strings.isNullOrEmpty(message))) {
                sb.append(": ");
            }

            if (message != null) {
                sb.append(message);
            }
            sb.append("\n");

            Throwable nextThrowable = currentThrowable;
            do {
                nextThrowable = org.apache.commons.lang3.exception.ExceptionUtils.getCause(nextThrowable);
            } while (onlyFriendlyExceptions && nextThrowableHasSameMessage(currentThrowable, nextThrowable));

            currentThrowable = nextThrowable;
        } while (currentThrowable != null && (isUserFriendly(currentThrowable) || !onlyFriendlyExceptions));

        return sb.toString();
    }

    public static String createErrorMessage(Throwable throwable) {
        return createErrorMessage(throwable, null);
    }

    public static String createStackTrace(Throwable throwable) {
        return throwable != null ? org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable) : StringUtils.EMPTY;
    }

    public static RuntimeException wrapNotRuntimeException(Throwable throwable) {
        if (throwable instanceof RuntimeException) return (RuntimeException) throwable;

        return new NotRuntimeExceptionWrapper(throwable);
    }

    public static Throwable unwrapWrappedNotRuntimeException(Throwable e) {
        e = unwrapInvocationTargetException(e);

        return e instanceof NotRuntimeExceptionWrapper ? e.getCause() : e;
    }

    /**
     * Take off wrapping of Exception
     * @param e
     * @return
     */
    private static Throwable unwrapInvocationTargetException(Throwable e) {
        Throwable exception = e;
        while (true) {
            if (exception instanceof UserFriendlyException) {
                return exception;
            }
            if (exception instanceof ProvisionException && exception.getCause() != null) {
                return exception.getCause();
            }

            if (exception.getCause() == null) {
                break;
            }
            exception = exception.getCause();
        }

        if(e instanceof RuntimeException && e.getCause() != null && (e.getCause() instanceof InvocationTargetException)) {
            return e.getCause().getCause();
        }
        return e;
    }

    public static <T> T extractException(final Class<T> exClass, final Throwable ex) {
        return (T) find(getCausalChain(ex), input -> exClass.isInstance(input), null);
    }

    private static boolean nextThrowableHasSameMessage(Throwable current, Throwable next) {
        return next != null && next.getMessage() != null && next.getMessage().equals(current.getMessage());
    }

    private static boolean isUserFriendly(Throwable throwable) {
        return throwable instanceof UserFriendlyException;
    }
}
