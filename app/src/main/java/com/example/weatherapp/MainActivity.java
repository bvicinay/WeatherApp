package com.example.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Main Screen";

    private TextView weatherDescription;
    private TextView weatherTemp;

    private String[] lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherDescription = (TextView)findViewById(R.id.w_description);
        weatherTemp = (TextView)findViewById(R.id.w_temp);

        GetWeatherTask retrieveWeather = new GetWeatherTask();
        retrieveWeather.execute("Atlanta");





    }


    private class GetWeatherTask extends AsyncTask<String, Void, String[]> {
        private String URL;


        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Starting to retrieve weather");
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String description = "";
            String temp = "";
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+strings[0]+"&units=imperial&APPID=726a45e6c2ce2485aa55c8e3a46dfa04");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();

                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) con.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                // Desscription
                JsonArray weather_data = rootobj.get("weather").getAsJsonArray();//.getAsJsonObject().get("0").getAsString();//.get("description").getAsString();//.getValues().get("description").getAsString(); //just grab the zipcode
                description = weather_data.get(0).getAsJsonObject().get("description").getAsString();
                Log.d(TAG, description+" ");

                //Temp
                JsonObject main_data = rootobj.get("main").getAsJsonObject();//.getAsJsonObject().get("0").getAsString();//.get("description").getAsString();//.getValues().get("description").getAsString(); //just grab the zipcode
                temp = main_data.get("temp").getAsString();
                Log.d(TAG, description+" ");




            } catch (Exception e) {
                Log.d(TAG, "Error occured" + e.getLocalizedMessage());
                //printException(e);

            }
            lastResult =  new String[] {temp, description};
            return lastResult;


        }

        @Override
        protected void onPostExecute(String[] result) {
            Log.d(TAG, "result occured");
            weatherDescription.setText(result[1]);
            weatherTemp.setText(result[0]+" F");
        }
    }
    private void printException(Exception e) throws Exception{
        throw e;
    }
}
