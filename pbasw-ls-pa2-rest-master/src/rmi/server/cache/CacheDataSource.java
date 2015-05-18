package rmi.server.cache;

import java.util.Map;

public interface CacheDataSource<K,V> {
    V get(K key);
    Map<K,V> getAll();
}
