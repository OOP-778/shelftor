package net.manga.api.index;

@FunctionalInterface
public interface KeyMapper<K, V> {
  K map(V value);
}
