package rmi.server.currency;



import rmi.server.api.remote.CurrencyNameProvider;
import rmi.server.api.remote.FinanceAPI;
import rmi.server.cache.CacheDataSource;

import java.util.HashMap;
import java.util.Map;

public class CurrencyDataSource implements CacheDataSource<String, ForexCurrency> {

    private FinanceAPI api;
    private CurrencyNameProvider nameProvider;

    public CurrencyDataSource(FinanceAPI financeAPI, CurrencyNameProvider nameProvider){
        this.api = financeAPI;
        this.nameProvider = nameProvider;
    }

    @Override
    public ForexCurrency get(String currencySymbol) {
        return convertToCurrency(currencySymbol);
    }

    @Override
    public Map<String, ForexCurrency> getAll() {
        Map<String, ForexCurrency> result = new HashMap<>();
        Map<String, Double> rates = api.getAllRates();
        for (String symbol : rates.keySet()) {
            double rate = rates.get(symbol);
            String name = nameProvider.getNameForSymbol(symbol);
            String base = api.getBaseCurrency();
            result.put(symbol, new Currency(name, base, symbol, rate));
        }
        return result;
    }

    private ForexCurrency convertToCurrency(String currencySymbol) {
        String name = nameProvider.getNameForSymbol(currencySymbol);
        String base = api.getBaseCurrency();
        double rate = api.getExchangeRate(base, currencySymbol);
        return new Currency(name, base, currencySymbol, rate);
    }
}
