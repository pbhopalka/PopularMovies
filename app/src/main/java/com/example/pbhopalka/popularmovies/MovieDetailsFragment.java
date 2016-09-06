package com.example.pbhopalka.popularmovies;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    static final String SELECTED_MOVIE = "selected";

    final String MOVIE_NAME = "title";
    final String MOVIE_POSTER = "poster_path";
    final String RELEASE_DATE = "release_date";
    final String MOVIE_RATING = "vote_average";
    final String MOVIE_OVERVIEW = "overview";
    final String MOVIE_ID = "id";

    SharedPreferences sharedPref;
    String favorites;

    TextView review, trailer1, trailer2;
    FloatingActionButton favorite;

    JSONArray movieArray;
    int arrayIndex;
    boolean currentFavorite;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w500/";

        final JSONObject movie;

        TextView movieName = (TextView) rootView.findViewById(R.id.movieName);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
        TextView releaseYear = (TextView) rootView.findViewById(R.id.year);
        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        TextView overview = (TextView) rootView.findViewById(R.id.synopsis);

        review = (TextView) rootView.findViewById(R.id.review);
        trailer1 = (TextView) rootView.findViewById(R.id.trailer1);
        trailer2 = (TextView) rootView.findViewById(R.id.trailer2);

        favorite = (FloatingActionButton) rootView.findViewById(R.id.favorite);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        favorites = sharedPref.getString("FAV_MOVIE", "");

        try {
            movieArray = new JSONArray(favorites);
        } catch (JSONException e) {
            movieArray = new JSONArray();
            //Log.v(getClass().getSimpleName(), Integer.toString(movieArray.length()));
            Log.e(getClass().getSimpleName(), "No favorites exist");
        }

        String intentExtra;

        Intent intent = getActivity().getIntent();

        if (intent == null || intent.getStringExtra("movie") == null){
            Bundle args = getArguments();
            if (args == null)
                return null;
            intentExtra = args.getString(MovieDetailsFragment.SELECTED_MOVIE);
        }
        else{
            intentExtra = intent.getStringExtra("movie");
        }

        try{

            movie = new JSONObject(intentExtra);

            Log.v(getClass().getSimpleName(), movie.toString());

            movieName.setText(movie.getString(MOVIE_NAME));
            releaseYear.setText(movie.getString(RELEASE_DATE));
            String ratingText = "Rating: " + movie.getString(MOVIE_RATING) + "/10";
            rating.setText(ratingText);
            overview.setText(movie.getString(MOVIE_OVERVIEW));

            Log.v(getClass().getSimpleName(), BASE_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER));

            fetchReview(movie.getString(MOVIE_ID));
            fetchTrailers(movie.getString(MOVIE_ID));

            Picasso.with(getActivity())
                    .load(BASE_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER)).into(poster);

            currentFavorite = false;

            if (movieArray != null) {
                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject object = movieArray.getJSONObject(i);
                    if (object.getString(MOVIE_ID).equals(movie.getString(MOVIE_ID))) {
                        currentFavorite = true;
                        arrayIndex = i;
                        break;
                    }
                }
            }

            if (currentFavorite)
                favorite.setImageResource(R.drawable.ic_favorite);
            else
                favorite.setImageResource(R.drawable.heart_white);

        } catch(JSONException e){
            e.printStackTrace();
            return rootView;
        }

        //On-click for favorites button
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFavorite){
                    favorite.setImageResource(R.drawable.heart_white);

                    JSONArray newArray = new JSONArray();

                    for (int i = 0; i < movieArray.length(); i++){
                        if (i != arrayIndex){
                            try {
                                newArray.put(movieArray.getJSONObject(i));
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    sharedPref.edit().putString("FAV_MOVIE", newArray.toString()).apply();
                    currentFavorite = false;
                }
                else{
                    favorite.setImageResource(R.drawable.ic_favorite);
                    movieArray.put(movie);

                    sharedPref.edit().putString("FAV_MOVIE", movieArray.toString()).apply();
                    currentFavorite = true;
                }
            }
        });

        //On-click for trailer 1 textview
        trailer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tag = trailer1.getTag().toString();
                    Intent watch = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + tag));
                    startActivity(watch);
                } catch (NullPointerException e){
                    Toast.makeText(getActivity(), "Trailer link is loading. Please try again after sometime",
                            Toast.LENGTH_SHORT).show();
                    Log.v(getClass().getSimpleName(), "String not yet loaded. Volley is still working");
                }
            }
        });

        //On-click for trailer 2 textview
        trailer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tag = trailer2.getTag().toString();
                    Intent watch = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + tag));
                    startActivity(watch);
                } catch (NullPointerException e){
                    Toast.makeText(getActivity(), "Trailer link is loading. Please try again after sometime",
                            Toast.LENGTH_SHORT).show();
                    Log.v(getClass().getSimpleName(), "String not yet loaded. Volley is still working");
                }
            }
        });

        return rootView;
    }

    private void fetchReview(String id){
        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews?";
        final String API_PARAM = "api_key";

        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        String url = uri.toString();
        Log.v("Url formed for Review: ", url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        extractReview(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Reviews could not be loaded",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void extractReview(String JSONString){

        try{
            JSONObject reviewText = new JSONObject(JSONString);
            JSONArray reviewArray = reviewText.getJSONArray("results");

            review.setText(reviewArray.getJSONObject(0).getString("content"));

        }catch (JSONException e){
            Log.e("Error: ", e.getMessage());
        }


    }

    private void fetchTrailers(String id){
        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/trailers?";
        final String API_PARAM = "api_key";

        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        String url = uri.toString();

        Log.v("Trailer: ", url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        extractTrailers(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Trailers could not be loaded",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void extractTrailers(String JSONString){
        try{
            JSONObject object = new JSONObject(JSONString);
            JSONArray array = object.getJSONArray("youtube");
            if (array.length() == 1)
                trailer1.setTag(array.getJSONObject(0).getString("source"));
            if (array.length() >= 2){
                trailer1.setTag(array.getJSONObject(0).getString("source"));
                trailer2.setTag(array.getJSONObject(1).getString("source"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("ExtractTrailer", "Exception in extracting trailer");
        }
    }
}
