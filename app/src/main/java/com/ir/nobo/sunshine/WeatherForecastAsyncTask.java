package com.ir.nobo.sunshine;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;

/**
 * Created by Ibrahim on 11-02-2016.
 */
public class WeatherForecastAsyncTask extends AsyncTask<String, String, String[]> {

    public static final String LOG_NAME = WeatherForecastAsyncTask.class.getSimpleName();
    private  ArrayAdapter<String> forecastAdapter;
    private Context context;

    WeatherForecastAsyncTask(ArrayAdapter<String> forecastAdapter, Context context) {
        this.forecastAdapter = forecastAdapter;
        this.context = context;
    }

    @Override
    protected String[] doInBackground(String... params) {
        Log.d(LOG_NAME,"doInBackground");
        return  getForecastWeather(params[0], params[1]);
    }

    private String[] getForecastWeather(String postalCode, String format) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        String[] forecastStringArray = null;
        //String format = "metric";
        String cnt = "7";
        String appid = "907f14649dd7564b21d2d4a61d954414";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
            final String QUERY_PARAM = "q";
            final String COUNT_PARAM = "cnt";
            final String APPID_PARAM = "appid";
            final String FORMAT_PARAM = "units";

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,postalCode)
                    .appendQueryParameter(FORMAT_PARAM,format)
                    .appendQueryParameter(COUNT_PARAM, cnt)
                    .appendQueryParameter(APPID_PARAM, appid).build();

            /*URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=700014,IN&units=metric&cnt=7&appid=907f14649dd7564b21d2d4a61d954414");*/
            URL url = new URL(uri.toString());
            Log.d(LOG_NAME, "URI: " + url.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            publishProgress("Connecting the internet");
            urlConnection.connect();
            publishProgress("Connection done, Retrieving the data");
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_NAME, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_NAME, "Error closing stream", e);
                }
            }
        }
        Log.d(LOG_NAME, forecastJsonStr);
        try {
            forecastStringArray = WeatherDataParser.parseWeatherData(forecastJsonStr);
            Log.d(LOG_NAME, forecastStringArray.toString());
        } catch (JSONException je) {
            Log.e(LOG_NAME,"JSON Exception: ", je);
        }
        return forecastStringArray;
    }

    @Override
    protected void onProgressUpdate(String... message) {
       Toast.makeText(context, message[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String[] forecastDataArray) {
        if(forecastDataArray != null) {
            forecastAdapter.clear();
            for(String forecastString : forecastDataArray) {
                forecastAdapter.add(forecastString);
            }
        }
    }
}
