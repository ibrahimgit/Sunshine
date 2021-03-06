package com.ir.nobo.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "On Create");
        //Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_SHORT);
        /*if(savedInstanceState != null) {
            Toast.makeText(getApplicationContext(), "Ibrahim Rashid Ibrahim", Toast.LENGTH_LONG);
            getSupportFragmentManager().beginTransaction().add(R.id.container, new WeatherForecastFragment()).commit();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "On Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "On Resume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "On Pause");

    }

    @Override
    protected void onStop() {
        super.onPause();
        Log.d(LOG_TAG, "On Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "On Destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)  {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_map) {
            openPreferredLocationInMap();
        }

        /*if (id == R.id.action_refresh) {
            Toast.makeText(getApplicationContext(), "Refresh menu selected", Toast.LENGTH_SHORT).show();
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sp.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Map couldn't launch", Toast.LENGTH_LONG).show();
        }

    }


}
