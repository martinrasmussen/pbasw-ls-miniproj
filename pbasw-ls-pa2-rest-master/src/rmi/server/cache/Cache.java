package rmi.server.cache;

import java.util.Map;

public interface Cache<K,V> extends Map<K,V>{
    float hitRatio();
    long getTimeToLive();
    void setTimeToLive(long newTimeToLive);

    int getMaxSize();
    void setMaxSize(int newMaxSize);

    CacheDataSource<K,V> getDataSource();
    void setDataSource(CacheDataSource<K, V> newDataSource);

    ReplacementPolicy getReplacementPolicy();
    void setReplacementPolicy(ReplacementPolicy newReplacementPolicy);
}
