package rmi.server.cache;

public interface ReplacementPolicy<T> {
    T itemToReplace();
    void registerAccess(T cacheKey);
    void remove(T cacheKey);
    void clear();
}
