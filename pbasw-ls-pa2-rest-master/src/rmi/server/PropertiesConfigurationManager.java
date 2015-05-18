package rmi.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PropertiesConfigurationManager implements ConfigurationManager {

    private static final String
            UPDATE_RATE_KEY             = "CacheItemTTL",
            UPDATE_RATE_DEFAULT         = "86400000", // 24h
            EXCLUDED_CURRENCIES_KEY     = "ExcludedCurrencies",
            EXCLUDED_CURRENCIES_DEFAULT = "XAG,XAU"; // Silver & gold

    private Properties properties;
    private File configFile;

    public PropertiesConfigurationManager(String configPath) {
        loadConfigFile(configPath);
    }

    private static String toCommaseparatedString(String... values) {
        StringBuilder sb = new StringBuilder(4* values.length);
        sb.append(values.length > 0 ? values[0] : "");
        for (int i = 1; i < values.length; i++) {
            sb.append(',');
            sb.append(values[i]);
        }
        return sb.toString();
    }

    public void loadConfigFile(String configPath) {
        try {
            properties = new Properties();
            configFile = new File(configPath);
            if (configFile.exists()) {
                properties.load(new FileReader(configFile));
            } else {
                properties.setProperty(UPDATE_RATE_KEY, UPDATE_RATE_DEFAULT);
                properties.setProperty(EXCLUDED_CURRENCIES_KEY, EXCLUDED_CURRENCIES_DEFAULT);
                saveChanges();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveChanges() throws IOException {
        FileWriter writer = new FileWriter(configFile, false);
        properties.store(writer, null);
        writer.close();
    }

    @Override
    public long getCacheTTL() {
        long result = Long.parseLong(UPDATE_RATE_DEFAULT);
        String updateRateString = null;
        try {
            updateRateString = properties.getProperty(UPDATE_RATE_KEY, UPDATE_RATE_DEFAULT);
            result = Long.parseLong(updateRateString);
        } catch (NumberFormatException nfe) {
            System.out.println(String.format("Invalid option for '%s': '%s'. Resetting to default...", UPDATE_RATE_KEY, updateRateString));
            properties.setProperty(UPDATE_RATE_KEY, UPDATE_RATE_DEFAULT);
            try {
                saveChanges();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return result;
    }

    @Override
    public void setUpdateRate(long newUpdateRate) {
        try {
            properties.setProperty(UPDATE_RATE_KEY, newUpdateRate+"");
            saveChanges();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excludeCurrencies(String... currenciesToExclude){
        if(currenciesToExclude.length == 0) return;
        Set<String> excludedSet = new TreeSet<String>(Arrays.asList(getExcludedCurrencies()));
        excludedSet.addAll(Arrays.asList(currenciesToExclude));
        String[] excludedArray = excludedSet.toArray(new String[excludedSet.size()]);
        String value = toCommaseparatedString(excludedArray);
        properties.setProperty(EXCLUDED_CURRENCIES_KEY, value);
        try {
            saveChanges();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void includeCurrencies(String... currenciesToInclude){
        if(currenciesToInclude.length == 0) return;
        Set<String> excludedSet = new TreeSet<String>(Arrays.asList(getExcludedCurrencies()));
        excludedSet.removeAll(Arrays.asList(currenciesToInclude));
        String[] excludedArray = excludedSet.toArray(new String[excludedSet.size()]);
        String value = toCommaseparatedString(excludedArray);
        properties.setProperty(EXCLUDED_CURRENCIES_KEY, value);
        try {
            saveChanges();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getExcludedCurrencies() {
        String currencyString = properties.getProperty(EXCLUDED_CURRENCIES_KEY, EXCLUDED_CURRENCIES_DEFAULT);
        String[] currencySplit = currencyString.split(",");
        List<String> currencyList = new ArrayList<String>();
        for (int i = 0; i < currencySplit.length; i++) {
            String s = currencySplit[i].trim();
            if (s.isEmpty()) continue;
            currencyList.add(s);
        }
        return currencyList.toArray(new String[currencyList.size()]);
    }
}
