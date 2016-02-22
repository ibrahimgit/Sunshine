package com.ir.nobo.sunshine;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ibrahim on 12-02-2016.
 */
public class WeatherDataParser {

    private static final String LOG_TAG = WeatherDataParser.class.getSimpleName();

    private static final String LIST = "list";
    private static final String TEMP = "temp";
    private static final String WEATHER = "weather";
    private static final String DESCRIPTION = "description";
    private static final String MIN = "min";
    private static final String MAX = "max";

    public static String[] parseWeatherData(String weatherData) throws JSONException {
        JSONObject dataObject = new JSONObject(weatherData);
        JSONArray dataArray = dataObject.getJSONArray(LIST);

        String[] forecast = new String[dataArray.length()];
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.optJSONObject(i);
            JSONObject dayForecast = item.getJSONObject(TEMP);
            JSONObject weatherForecast = item.getJSONArray(WEATHER).getJSONObject(0);

            String description = weatherForecast.getString(DESCRIPTION);
            double minTemp = dayForecast.getDouble(MIN);
            double maxTemp = dayForecast.getDouble(MAX);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,i);
            Date forecastDate = calendar.getTime();

            forecast[i] = getForecastString(minTemp, maxTemp, description, forecastDate);
            Log.d(LOG_TAG,forecast[i]);
        }
        return forecast;
    }

    private static String getForecastString(double minTemp, double maxTemp, String description, Date forecastDate){
        final String stringSeparator = " - ";
        String dateString = getDateString(forecastDate);
        String tempFormatString = getTempForematString(minTemp, maxTemp);
        StringBuilder stringBuilder = new StringBuilder(dateString);
        stringBuilder.append(stringSeparator).append(description).append(stringSeparator).append(tempFormatString);
        return stringBuilder.toString();
    }

    private static String getTempForematString(double minTemp, double maxTemp) {
        return Math.round(maxTemp) + "/" + Math.round(minTemp);
    }

    private static String getDateString(Date forecastDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd");
        return sdf.format(forecastDate);
    }
}
