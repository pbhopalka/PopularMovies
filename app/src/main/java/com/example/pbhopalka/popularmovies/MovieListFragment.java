package com.example.pbhopalka.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListFragment extends Fragment {

    ArrayList<String> posterPaths;
    JSONArray movieLists;
    GridView gridView;
    ProgressBar spinner;

    static int state;
    String sorted;

    private int sPosition = gridView.INVALID_POSITION;
    String SCROLL_KEY = "scroll_key";

    public MovieListFragment() {
        // Required empty public constructor
    }

    public interface Callback{
        void onItemSelected(String selectedMovie);
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

        gridView = (GridView)rootView.findViewById(R.id.gridView);
        spinner = (ProgressBar)rootView.findViewById(R.id.progressBar);

        if (savedInstanceState != null) {
            try {

                movieLists = new JSONArray(savedInstanceState.getString("movieLists"));
                posterPaths = savedInstanceState.getStringArrayList("posterPath");
                sorted = savedInstanceState.getString("sortOrder");
                gridView.setAdapter(new ImageAdapter(getActivity(), posterPaths));
                spinner.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("SavedInstanceState", "Save Instance null");
            updateMovieList();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject movie;
                try {

                    movie = movieLists.getJSONObject(position);
                    Log.v(getActivity().getClass().getSimpleName(), movie + "");

                    ((Callback) getActivity()).onItemSelected(movie.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sPosition = position;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("OnStart", "Onstart");
        if (sorted != null) {
            Log.d("sortOrder", sorted);
            Log.d("sortOrder2", getSortOrder());
            if (!getSortOrder().equals(sorted)) {
                Log.d("SortOrderChanged", "Sort Order has changed");
                updateMovieList();
            }
        }
    }

    private String getSortOrder(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = pref.getString("sort", "popular");
        return sortOrder;
    }

    private void updateMovieList(){
        if (isNetworkConnected()){

            spinner.setVisibility(View.VISIBLE);

            String sortOrder = getSortOrder();

            //Log.d("sortOrder", sortOrder);
            if (!sortOrder.equals(sorted)) {
                sorted = sortOrder;
                execute(sorted);
            }
            else
                spinner.setVisibility(View.INVISIBLE);
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

    private JSONArray getMovieList(String movieJsonStr) throws JSONException {

        final String MOVIE_LIST = "results";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        return movieJson.getJSONArray(MOVIE_LIST);
    }

    private void updateGrid(String fromInternet){
        try{
            movieLists = getMovieList(fromInternet);
            Log.v(getClass().getSimpleName(), movieLists.toString());
        } catch (JSONException e){
            return;
        }

        final String POSTER_PATH = "poster_path";
        posterPaths = new ArrayList<>();

        for(int i = 0; i < movieLists.length(); i++){
            try{
                JSONObject object = movieLists.getJSONObject(i);
                posterPaths.add(object.getString(POSTER_PATH));
            } catch (JSONException e) {
                return;
            }
        }
        gridView.setAdapter(new ImageAdapter(getActivity(), posterPaths));

        //Log.d("ScrollPositionAgain", Integer.toString(sPosition));
        //Log.d("gridPosition", Integer.toString(gridView.INVALID_POSITION));



        spinner.setVisibility(View.INVISIBLE);
    }

    private void execute(String sortOrder){

        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + sortOrder + "?";
        final String API_PARAM = "api_key";

        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        String url = uri.toString();
        Log.v("Url formed: ", url);

        StringRequest movies = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateGrid(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(getClass().getSimpleName(), error.getMessage());
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(movies);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.d("Onsave_scrollPosition", Integer.toString(sPosition));

        if (sPosition != gridView.INVALID_POSITION)
            outState.putInt(SCROLL_KEY, sPosition);
        if (movieLists != null) {
            outState.putString("movieLists", movieLists.toString());
            outState.putStringArrayList("posterPath", posterPaths);
            outState.putString("sortOrder", sorted);
        }
        super.onSaveInstanceState(outState);
    }
}
