package dev.oop778.shelftor.core.shelf;

import dev.oop778.shelftor.api.shelf.ShelfSettings;
import java.util.Collection;
import lombok.ToString;

@ToString
public class CoreShelfSettings implements ShelfSettings {
    private boolean concurrent;
    private boolean hashable;
    private boolean weak;

    public CoreShelfSettings(boolean concurrent, boolean hashable) {
        this.concurrent = concurrent;
        this.hashable = hashable;
    }

    public CoreShelfSettings(ShelfSettings settings) {
        this.concurrent = settings.isConcurrent();
        this.hashable = settings.isHashable();
        this.weak = settings.isWeak();
    }

    public static CoreShelfSettings create() {
        return new CoreShelfSettings(false, false);
    }

    @Override
    public boolean isConcurrent() {
        return this.concurrent;
    }

    @Override
    public boolean isHashable() {
        return this.hashable;
    }

    @Override
    public boolean isWeak() {
        return this.weak;
    }

    public void setWeak(boolean weak) {
        this.weak = weak;
    }

    public void setHashable(boolean hashable) {
        this.hashable = hashable;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    @Override
    public void dump(Collection<String> lines) {
        lines.add("== Settings ==");
        lines.add("concurrent = " + this.concurrent);
        lines.add("hashable = " + this.hashable);
        lines.add("weak = " + this.weak);
    }
}
