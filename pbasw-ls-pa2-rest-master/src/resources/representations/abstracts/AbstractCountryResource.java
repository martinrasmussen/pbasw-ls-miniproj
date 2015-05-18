package resources.representations.abstracts;

import cache.countries.CurrCountries;
import api.remote.restcountries.RestCountriesCountry;
import api.remote.restcountries.RestCountryCountries;
import application.ServerApplication;
import application.VelocityManager;
import org.restlet.resource.ServerResource;

/**
 * Created by Martin Rasmussen on 12/05/2015.
 */
public abstract class AbstractCountryResource extends ServerResource{

    protected VelocityManager getVelocityManager(){
        return ((ServerApplication) getApplication()).getVelocityManager();
    }

    protected boolean addCountry(String name){

        try {
            RestCountriesCountry country = RestCountryCountries.getCountry(name);
            CurrCountries.getInstance().putCountry(country);
            return true;
        } catch (Exception e){
            return false;
        }

    }

    protected RestCountriesCountry getCountry(String name){
        return CurrCountries.getInstance().getCountry(name);
    }


}
