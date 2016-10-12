package com.example.android.sunshineapp;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by alex on 10/10/2016.
 */
public class WeatherDataParser {

    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here
        JSONObject jsonObject = new JSONObject(weatherJsonStr);
        String city = jsonObject.getString("cnt");
        //System.out.println("cnt = " + city);

        //String[] commaSplit = weatherJsonStr.split(",");
        //System.out.println(weatherJsonStr);
        //System.out.println("commaSplit = " + commaSplit[4]);
        return -1;
    }
}
