package com.example.android.sunshineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static android.content.Intent.ACTION_VIEW;

public class ForecastActivity extends AppCompatActivity {

    private final String  LOG_TAG = ForecastActivity.class.getSimpleName();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forecast_activity, menu);
        return true;
    }

    public void updateMap(){
        //get preference from getshared preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String zipCodePreference = sharedPreferences.getString(SettingsActivity.LOCATION_KEY, "");
        Uri uri = Uri.parse("geo:0,0?q="+zipCodePreference);

        Intent intent = new Intent();
        //Set ACTION for app to resolve implicit intent
        intent.setAction(ACTION_VIEW);

        //Set uri of location
        intent.setData(uri);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
    //define onclick action for item in menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(LOG_TAG, "Action settings was clicked");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.map){
            Log.i(LOG_TAG, "Map was clicked");
            updateMap();
            return true;
        }
        else if (id == R.id.menu_item_share){
            Log.i(LOG_TAG, "Test was clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //message button with snackbar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //snackbar is similar to a toast
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //add context menu to settings in menu option
        //ImageButton settings = (ImageButton) findViewById(R.id.action_settings);
        //View settings = findViewById(R.id.action_settings);
        //registerForContextMenu(settings);
    }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //inflate context menu defined in res/menu/context_menu.xml
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }*/

    //this inflates the appbar/option menu defined in xml

}