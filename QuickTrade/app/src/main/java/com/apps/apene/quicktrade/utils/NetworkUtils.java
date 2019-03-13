package com.apps.apene.quicktrade.utils;
import android.util.Log;

import com.apps.apene.quicktrade.model.ExchangeRate;

import org.json.JSONException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkUtils {

    // Variables para ajustar los parámetros de conexión
    protected static String REQUEST_METHOD = "GET";
    protected static int CONNECTION_TIMEOUT = 10000;
    protected static int READ_TIMEOUT = 10000;

    /**
     * Método que gestiona las conexiones con el servidor.
     * Recibe un string con la url, inicia la conexión y devuleve un ExchangeRate con los resultados
     * */
    public static ExchangeRate loadDataFromServer (String urlString) {
        ExchangeRate results = null;
        try {
            // creamos un objeto URL
            URL url = new URL(urlString);

            // creamos un obteto HttpURLConection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // ajustamos los parámetros de conexión
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);

            // abrimos la conexión
            urlConnection.connect();
            // pasamos al ExchangeRate results el resultado del getInputStream sobre la conexión URL
            results = JsonParser.parseJSONDatabase(urlConnection.getInputStream());
            // Cerramos la conexion con el servidor
            urlConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("MainActivity", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Devolvemos el ExchangeRate results
        return results;
    }

    public static ExchangeRate loadImageFromServer (String urlString) {
        ExchangeRate results = null;
        try {
            // creamos un objeto URL
            URL url = new URL(urlString);

            // creamos un obteto HttpURLConection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // ajustamos los parámetros de conexión
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);

            // abrimos la conexión
            urlConnection.connect();
            // pasamos al ExchangeRate results el resultado del getInputStream sobre la conexión URL
            results = JsonParser.parseJSONDatabase(urlConnection.getInputStream());
            // Cerramos la conexion con el servidor
            urlConnection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("MainActivity", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Devolvemos el ExchangeRate results
        return results;
    }



}

