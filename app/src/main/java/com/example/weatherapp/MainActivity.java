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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetWeatherTask retrieveWeather = new GetWeatherTask();
        retrieveWeather.execute("Atlanta");

        weatherDescription = (TextView)findViewById(R.id.w_description);
        weatherTemp = (TextView)findViewById(R.id.w_temp);



    }


    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        private String URL;


        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Starting to retrieve weather");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+strings[0]+"&units=imperial&APPID=726a45e6c2ce2485aa55c8e3a46dfa04");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                //Object raw = con.getContent();
                //String rep = raw.toString();
                con.connect();


               /* BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();*/
                // Convert to a JSON object to print data
                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) con.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                // Desscription
                JsonArray weather_data = rootobj.get("weather").getAsJsonArray();//.getAsJsonObject().get("0").getAsString();//.get("description").getAsString();//.getValues().get("description").getAsString(); //just grab the zipcode
                String description = weather_data.get(0).getAsJsonObject().get("description").getAsString();
                Log.d(TAG, description+" ");

                //Temp
                JsonObject main_data = rootobj.get("main").getAsJsonObject();//.getAsJsonObject().get("0").getAsString();//.get("description").getAsString();//.getValues().get("description").getAsString(); //just grab the zipcode
                String temp = main_data.get("temp").getAsString();
                Log.d(TAG, description+" ");


                weatherDescription.setText(description);
                weatherTemp.setText(temp+" F");

            } catch (Exception e) {
                Log.d(TAG, "Error occured" + e.getLocalizedMessage());
                //printException(e);

            }
            return "";


        }

        @Override
        protected void onPostExecute(String temp) {
            Log.d(TAG, "result occured");
        }
    }
    private void printException(Exception e) throws Exception{
        throw e;
    }
}