package application;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;
import resources.representations.countries.CountryListResource;
import resources.representations.countryinfo.CountryInfoResource;
import resources.representations.proxy.CurrencyProxyResource;
import resources.representations.proxy.XMLProxyResource;
import rmi.ExchangeRateRMI;
import rmi.client.ExchangeRateClient;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class ServerApplication extends Application {
    private static ExchangeRateRMI stub;
    private VelocityManager velocityManager;
    private Map<String, String> xmlProxyCache; // A very simple cache based of a Map. Should implement ttl.

    public ServerApplication() {
        this.velocityManager = new VelocityManager();
        this.xmlProxyCache = new HashMap<>();
    }

    public static void main(String[] args) throws Exception {
        Component component = new Component();

        component.getServers().add(Protocol.HTTP, 8080); // We want to allow HTTP requests
        component.getClients().add(Protocol.FILE); // We need this to map to our styles and images

        final ServerApplication application = new ServerApplication();
        component.getDefaultHost().attach(application);

        component.start();

    }

    public VelocityManager getVelocityManager() {
        return velocityManager;
    }

    public Map<String, String> getXmlProxyCache() {
        return xmlProxyCache;
    }

    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());

        // Country service
        router.attach("/countries/{countryName}", CountryInfoResource.class);
        router.attach("/countries", CountryListResource.class);

        router.attach("/currency/{fromCountry}/{toCountry}", CurrencyProxyResource.class);

        // Proxies
        TemplateRoute xmlProxyRoute = router.attach("/proxy/xml/{url}", XMLProxyResource.class);
        xmlProxyRoute.setMatchingQuery(true);   // Allow queries
        xmlProxyRoute.getTemplate().getVariables().put("url", new Variable(Variable.TYPE_URI_ALL)); // Allow uri in parameters

        // Resources
        String classPath = getClass().getClassLoader().getResource("").getPath();
        final String protocol = "file://";
        router.attach("/images", new Directory(getContext(), protocol + classPath + "/images/"));
        router.attach("/styles", new Directory(getContext(), protocol + classPath + "/styles/"));

        return router;
    }

    public void Debug() {
        System.out.println("Contents of cache: ");
        for (String s : xmlProxyCache.keySet()) {
            System.out.println("- " + s);
        }
    }



}
