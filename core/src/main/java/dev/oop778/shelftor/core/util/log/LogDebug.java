package dev.oop778.shelftor.core.util.log;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.function.Supplier;

public class LogDebug {
    public static boolean DEBUG = false;

    public static void log(String message, Object... args) {
        if (!DEBUG) {
            return;
        }

        final Deque<StackTraceElement> stackTraceElements = collectStack();
        final StackTraceElement pop = stackTraceElements.pop();

        final String[] split = pop.getClassName().split("\\.");
        final String simpleName = split[split.length - 1];

        System.out.printf("[%s] [%s]: %s%n",
            System.nanoTime(),
            String.format("%s#%s", simpleName, pop.getMethodName()),
            String.format(message, args)
        );
    }

    private static Deque<StackTraceElement> collectStack() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        final Deque<StackTraceElement> elements = new ArrayDeque<>(Arrays.asList(stackTrace));
        elements.removeIf((element) -> element.getClassName().contains("dev.oop778.shelftor.core.util.log"));

        elements.removeFirst();
        return elements;
    }
}
