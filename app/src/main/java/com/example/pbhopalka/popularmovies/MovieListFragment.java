package com.example.pbhopalka.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
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

public class MovieListFragment extends Fragment {

    ArrayList<String> posterPaths;
    JSONArray movieLists;
    GridView gridView;
    ProgressBar spinner;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        View parentView = inflater.inflate(R.layout.activity_main, container, false);

        gridView = (GridView)rootView.findViewById(R.id.gridView);
        spinner = (ProgressBar)parentView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject movie = null;
                try {

                    movie = movieLists.getJSONObject(position);
                    Log.v(getActivity().getClass().getSimpleName(), movie + "");
                    Intent intent = new Intent(getActivity(), MovieDetails.class);
                    intent.putExtra("movie", movie.toString());
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        updateMovieList();

        return rootView;
    }

    private void updateMovieList(){
        if (isNetworkConnected()){

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortOrder = pref.getString("sort", "popular");

            new FetchMovie().execute(sortOrder);
            spinner.setVisibility(View.GONE);
        }
        else{
            Log.v(MainActivity.class.getSimpleName(), "No Internet connection");
            Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    /*Use Android Volley or other instead of AsyncTask */
    public class FetchMovie extends AsyncTask<String, Void, JSONArray> {

        private final String LOG_TAG = FetchMovie.class.getSimpleName();

        private JSONArray getMovieList(String movieJsonStr) throws JSONException {

            final String MOVIE_LIST = "results";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

            return movieArray;
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "?";
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

                Log.v(getClass().getSimpleName(), "Internet Connected");

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
                    Log.v(LOG_TAG, movie.toString());
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

            if (jsonArray == null){
                Toast.makeText(getActivity(), "Something is wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                return;
            }

            movieLists = new JSONArray();
            posterPaths = new ArrayList<String>();

            for(int i = 0; i < jsonArray.length(); i++){
                try{

                    JSONObject object = jsonArray.getJSONObject(i);
                    movieLists.put(object);
                    posterPaths.add(object.getString(POSTER_PATH));


                } catch (JSONException e) {

                    return;

                }
            }
            gridView.setAdapter(new ImageAdapter(getActivity(), posterPaths));
        }
    }
}
