package dev.oop778.shelftor.core.util;

import java.util.function.Supplier;

public class OptionalLocking {
    private final Object lock;

    public OptionalLocking(boolean useLock) {
        this.lock = useLock ? new Object() : null;
    }

    public <T> T lockingSupply(Supplier<T> supplier) {
        if (this.lock == null) {
            return supplier.get();
        }

        synchronized (this.lock) {
            return supplier.get();
        }
    }

    public void locking(Runnable runnable) {
        this.lockingSupply(() -> {
            runnable.run();
            return null;
        });
    }
}
