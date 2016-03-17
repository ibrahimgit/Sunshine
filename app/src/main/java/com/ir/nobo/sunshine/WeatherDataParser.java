package com.ir.nobo.sunshine;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.ir.nobo.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Ibrahim on 12-02-2016.
 */
public class WeatherDataParser {

    private static final String LOG_TAG = WeatherDataParser.class.getSimpleName();

    private static final String OWM_CITY = "city";
    private static final String OWM_CITY_NAME = "name";
    private static final String OWM_COORD = "coord";

    // Location coordinate
    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";

    // Weather information.  Each day's forecast info is an element of the "list" array.
    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    // All temperatures are children of the "temp" object.
    private static final String OWM_TEMPERATURE = "temp";
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_DESCRIPTION = "main";
    private static final String OWM_WEATHER_ID = "id";

    public static String[] parseWeatherData(String weatherData, String locationSetting, Context mContext) throws JSONException {
        JSONObject dataObject = new JSONObject(weatherData);

        JSONObject cityJsonObj = dataObject.getJSONObject(OWM_CITY);
        String cityName = cityJsonObj.getString(OWM_CITY_NAME);

        JSONObject coordJsonObj = cityJsonObj.getJSONObject(OWM_COORD);
        double latitude = coordJsonObj.getDouble(OWM_LATITUDE);
        double longitude = coordJsonObj.getDouble(OWM_LONGITUDE);

        long locationID = addLocation(locationSetting, cityName, latitude, longitude, mContext);

        JSONArray dataArray = dataObject.getJSONArray(OWM_LIST);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(dataArray.length());

        String[] forecast = new String[dataArray.length()];
        for (int i = 0; i < dataArray.length(); i++) {
            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            String description;
            int weatherId;

            JSONObject dayForecast = dataArray.getJSONObject(i);
            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            JSONObject temperatureObj = dayForecast.getJSONObject(OWM_TEMPERATURE);
            low = temperatureObj.getDouble(OWM_MIN);
            high = temperatureObj.getDouble(OWM_MAX);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);


            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            Date forecastDate = calendar.getTime();

            ContentValues weatherValues = new ContentValues();

            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationID);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, forecastDate.getTime());
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            cVVector.add(weatherValues);
            forecast[i] = getForecastString(low, high, description, forecastDate);
        }
        int inserted = 0;

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");


        //Log.d(LOG_TAG,forecast[i]);

        return forecast;
    }

    private static long addLocation(String locationSetting,  String cityName, double latitude, double longitude, Context mContext) {
        long locationId;

        Cursor cursorLocation = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + "=?",
                new String[]{locationSetting},
                null);

        if (cursorLocation.moveToFirst()) {
            int locationIdIndex = cursorLocation.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = cursorLocation.getLong(locationIdIndex);
            Log.d(LOG_TAG, "Existing Location Id: " + locationId + " for location setting: " + locationSetting);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, latitude);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, longitude);

            Uri insertedUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            locationId = ContentUris.parseId(insertedUri);
            Log.d(LOG_TAG, "New Location Id: " + locationId + " for location setting: " + locationSetting);
        }

        cursorLocation.close();

        return locationId;

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
