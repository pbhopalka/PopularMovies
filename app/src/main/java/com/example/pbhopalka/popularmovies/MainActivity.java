package com.example.pbhopalka.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public class FetchMovie extends AsyncTask<Void, Void, JSONArray> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();


        @Override
        protected JSONArray doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String API_PARAM = "api_key";

            try{
                Uri uri = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Log.v(LOG_TAG, "Built URL: " + url.toString());


            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
        }
    }
}


