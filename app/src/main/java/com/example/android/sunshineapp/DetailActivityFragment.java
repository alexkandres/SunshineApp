package com.example.android.sunshineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //get intent from forecast via getActivity
        Intent intentFromForecast = getActivity().getIntent();
        if(intentFromForecast != null && intentFromForecast.hasExtra("weatherData")) {
            String weatherData = intentFromForecast.getStringExtra("weatherData");

            //display string in textview
            TextView textViewWeather = (TextView) rootView.findViewById(R.id.weatherData);
            textViewWeather.setText(weatherData);
        }

        return rootView;
    }
}
