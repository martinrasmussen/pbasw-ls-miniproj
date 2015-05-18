/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cache.countries;

import api.remote.restcountries.RestCountriesCountry;
import api.remote.restcountries.RestCountryCountries;

import java.util.HashMap;
import java.util.Map;


public class CurrCountries {
    private static CurrCountries instance=null;
    private final Map <String,RestCountriesCountry> currCountries;
        
    protected CurrCountries(){
        currCountries= new HashMap<>();
        String country = "Denmark";
        currCountries.put(country, RestCountryCountries.getCountry(country));
    }
    
    /*
    *
    * This method creates a new instance of the class if none exists or returns a reference to
    * the object already created
    */
    public static CurrCountries getInstance() {
      if(instance == null) {
         instance = new CurrCountries();
      }
      return instance;
    }
    
    public Map getCurrCountries(){
        return currCountries;
    }

    /**
     * 
     * @param name
     * @return the class of a country with its data
     */
    public RestCountriesCountry getCountry(String name){
        return (RestCountriesCountry)currCountries.get(name);
    }    
    
    public void putCountry(RestCountriesCountry country){
        currCountries.put(country.getName(), country);
    }
    
    public void deleteCountry(String name){
        currCountries.remove(name);
    }
}
