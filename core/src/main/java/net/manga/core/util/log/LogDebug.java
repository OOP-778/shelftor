package net.manga.core.util.log;

public class LogDebug {
    public static boolean DEBUG = false;

    public static void log(String message, Object... args) {
        System.out.println(String.format(message, args));
    }
}
