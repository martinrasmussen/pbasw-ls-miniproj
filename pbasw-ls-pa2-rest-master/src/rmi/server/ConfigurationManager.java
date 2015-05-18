package rmi.server;

public interface ConfigurationManager {
    /**
     * @return Time in milliseconds before data is invalidated.
     */
    long getCacheTTL();

    /**
     * Sets a new time in milliseconds before data is invalidated.
     *
     * @param newUpdateRate New delay before cache data must be revalidated
     */
    void setUpdateRate(long newUpdateRate);

    /**
     * Exclude one or more currencies from the currencies list.
     *
     * @param currenciesToExclude Currencies to exclude.
     */
    void excludeCurrencies(String... currenciesToExclude);

    /**
     * Include one or more currencies in the currencies list.
     *
     * @param currenciesToInclude Currencies to include.
     */
    void includeCurrencies(String... currenciesToInclude);

    /**
     * @return A list of currencies to hide from clients.
     */
    String[] getExcludedCurrencies();
}
