package resources.representations.countries;

import cache.countries.CurrCountries;
import application.ServerApplication;
import application.VelocityManager;
import org.apache.velocity.Template;
import org.restlet.data.MediaType;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.*;
import resources.representations.abstracts.AbstractCountryResource;

import java.util.HashMap;
import java.util.Map;

public class CountryListResource extends AbstractCountryResource {

    private static final String COUNTRY_LIST_SERVICE_URL = "http://restcountries.eu/rest/v1/all";
    private static final String COUNTRY_NAME_FIELD = "name";

    @Get("html")
    public Representation represent() {

        try {
           Map<String, Object> dataModel = new HashMap<>(1);
           dataModel.put("countryNames", CurrCountries.getInstance().getCurrCountries());
            VelocityManager velocityManager = ((ServerApplication) getApplication()).getVelocityManager();
            Template template = velocityManager.getTemplate("templates/countrylist.vtl");
            return new TemplateRepresentation(template, dataModel, MediaType.TEXT_HTML);}
        catch (Exception e) {
            System.out.println("ERROR");
        }
        return null;
    }

    @Post
    public Representation postCountry(String name){
        System.out.println(name);
        String status = "";
        super.addCountry(name);
        if(super.addCountry(name)){
            status="Success";
            return new StringRepresentation(status);
        }
        else {
            status="Failure";
            return new StringRepresentation(status);
        }
        //super.addCountry(name);

    }
    @Delete
    public Representation deleteCountry(String name){
        System.out.println(name);
        CurrCountries.getInstance().deleteCountry(name);

        return new StringRepresentation(String.format("Successfully removed %s", name));
    }
}
