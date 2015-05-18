package resources.representations.proxy;

import application.ServerApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class XMLProxyResource extends ServerResource {

    @Get("xml")
    public Representation represent() {
        ServerApplication app = (ServerApplication) getApplication();
        Map<String, String> cache = app.getXmlProxyCache();
        String url = (String) getRequest().getAttributes().get("url");

        if (cache.containsKey(url)) return new StringRepresentation(cache.get(url));

        try (InputStream input = new URL(url).openStream()) {
            StringBuilder sb = new StringBuilder();
            Scanner sc = new Scanner(input);
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
            }
            String xmlString = sb.toString();
            cache.put(url, xmlString);
            app.Debug();
            return new StringRepresentation(xmlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); // TODO: Handle exception
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: Handle exception
        }
    }

}
