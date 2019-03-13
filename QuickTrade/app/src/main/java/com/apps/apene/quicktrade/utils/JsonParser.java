package com.apps.apene.quicktrade.utils;

import com.apps.apene.quicktrade.model.ExchangeRate;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class JsonParser {

    protected static String DATE_JSON = "date";
    protected static String RATES_JSON = "rates";
    protected static String USD_JSON = "USD";
    protected static String BASE_JSON = "base";

    public static ExchangeRate parseJSONDatabase(InputStream jsonFile) throws java.io.IOException, org.json.JSONException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonFile));
        StringBuilder jsonString = new StringBuilder();
        String line = null;
        while( (line=reader.readLine())!=null ){
            jsonString.append(line);
        }
        reader.close();

        JSONObject database = new JSONObject(jsonString.toString());

        String date = database.getString(DATE_JSON);
        Object rates = database.getJSONObject(RATES_JSON);
        String base = database.getString(BASE_JSON);
        String usd = database.getJSONObject(RATES_JSON).get(USD_JSON).toString();
        ExchangeRate exchangeRate = new ExchangeRate(date, rates, usd,base);

        return exchangeRate;
    }

}
