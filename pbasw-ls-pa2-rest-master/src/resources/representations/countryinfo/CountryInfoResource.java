package resources.representations.countryinfo;

import cache.countries.CurrCountries;
import api.remote.restcountries.RestCountriesCountry;
import org.apache.velocity.Template;
import org.restlet.data.MediaType;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import resources.representations.abstracts.AbstractCountryResource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class CountryInfoResource  extends AbstractCountryResource {


    @Get("html")
    public Representation represent() {
        String countryName = (String) getRequest().getAttributes().get("countryName");

        String decodedName = "";
        try {
            decodedName = URLDecoder.decode(countryName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RestCountriesCountry country = CurrCountries.getInstance().getCountry(decodedName);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("country", country);
        dataModel.put("message", "");

        Template template = super.getVelocityManager().getTemplate("templates/countryinfo.vtl");
        return new TemplateRepresentation(template, dataModel, MediaType.TEXT_HTML);
    }


}
