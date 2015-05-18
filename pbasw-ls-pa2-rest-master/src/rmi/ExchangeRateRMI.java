package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ExchangeRateRMI extends Remote {
    /**
     * Converts an amount of source currency to target currency.
     *
     * @param sourceCurrency The three-letter symbol of the source currency
     * @param targetCurrency The three-letter symbol of the target currency
     * @param amount         The amount of source currency to buy
     * @return The price of the given amount of target currency in the source currency
     * @throws java.rmi.RemoteException
     */
    double convert(String sourceCurrency, String targetCurrency, double amount) throws RemoteException;

    /**
     * @return An array of all currency symbols supported by the API.
     */
    String[] getSupportedCurrencies() throws RemoteException;
}
