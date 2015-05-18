package rmi.server.currency;

/**
 * Interface describing a foreign exchange currency.
 */
public interface ForexCurrency {
    /**
     * @return the symbol for the base currency for this foreign exchange rate.
     */
    String getBase();

    /**
     * @return the amount of this currency you may buy for one base currency.
     */
    double getRate();

    /**
     * @param newRate the new amount of this currency you may buy for one base currency.
     */
    void setRate(double newRate);

    /**
     * @return the name of the currency.
     */
    String getName();

    /**
     * @return the ISO-4217 symbol for this currency;
     */
    String getSymbol();
}
