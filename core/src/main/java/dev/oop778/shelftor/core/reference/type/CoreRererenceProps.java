package dev.oop778.shelftor.core.reference.type;

public class CoreRererenceProps<T> {
    private final boolean identity;
    private final int hashCode;

    public CoreRererenceProps(boolean identity, T referent) {
        this.identity = identity;
        this.hashCode = identity ? System.identityHashCode(referent) : referent.hashCode();
    }

    public int getHashCode() {
        return this.hashCode;
    }

    public boolean isIdentity() {
        return this.identity;
    }
}
