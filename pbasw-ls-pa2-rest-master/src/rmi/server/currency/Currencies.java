package rmi.server.currency;

import java.util.Objects;

public class Currencies {

    public static double convert(ForexCurrency sourceCurrency, ForexCurrency targetCurrency){
        return convert(1, sourceCurrency, targetCurrency);
    }

    public static double convert(double amount, ForexCurrency sourceCurrency, ForexCurrency targetCurrency){
        return amount * getConvertionRate(sourceCurrency, targetCurrency);
    }

    private static double getConvertionRate(ForexCurrency sourceCurrency, ForexCurrency targetCurrency) {
        if(!Objects.equals(sourceCurrency.getBase(), targetCurrency.getBase())) {
            throw new IncompatibleCurrencyBasesException(sourceCurrency, targetCurrency);
        }
        return targetCurrency.getRate() * (1/sourceCurrency.getRate());
    }

    public static class IncompatibleCurrencyBasesException extends RuntimeException{
        public IncompatibleCurrencyBasesException(String message) {
            super(message);
        }

        public IncompatibleCurrencyBasesException(ForexCurrency currency1, ForexCurrency currency2) {
            this(String.format("Base of %s does not match the base of %s!", currency1.getSymbol(), currency2.getSymbol()));
        }
    }
}
