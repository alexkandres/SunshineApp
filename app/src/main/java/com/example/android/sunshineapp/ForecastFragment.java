package com.example.android.sunshineapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    String LOG_TAG = ForecastFragment.class.getSimpleName();

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

        //case switch it
        switch(id){
            case R.id.action_refresh:   Log.i(LOG_TAG, "Refresh clicked");
                                        //create instance of FetchWeatherTask with execute
                                        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
                                        fetchWeatherTask.execute();
                                        return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //rootview
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> stringArrayList = new ArrayList<>();

        //raw data
        stringArrayList.add("Today - Sunny - 88/63");
        stringArrayList.add("Tomorrow - Foggy - 70/46");
        stringArrayList.add("Weds - Cloudy` - 72/63");
        stringArrayList.add("Thurs - Rainy - 64/51");
        stringArrayList.add("Fri - Foggy - 70/46");
        stringArrayList.add("Sat - Sunny - 76/68");

        //pass data into adapter, but dont create until user request them
        //context, xml file, textview
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview, stringArrayList);

        //create listview and bind arrayadapter to it
        ListView listview = (ListView) rootview.findViewById(R.id.listview_forecast);
        listview.setAdapter(stringArrayAdapter);

        //change getview() to rootView if necessary (inflater.inflate(R.layout.fragment_main, container, false);)
        return rootview;
    }

    //this actuvoity may be terminated quick, use a service->SyncAdapter without a UI
    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        @Override
        protected Void doInBackground(String... strings) {
            //GET request url: http://api.openweathermap.org/data/2.5/forecast/daily?q=32826&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                //check for zipcode argument, if not then use default
                String zipCode = "";
                if(strings.length >= 1) {
                    zipCode = strings[0];
                }
                else{
                    zipCode = "32826";
                }

                //URI Builder for url connection
                Uri.Builder weatherUriBuilder = new Uri.Builder();
                weatherUriBuilder.scheme("http");
                weatherUriBuilder.authority("api.openweathermap.org");
                weatherUriBuilder.path("data/2.5/forecast/daily");
                weatherUriBuilder.appendQueryParameter("q", zipCode);
                weatherUriBuilder.appendQueryParameter("mode", "json");
                weatherUriBuilder.appendQueryParameter("units", "metric");
                weatherUriBuilder.appendQueryParameter("cnt", "7");
                weatherUriBuilder.appendQueryParameter("APPID", "2416aeb32b3e3d8360593abf67e88ddc");

                String urlStringFromBuilder = weatherUriBuilder.build().toString();
                Log.i(LOG_TAG, "tostring " + urlStringFromBuilder);
                URL urlFromBuilder = new URL(urlStringFromBuilder);
                urlConnection = (HttpURLConnection) urlFromBuilder.openConnection();
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=32826&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc");
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
                Log.i(LOG_TAG, forecastJsonStr);

                //argument for zipcode via exucute();
                //Log.i(LOG_TAG, strings[0]);
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
            //is this necessary??
            return null;
        }

    }


}
