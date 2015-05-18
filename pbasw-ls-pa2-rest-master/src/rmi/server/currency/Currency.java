package rmi.server.currency;

public class Currency implements ForexCurrency {

    private String base;
    private String name;
    private String symbol;
    private double rate;

    public Currency(String name, String baseCurrencySymbol, String symbol, double rate) {
        this.name = name;
        this.base = baseCurrencySymbol;
        this.symbol = symbol;
        this.rate = rate;
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public void setRate(double newRate) {
        rate = newRate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getClass() != obj.getClass()) return false;
        Currency other = (Currency) obj;
        return this.name.equals(other.name)
                && this.base.equals(other.base)
                && this.symbol.equals(other.symbol)
                && this.rate == other.rate;
    }
}
