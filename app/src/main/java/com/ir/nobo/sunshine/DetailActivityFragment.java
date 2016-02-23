package com.ir.nobo.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String SUNSHINE_HASHTAG = "#sunshine";
    private String forecastStr;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        TextView tv = (TextView)rootView.findViewById(R.id.forecast_details);
        forecastStr = intent.getStringExtra(Intent.EXTRA_INTENT);
        tv.setText(forecastStr);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);
        MenuItem mi = menu.findItem(R.id.action_share);

        ShareActionProvider sap = (ShareActionProvider)MenuItemCompat.getActionProvider(mi);

        if (sap != null) {
            sap.setShareIntent(getForecastSharingIntent());
        } else
            Toast.makeText(getActivity(), "Share Action Provider is Null", Toast.LENGTH_LONG).show();
        //super.onCreateOptionsMenu(menu, inflater);
    }

    private Intent getForecastSharingIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, forecastStr + SUNSHINE_HASHTAG);
        return intent;
    }
}
