/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Collectors;

public class Service {

    private final String weatherApiKey;
    private final Gson gson;
    private final Locale locale;


    public Service(String country){
        this.locale = getLocale(country);
        this.weatherApiKey = "99f643c4611765cb5309de80089111bb";
        this.gson = new Gson();
    }


    private Locale getLocale(String country){
        for(Locale locale : Locale.getAvailableLocales()) {
            if(locale.getDisplayCountry(Locale.ENGLISH).equals(country) ||
               locale.getDisplayCountry(Locale.getDefault()).equals(country)) {
                return locale;
            }
        }
        return null;
    }



    public String getWeather(String miasto) {

        String weatherUrlString = "https://api.openweathermap.org/data/2.5/weather?q="
                + miasto + "," + locale.getCountry() + "&appid=" + weatherApiKey + "&units=metric";

        return getJsonFromUrl(weatherUrlString);
    }


    public Double getRateFor(String kod_waluty) {

        String currencyUrlString = "https://open.er-api.com/v6/latest/" + Currency.getInstance(this.locale);

        String json = getJsonFromUrl(currencyUrlString);

        if (json == null || json.isEmpty())
            return null;

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        if (jsonObject.has("rates") && jsonObject.getAsJsonObject("rates").has(kod_waluty)) {
            return (Double) jsonObject.getAsJsonObject("rates").get(kod_waluty).getAsDouble();
        }

        return null;
    }


    public Double getNBPRate() {

        if(String.valueOf(Currency.getInstance(locale)).equals("PLN")){
            return (Double) 1.0;
        }

        //A
        String nbpUrlString = "https://api.nbp.pl/api/exchangerates/rates/A/" + Currency.getInstance(locale) + "?format=json";
        String json = getJsonFromUrl(nbpUrlString);

        if(json != null && !json.isEmpty()){
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            return (Double) jsonObject.getAsJsonArray("rates").get(0).getAsJsonObject().get("mid").getAsDouble();
        }

        //B
        nbpUrlString = "https://api.nbp.pl/api/exchangerates/rates/B/" + Currency.getInstance(locale) + "?format=json";
        json = getJsonFromUrl(nbpUrlString);

        if(json != null && !json.isEmpty()){
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            return (Double) jsonObject.getAsJsonArray("rates").get(0).getAsJsonObject().get("mid").getAsDouble();
        }

        //C
        nbpUrlString = "https://api.nbp.pl/api/exchangerates/rates/C/" + Currency.getInstance(locale) + "?format=json";
        json = getJsonFromUrl(nbpUrlString);

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        return (Double) jsonObject.getAsJsonArray("rates").get(0).getAsJsonObject().get("mid").getAsDouble();
    }


    private String getJsonFromUrl(String urlString) {
        String json = "";

        try ( InputStream inputStream = new URL(urlString).openStream() ) {

            json = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return json;
    }


    public String getServiceCurrency() {
        return String.valueOf(Currency.getInstance(locale));
    }

}  
