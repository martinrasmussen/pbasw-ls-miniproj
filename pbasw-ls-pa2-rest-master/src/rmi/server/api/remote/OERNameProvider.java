package rmi.server.api.remote;



import rmi.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class OERNameProvider implements CurrencyNameProvider {

    private static final String URL_PATH = "http://openexchangerates.org/api/currencies.json";

    private JSONObject json;

    public OERNameProvider(){
        try(InputStream stream = new URL(URL_PATH).openStream()){
            String streamAsString = new Scanner(stream).useDelimiter("\\A").next();
            json = new JSONObject(streamAsString);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: Handle exception
        }
    }

    @Override
    public String getNameForSymbol(String symbol) {
        return json.has(symbol) ? json.getString(symbol) : symbol;
    }
}
