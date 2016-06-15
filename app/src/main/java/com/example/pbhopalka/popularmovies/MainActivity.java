package com.example.pbhopalka.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> posterPaths;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "This is a click", Toast.LENGTH_SHORT).show();
            }
        });

        if (isNetworkConnected())
            new FetchMovie().execute();
        else{
            Log.v(MainActivity.class.getSimpleName(), "No Internet connection");
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

        private JSONArray getMovieList(String movieJsonStr) throws JSONException{

            final String MOVIE_LIST = "results";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

            return movieArray;
        }

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
                //Log.v(LOG_TAG, array);

                try{
                    JSONArray movie = getMovieList(array);
                    return movie;
                } catch (JSONException e){

                    return null;
                }
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
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            final String POSTER_PATH = "poster_path";

            if (jsonArray == null)
                return;

            posterPaths = new ArrayList<String>();

            for(int i = 0; i < jsonArray.length(); i++){
                try{

                    JSONObject object = jsonArray.getJSONObject(i);
                    posterPaths.add(object.getString(POSTER_PATH));

                } catch (JSONException e) {

                    return;

                }
            }
            gridView.setAdapter(new ImageAdapter(getApplicationContext(), posterPaths));
        }
    }
}


