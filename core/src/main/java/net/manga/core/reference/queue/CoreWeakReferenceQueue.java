package net.manga.core.reference.queue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.manga.api.reference.EntryReference;
import net.manga.api.reference.EntryReferenceQueue;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CoreWeakReferenceQueue<T> extends ReferenceQueue<T> implements EntryReferenceQueue<T> {
    private static final MethodHandle ENQUEUE;

    static {
        try {
            final Method enqueue = ReferenceQueue.class.getDeclaredMethod("enqueue", Reference.class);
            enqueue.setAccessible(true);

            ENQUEUE = MethodHandles.lookup().unreflect(enqueue);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to initialize enqueue method", throwable);
        }
    }

    public CoreWeakReferenceQueue() {
    }

    @Override
    public @Nullable EntryReference<T> safePoll() {
        return (EntryReference<T>) super.poll();
    }

    @Override
    @SneakyThrows
    public void safeOffer(@NonNull EntryReference<T> reference) {
        if (!(reference instanceof WeakReference)) {
            return;
        }

        ENQUEUE.invoke(this, reference);
    }
}
