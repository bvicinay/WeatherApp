package com.example.weatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView weatherIcon;

    private String[] lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherDescription = (TextView)findViewById(R.id.w_description);
        weatherTemp = (TextView)findViewById(R.id.w_temp);
        weatherIcon = (ImageView)findViewById(R.id.w_image);

        GetWeatherTask retrieveWeather = new GetWeatherTask();
        retrieveWeather.execute("Atlanta");





    }

    /*
    public void notify(View view) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.clear)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        // Gets an instance of the NotificationManager service//


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(0, mBuilder.build());

    }*/

    public void notify(View view) {

        DecimalFormat df = new DecimalFormat("#.###");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String subject = "The Weather in my city is:";
        String body = "The temperature is " + lastResult[0] + " and it is " + lastResult[1] + ".";
                /*
                "Key:" + (curr.getKey())
                + "\nDate : " + (curr.getDateStr() != null ? curr.getDateStr() : "No Date")
                + "\nLocation : " + (curr.getLocation_type() != null ?
                curr.getLocation_type() : "No Type")
                + "\nZip Code : " + (curr.getZip())
                + "\nAddress : " + (curr.getStreet() != null ? curr.getStreet()
                : "No street")
                + "\nCity : " + (curr.getCity() != null ? curr.getCity() : "No city")
                + "\nBorough : " + (curr.getBorough() != null ? curr.getBorough()
                : "No Borough")
                + "\nLatitude : " + df.format(loc.getLatitude())
                + "\nLongitude : " + df.format(loc.getLongitude());*/

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(shareIntent, "Share Your Weather: "));


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
            // Logic to pick image
            String description = result[1];
            String imageType = "";  // clouds | snow | clear | rain | drizzle
            if (description.contains("clouds")) {
                imageType = "clouds";
                weatherIcon.setImageResource(R.drawable.clouds);
            } else if (description.contains("snow")) {
                imageType = "snow";
                weatherIcon.setImageResource(R.drawable.snow);
            } else if (description.contains("clear")) {
                imageType = "clear";
                weatherIcon.setImageResource(R.drawable.clear);
            } else if (description.contains("rain")) {
                imageType = "rain";
                weatherIcon.setImageResource(R.drawable.rain);
            } else if (description.contains("drizzle")) {
                weatherIcon.setImageResource(R.drawable.drizzle);

            }


        }
    }
    private void printException(Exception e) throws Exception{
        throw e;
    }
}
