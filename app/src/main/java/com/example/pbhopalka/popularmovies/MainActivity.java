package com.example.pbhopalka.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isNetworkConnected())
            new FetchMovie().execute();
        else{
            Toast.makeText(getApplicationContext(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }

    public class FetchMovie extends AsyncTask<Void, Void, JSONArray> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();


        @Override
        protected JSONArray doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String API_PARAM = "api_key";

            String array;

            try{
                Uri uri = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Log.v(LOG_TAG, "Built URL: " + url.toString());

                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                if (stream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line;

                while((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return null;
                }

                array = buffer.toString();
                Log.v(LOG_TAG, array);

            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (connection != null)
                    connection.disconnect();
                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException r){
                        Log.e(LOG_TAG, "Reader not closed", r);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
        }
    }
}


