package ru.kamuzta.rollfactorymgr.ui;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manage app exit. In case of abnormal termination, outputs the Full dump thread to the log
 * (only works on Linux).
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ApplicationFinalizer {
    private static final Thread ON_ABNORMAL_SHUTDOWN_THREAD;

    static {
        ON_ABNORMAL_SHUTDOWN_THREAD = new Thread(ApplicationFinalizer::onAbnormalShutdown, "OnShutdownThread");
    }

    static void prepareToAbnormalShutdown() {
        Runtime.getRuntime().addShutdownHook(ON_ABNORMAL_SHUTDOWN_THREAD);
    }

    static void exit(@NotNull ExitStatus exitStatus) {
        Runtime.getRuntime().removeShutdownHook(ON_ABNORMAL_SHUTDOWN_THREAD);
        log.info("Exited with status {}.", exitStatus);
        System.exit(exitStatus.getCode());
    }

    private static void onAbnormalShutdown() {
        final String fullThreadDump = generateThreadDump();
        log.warn("\n\n====================== ABNORMAL SHUTDOWN DETECTED ======================\n\n{}", fullThreadDump);
    }

    @NotNull
    private static String generateThreadDump() {
        log.info("Gathering full thread dump... ");

        final StringBuilder sb = new StringBuilder();

        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfoArray = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
        final Map<Long, Thread> threadMap = Thread.getAllStackTraces().keySet().stream().collect(Collectors.toMap(Thread::getId, Function.identity()));

        for (ThreadInfo threadInfo : threadInfoArray) {
            if (threadInfo != null) {
                sb.append('"');
                sb.append(threadInfo.getThreadName());
                sb.append("\" #");
                sb.append(threadInfo.getThreadId());

                final Thread thread = threadMap.get(threadInfo.getThreadId());
                if (thread != null) {
                    if (thread.isDaemon()) {
                        sb.append(" daemon");
                    }
                    sb.append(" priority=");
                    sb.append(thread.getPriority());
                }

                final LockInfo lockInfo = threadInfo.getLockInfo();
                if (lockInfo != null) {
                    sb.append(" waiting on ");
                    sb.append(lockInfo.getClassName());
                    sb.append(" [");
                    sb.append(lockInfo.getIdentityHashCode());
                    sb.append("]");
                }

                sb.append("\n   java.lang.Thread.State: ");
                sb.append(threadInfo.getThreadState());
                final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
                for (final StackTraceElement stackTraceElement : stackTraceElements) {
                    sb.append("\n        at ");
                    sb.append(stackTraceElement);
                }
                sb.append("\n\n");
            }
        }

        return sb.toString();
    }
}

