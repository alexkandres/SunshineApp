package com.example.android.sunshineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.util.Log.i;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    String LOG_TAG = ForecastFragment.class.getSimpleName();
    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Lets fragment know that it has an option menu
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get item
        int id = item.getItemId();

        //look for menu id
        if(id == R.id.action_refresh){
            i(LOG_TAG, "Refresh clicked");
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateWeather(){
        //retrieve location zip
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zipcode = sharedPreferences.getString(SettingsActivity.LOCATION_KEY, "location");

        //create instance of FetchWeatherTask with execute
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        fetchWeatherTask.execute(zipcode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //rootview
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> stringArrayList = new ArrayList<>();

        //pass data into adapter, but dont create until user request them
        //context, xml file, textview, string array
        mForecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, stringArrayList);

        //create listview and bind arrayadapter to it
        ListView listview = (ListView) rootview.findViewById(R.id.listview_forecast);
        listview.setAdapter(mForecastAdapter);

        //put setOnItemClickListener on item in menu
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //retrieve string from item in menu
                //String weatherString = (String) adapterView.getItemAtPosition(i);
                String weatherString =  mForecastAdapter.getItem(i);
                Intent intentDetailActivity = new Intent(getActivity(), DetailActivity.class);
                intentDetailActivity.putExtra("weatherData", weatherString);
                startActivity(intentDetailActivity);
                Toast.makeText(getActivity(), weatherString, Toast.LENGTH_SHORT).show();
            }
        });

        //change getview() to rootView if necessary (inflater.inflate(R.layout.fragment_main, container, false);)
        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateWeather();
    }

    /*
        ag1 = parameters, arg2 = progress, arg3 = result
         */
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            if(strings != null) {
                //clear global arrayAdapter
                mForecastAdapter.clear();
                mForecastAdapter.addAll(strings);
            }
        }

        /* Helper method for getWeatherDataFromJson
        * The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        * ex. Tue Jul 01
        */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /* Helper method for getWeatherDataFromJson
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            //check for preference settings imperial or metric value
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unit = sharedPreferences.getString(SettingsActivity.UNIT_KEY, "");

            //get string from array
            Resources resources = getResources();
            //String[] unitArray = resources.getStringArray(R.array.pref_units_values);

            //metric value is default
            if(unit.compareTo(resources.getString(R.string.pref_units_metric)) == 0){
                return roundedHigh + "/" + roundedLow;
            }
            //imperial value
            else if(unit.compareTo(resources.getString(R.string.pref_units_imperial)) == 0){
                double farenheitHigh = (high  * 9.0/5) + 32;
                double farenheitLow = (low * 9.0/5) + 32;
                return Math.round(farenheitHigh) + "/" + Math.round(farenheitLow);
            }
            //this option should never be hit
            else{
                Toast.makeText(getContext(), "ForecastFragment:formatHighLows: Something with wrong in Unit Settings",  Toast.LENGTH_SHORT).show();
            }

            return roundedHigh + "/" + roundedLow;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);

                //returns Tue Jul 01 - Cloudy - 18/13
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;

        }

        /*
        This is the "main function" in FetchWeatherTask
         */
        @Override
        protected String[] doInBackground(String... strings) {
            //GET request url: http://api.openweathermap.org/data/2.5/forecast/daily?q=32826&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            //check for zipcode argument, if not then use default
            String zipCode;
            if(strings.length >= 1) {
                zipCode = strings[0];
            }
            else{
                zipCode = "32826";
            }
            int cnt = 7;
            String cntString = String.valueOf(cnt);

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                //URI Builder for url connection
                Uri.Builder weatherUriBuilder = new Uri.Builder();
                weatherUriBuilder.scheme("http");
                weatherUriBuilder.authority("api.openweathermap.org");
                weatherUriBuilder.path("data/2.5/forecast/daily");
                weatherUriBuilder.appendQueryParameter("q", zipCode);
                weatherUriBuilder.appendQueryParameter("mode", "json");
                weatherUriBuilder.appendQueryParameter("units", "metric");
                weatherUriBuilder.appendQueryParameter("cnt", cntString);
                weatherUriBuilder.appendQueryParameter("APPID", "2416aeb32b3e3d8360593abf67e88ddc");

                String urlStringFromBuilder = weatherUriBuilder.build().toString();

                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=32826&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc");
                URL urlFromBuilder = new URL(urlStringFromBuilder);
                urlConnection = (HttpURLConnection) urlFromBuilder.openConnection();
                urlConnection.setRequestMethod("GET");

                //this caused network thread exception(should be run as a background process)
                urlConnection.connect();

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
                   // forecastJsonStr = null;
                    return null;
                }

                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                //Log.e("PlaceholderFragment", "Error ", e);
                Log.e(LOG_TAG, "Error", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                //forecastJsonStr = null;
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                /*
                get array of weather (Tue Jul 01 - Cloudy - 18/13)
                 */
                String[] weatherArray = getWeatherDataFromJson(forecastJsonStr, cnt);
                return weatherArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


}
