package rmi.client;



import rmi.ExchangeRateRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ExchangeRateClient {
    private ExchangeRateRMI stub;
    private static ExchangeRateClient instance;

    protected ExchangeRateClient() throws Exception {
        String host = null;
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (ExchangeRateRMI) registry.lookup("ExchangeRateRMI");

        } catch (Exception e) {
           throw new Exception("Server is currently unavailable");
        }
    }


    public double convert(String sourceCurrency, String targetCurrency, double amount) {
        try {
            return stub.convert(sourceCurrency, targetCurrency, amount);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String[] getSupportCurrencies(){
        try {
            return stub.getSupportedCurrencies();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
   *
   * This method creates a new instance of the class if none exists or returns a reference to
   * the object already created
   */
    public static ExchangeRateClient getInstance() throws Exception {
        if(instance == null) {
            instance = new ExchangeRateClient();
        }
        return instance;
    }
}
