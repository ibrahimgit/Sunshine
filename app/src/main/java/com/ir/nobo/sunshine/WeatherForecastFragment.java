package com.ir.nobo.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class WeatherForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;
    private static final String LOG_TAG = WeatherForecastFragment.class.getSimpleName();

    public WeatherForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] forecastArray = {"Today - Sunny - 88/63",
                            "Tomorrow - Foggy - 70/46",
                            "Weds - Cloudy - 72/63",
                            "Thurs - Rainy - 64/51",
                            "Fri - Foggy - 70/46",
                            "Sat - Sunny - 88/63",
                            "Sun - Sunny - 83/66",
                            "Mon - Foggy - 78/61"};

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        forecastAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        WeatherForecastAsyncTask wfat = new WeatherForecastAsyncTask(forecastAdapter, getActivity());
        //wfat.execute("700014,IN");
        /*listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = forecastAdapter.getItem(position);
                Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = forecastAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_INTENT, str);
                startActivity(detailIntent);
                //Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        /*if (itemId == R.id.action_settings) {
            Toast.makeText(getActivity(), "Settings done", Toast.LENGTH_SHORT).show();
        }*/

        if(itemId == R.id.action_refresh) {
            Log.d(LOG_TAG, "I am clicked");
            WeatherForecastAsyncTask wfat = new WeatherForecastAsyncTask(forecastAdapter, getActivity());
            wfat.execute("700014,IN");
            //Toast.makeText(getActivity(), "Screen is refreshed", Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }
}
