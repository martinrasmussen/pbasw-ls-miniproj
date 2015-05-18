package api.remote.restcountries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.prism.shader.Solid_ImagePattern_Loader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Martin Rasmussen on 12/05/2015.
 */
public class RestCountryCountries {
    private static final String BASE_REST_PATH_TEMPLATE = "http://restcountries.eu/rest/v1/name/%s?fullText=true";
    private static final String ALPHA_2_CODE_PROPERTY = "alpha2Code";
    private static final String NAME_PROPERTY = "name";
    private static final String CAPITAL_PROPERTY = "capital";
    private static final String LATLNG_PROPERTY = "latlng";
    private static final String CURRENCY_PROPERTY = "currencies";

    private static URL getUrl(String countryName) throws MalformedURLException {
        String path = String.format(BASE_REST_PATH_TEMPLATE, countryName);
        return new URL(path);
    }

    private static JsonNode getJsonNodeFromInputStream(InputStream stream) {
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            return new ObjectMapper().readTree(reader);
        } catch (IOException e) {
            System.out.println("I HAVE FAILED");
        }
        return null;
    }

    public static RestCountriesCountry getCountry(String countryName) {
        try (InputStream stream = getUrl(countryName).openStream()) {
            JsonNode countryNode = getJsonNodeFromInputStream(stream).get(0);
            String name = countryNode.get(NAME_PROPERTY).asText();
            String capital = countryNode.get(CAPITAL_PROPERTY).asText();
            String alpha2Code = countryNode.get(ALPHA_2_CODE_PROPERTY).asText();
            JsonNode latlongNode = countryNode.get(LATLNG_PROPERTY);
            double latitude = latlongNode.get(0).asDouble();
            double longitude = latlongNode.get(1).asDouble();
            JsonNode currencyNode = countryNode.get(CURRENCY_PROPERTY);
            String currency = currencyNode.get(0).asText();


            RestCountriesCountry country = new RestCountriesCountry();
            country.setName(name);
            country.setCapital(capital);
            country.setISO(alpha2Code);
            country.setLatitude(latitude);
            country.setLongitude(longitude);
            country.setCurrency(currency);
            stream.close();
            return country;
        } catch (Exception e) {
            return null;
        }

    }




}
