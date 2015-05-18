package rmi.server.api.remote;

public interface CurrencyNameProvider {
    /**
     * Get the full name of the currency matching the given ISO-4217 symbol.
     *
     * @param symbol The ISO-4217 symbol of the currency to get the name for.
     * @return The full name of the currency matching the given ISO-4217 symbol, or the symbol itself, if no matching
     * name was provided.
     */
    String getNameForSymbol(String symbol);
}
