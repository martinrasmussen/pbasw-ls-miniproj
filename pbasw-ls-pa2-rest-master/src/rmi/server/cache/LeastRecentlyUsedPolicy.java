package rmi.server.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LeastRecentlyUsedPolicy<T> implements ReplacementPolicy<T> {
    private Map<T, Long> valueTimestamps;
    private TreeMap<Long, T> usageMap;

    public LeastRecentlyUsedPolicy(){
        valueTimestamps = new HashMap<T, Long>();
        usageMap = new TreeMap<Long, T>();
    }

    @Override
    public T itemToReplace() {
        if(valueTimestamps.isEmpty()) return null;
        T leastUsedValue = usageMap.remove(usageMap.firstKey());
        valueTimestamps.remove(leastUsedValue);
        return leastUsedValue;
    }

    @Override
    public void registerAccess(T cacheKey) {
        Long newTimestamp = System.currentTimeMillis();
        if(valueTimestamps.containsKey(cacheKey)){
            Long lastUsedTime = valueTimestamps.remove(cacheKey);
            usageMap.remove(lastUsedTime);
            valueTimestamps.put(cacheKey, newTimestamp);
        }
        usageMap.put(newTimestamp, cacheKey);
    }

    @Override
    public void remove(T cacheKey) {
        if(!valueTimestamps.containsKey(cacheKey)) return;
        Long timestamp = valueTimestamps.remove(cacheKey);
        usageMap.remove(timestamp);
    }

    @Override
    public void clear() {
        valueTimestamps.clear();
        usageMap.clear();
    }
}
