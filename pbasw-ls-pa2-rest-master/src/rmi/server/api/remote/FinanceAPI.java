package rmi.server.api.remote;

import java.util.Map;

public interface FinanceAPI {
    /**
     * @return An array of all currency symbols supported by the API.
     */
    String[] getSupportedCurrencies();

    /**
     * Retrieve a map of all supported currencies and their rates.
     *
     * @return A map of all supported currencies and their rates.
     */
    Map<String,Double> getAllRates();

    /**
     * Returns the conversion rate between the supplied currency symbols.
     *
     * @param sourceCurrency
     *      The currencies to retrieve the conversion rate for, in the format SSSTTT.
     *      Example: US Dollars to Euro -> USDEUR.
     * @param targetCurrency
     * @return
     *      The conversion rate between the source currency (first three letters of the currencies parameter) and the
     *      target currency (the last three letters of the currencies parameter)
     */
    double getExchangeRate(String sourceCurrency, String targetCurrency);

    /**
     * Retrieve the ISO-4217 symbol for the base currency of this API.
     * @return The ISO-4217 symbol for the base currency of this API.
     */
    String getBaseCurrency();
}
