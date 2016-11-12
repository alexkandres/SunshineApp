package com.example.android.sunshineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class DetailFragment extends Fragment {

    String weatherData;

    ShareActionProvider mShareActionProvider;
    public DetailFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //get intent from forecast via getActivity
        Intent intentFromForecast = getActivity().getIntent();
        if(intentFromForecast != null && intentFromForecast.hasExtra("weatherData")) {
            weatherData = intentFromForecast.getStringExtra("weatherData");

            //display string in textview
            TextView textViewWeather = (TextView) rootView.findViewById(R.id.weatherData);
            textViewWeather.setText(weatherData);
        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_detail, menu);

        //locate menuItem with ShareActionProvider
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);

        //retrieve ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        Toast.makeText(getActivity(),"onCreateOptionsMenu method",Toast.LENGTH_SHORT).show();

        //Create and start ShareIntent. Pass Weather data for that day
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, weatherData);
        shareIntent.setType("text/plain");

        //setting share intent with weather data
        setShareIntent(shareIntent);

    }

    public void setShareIntent(Intent shareIntent){
        if(mShareActionProvider != null){

            //setShareIntent is a private method from ShareActionProvider
            //it is not looping. ctrl + click to verify
            mShareActionProvider.setShareIntent(shareIntent);
        }
        else{
            Toast.makeText(getActivity(),"mShareActionProvider is null", Toast.LENGTH_SHORT).show();
        }
    }
}
