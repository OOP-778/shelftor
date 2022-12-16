package net.manga.core.util.closeable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.manga.api.util.Closeable;

public class CloseableHolder implements Closeable {
    private List<Closeable> closeables;

    public CloseableHolder() {
        this.closeables = new ArrayList<>();
    }

    protected synchronized void addCloseable(Closeable closeable) {
        this.closeables.add(closeable);
    }

    protected synchronized void removeCloseable(Closeable closeable) {
        this.closeables.remove(closeable);
    }

    @Override
    public synchronized void close() {
        final Iterator<Closeable> iterator = this.closeables.iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
            iterator.remove();
        }
    }
}
