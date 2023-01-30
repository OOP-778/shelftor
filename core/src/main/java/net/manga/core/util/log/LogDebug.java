package net.manga.core.util.log;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LogDebug {
    public static boolean DEBUG = true;

    public static void log(String message, Object... args) {
        if (!DEBUG) {
            return;
        }

        final Deque<StackTraceElement> stackTraceElements = collectStack();
        final StackTraceElement pop = stackTraceElements.pop();

        System.out.printf("[%s]: %s%n",
            String.format("%s#%s", pop.getClassName(), pop.getMethodName()),
            String.format(message, args)
        );
    }

    private static Deque<StackTraceElement> collectStack() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        final Deque<StackTraceElement> elements = new ArrayDeque<>(Arrays.asList(stackTrace));
        elements.removeIf((element) -> element.getClassName().contains("net.manga.core.util.log"));

        elements.removeFirst();
        return elements;
    }
}
