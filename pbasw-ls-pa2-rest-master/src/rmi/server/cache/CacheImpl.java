package rmi.server.cache;

import java.util.*;

public class CacheImpl<K,V> implements Map<K,V>, Cache<K,V> {

    protected long timeToLive;
    protected long updateAllTimestamp;
    protected Map<K, TimestampWrapper<V>> cache;
    protected CacheDataSource<K,V> dataSource;
    protected ReplacementPolicy replacementPolicy;
    protected int hits;
    protected int misses;
    protected int maxSize;

    public CacheImpl(long timeToLive, int maxSize, CacheDataSource<K,V> dataSource, ReplacementPolicy<K> replacementPolicy) {
        this.timeToLive = timeToLive;
        this.maxSize = maxSize;
        this.dataSource = dataSource;
        this.replacementPolicy = replacementPolicy == null ? new LeastRecentlyUsedPolicy() : replacementPolicy;
        this.cache = new HashMap<>();
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the maximum size of the cache.
     * A size of 0 will disable the cache; negative values will disable the size limit.
     * @param newMaxSize New max size for the cache.
     */
    @Override
    public void setMaxSize(int newMaxSize) {
        maxSize = newMaxSize;
        trim();
    }

    /**
     * @return the hit ratio for the cache. (= hits/(hits+misses))
     */
    @Override
    public float hitRatio() {
        return 1.0f * hits / (hits+misses);
    }

    @Override
    public long getTimeToLive() {
        return timeToLive;
    }

    /**
     * Set the maximum time in milliseconds before data is invalidated.
     * @param newTimeToLive Time in milliseconds before data is invalidated.
     */
    @Override
    public void setTimeToLive(long newTimeToLive) {
        timeToLive = newTimeToLive;
    }

    @Override
    public CacheDataSource<K, V> getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(CacheDataSource<K, V> newDataSource) {
        dataSource = newDataSource;
    }

    @Override
    public ReplacementPolicy getReplacementPolicy() {
        return replacementPolicy;
    }

    /**
     * Sets the replacement policy to use when the cache reaches max size.
     * @param newReplacementPolicy the replacement policy to use when the cache reaches max size.
     */
    @Override
    public void setReplacementPolicy(ReplacementPolicy newReplacementPolicy) {
        replacementPolicy = newReplacementPolicy;
    }

    /**
     * @return The current size of the cache.
     */
    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        updateHitRatio(key);
        replacementPolicy.registerAccess(key);
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    @Override
    public V get(Object key) {
        updateHitRatio(key);
        K keyAsK;
        try{
            keyAsK = (K) key;
        }catch (ClassCastException e){
            throw new RuntimeException(String.format("%s is not a valid key type!", key.getClass()), e);
        }
        if(!cache.containsKey(key)) addToCache(keyAsK, dataSource.get(keyAsK));
        replacementPolicy.registerAccess(key);
        return cache.get(key).value;
    }

    @Override @Deprecated
    public V put(K key, V value) {
        throw new UnsupportedOperationException("You can not manually add elements to the cache!");
    }

    @Override
    public V remove(Object key) {
        updateHitRatio(key);
        replacementPolicy.remove(key);
        return cache.remove(key).value;
    }

    @Override @Deprecated
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("You can not manually add elements to the cache!");
    }

    @Override
    public void clear() {
        cache.clear();
        replacementPolicy.clear();
    }

    @Override
    public Set<K> keySet() {
        updateAllIfNeeded();
        return cache.keySet();
    }

    @Override
    public Collection<V> values() {
        updateAllIfNeeded();
        Collection<V> result = new ArrayList<>(cache.size());
        for (TimestampWrapper<V> wrapper : cache.values()) {
            result.add(wrapper.value);
        }
        return result;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        updateAllIfNeeded();
        Set<Entry<K,V>> result = new HashSet<>(cache.size());
        for (Entry<K, TimestampWrapper<V>> entry : cache.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue().value;
            result.add(new AbstractMap.SimpleEntry<>(key, value));
        }
        return result;
    }

    private void updateAllIfNeeded() {
        if(System.currentTimeMillis() <= updateAllTimestamp + timeToLive) return;
        addAllToCache(dataSource.getAll());
        updateAllTimestamp = System.currentTimeMillis();
    }

    /**
     * Trims the cache, so that it is not larger than maxSize
     */
    private void trim(){
        if(maxSize < 0) return;
        while(size() > maxSize){
            Object itemToReplace = replacementPolicy.itemToReplace();
            cache.remove(itemToReplace);
        }
    }

    private void addToCache(K key, V value) {
        boolean hit = updateHitRatio(key);
        trim();
        replacementPolicy.registerAccess(key);
        TimestampWrapper<V> wrapped = !hit ? new TimestampWrapper<>() : cache.get(key); // Reuse wrapper if it exists
        wrapped.value = value;
        wrapped.timestamp = System.currentTimeMillis();
        cache.put(key, wrapped);
    }

    private void addAllToCache(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            addToCache(entry.getKey(), entry.getValue());
        }
    }

    private boolean updateHitRatio(Object key) {
        boolean hit = cache.containsKey(key);
        if(hit) hits++;
        else misses++;
        return hit;
    }

    private class TimestampWrapper<T>{
        private T value;
        private long timestamp;
    }
}
