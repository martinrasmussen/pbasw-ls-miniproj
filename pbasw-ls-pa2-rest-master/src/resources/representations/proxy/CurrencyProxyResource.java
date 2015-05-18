package resources.representations.proxy;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import rmi.client.ExchangeRateClient;

/**
 * Created by Martin Rasmussen on 13/05/2015.
 */
public class CurrencyProxyResource extends ServerResource{

    @Get
    public Representation currencyConversion(){
        String fromCountry = (String) getRequest().getAttributes().get("fromCountry");
        String toCountry = (String) getRequest().getAttributes().get("toCountry");

        System.out.println("From Counntry: " + fromCountry);
        System.out.println("To Counntry: " + toCountry);
        try {
            String currency = String.valueOf(ExchangeRateClient.getInstance().convert(fromCountry, toCountry, 1.0));
            return new StringRepresentation(currency);
        } catch (Exception e) {
            System.out.println("CURRENCY CONVERSION FAILED");
            return null;
        }

    }
}
