package com.ir.nobo.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_SHORT);
        /*if(savedInstanceState != null) {
            Toast.makeText(getApplicationContext(), "Ibrahim Rashid Ibrahim", Toast.LENGTH_LONG);
            getSupportFragmentManager().beginTransaction().add(R.id.container, new WeatherForecastFragment()).commit();
        }*/
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
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Settings menu selected", Toast.LENGTH_SHORT).show();
        }

        /*if (id == R.id.action_refresh) {
            Toast.makeText(getApplicationContext(), "Refresh menu selected", Toast.LENGTH_SHORT).show();
        }*/

        return super.onOptionsItemSelected(item);
    }


}
