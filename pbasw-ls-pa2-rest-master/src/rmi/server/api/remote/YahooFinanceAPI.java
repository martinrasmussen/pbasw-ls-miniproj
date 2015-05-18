package rmi.server.api.remote;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class YahooFinanceAPI implements FinanceAPI{

    public static final String BASE_CURRENCY_SYMBOL = "USD";

    private static final String BASE_URL_ADDRESS_RATE = "http://quote.yahoo.com/d/quotes.cvs?s=%s=X&f=l1&e=.cvs";
    private static final String BASE_URL_ADDRESS_CURRENCIES_XML = "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote";

    // BEGIN interface methods
    @Override
    public String[] getSupportedCurrencies() {
        Set<String> symbols = getAllRates().keySet();
        return symbols.toArray(new String[symbols.size()]);
    }

    @Override
    public Map<String, Double> getAllRates() {
        final Map<String,Double> result = new HashMap<>();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            // Create an anonymous content handler
            reader.setContentHandler(new DefaultHandler(){
                final String SYMBOL = "symbol";
                final String PRICE = "price";
                final String RESOURCE = "resource";

                boolean isParsingSymbol = false;
                boolean isParsingPrice = false;

                String parsedSymbol;
                double parsedPrice;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes)
                        throws SAXException {
                    String firstAttribute = attributes.getValue(0);
                    if(firstAttribute == null) return;
                    if(firstAttribute.equals(SYMBOL)) isParsingSymbol = true;
                    if(firstAttribute.equals(PRICE)) isParsingPrice = true;
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    isParsingSymbol = isParsingPrice = false;
                    if(localName.equals(RESOURCE)) result.put(parsedSymbol, parsedPrice);
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if(isParsingSymbol) parsedSymbol = new String(ch, start, length).split("=")[0];
                    if(isParsingPrice) parsedPrice = Double.parseDouble(new String(ch, start, length));
                }
            });
            // Parse the currencies URL
            InputStream stream = getCurrenciesURL().openStream();
            reader.parse(new InputSource(stream));
            stream.close();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
        // Return an empty array if nothing was found
        return result;
    }

    @Override
    public double getExchangeRate(String sourceCurrency, String targetCurrency) {
        double result;
        try {
            URL url = getExchangeURL(sourceCurrency+targetCurrency);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String resultAsString = br.readLine();
            br.close();
            result = Double.parseDouble(resultAsString);
        } catch (IOException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String getBaseCurrency() {
        return BASE_CURRENCY_SYMBOL;
    }
    // END interface methods

    // BEGIN private methods
    private URL getExchangeURL(String currencies) throws MalformedURLException {
        String urlAddress = String.format(BASE_URL_ADDRESS_RATE, currencies);
        return new URL(urlAddress);
    }

    private URL getCurrenciesURL() throws MalformedURLException {
        return new URL(BASE_URL_ADDRESS_CURRENCIES_XML);
    }
    // END private methods
}
