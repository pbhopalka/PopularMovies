package com.example.pbhopalka.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.*;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class MovieDetails extends AppCompatActivity {

    final String MOVIE_NAME = "title";
    final String MOVIE_POSTER = "poster_path";
    final String RELEASE_DATE = "release_date";
    final String MOVIE_RATING = "vote_average";
    final String MOVIE_OVERVIEW = "overview";
    final String MOVIE_ID = "id";

    TextView review, trailer1, trailer2;

    ListView reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w500/";

        JSONObject movie;
        TextView movieName = (TextView) findViewById(R.id.movieName);
        ImageView poster = (ImageView) findViewById(R.id.poster);
        TextView releaseYear = (TextView) findViewById(R.id.year);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView overview = (TextView) findViewById(R.id.synopsis);

        review = (TextView) findViewById(R.id.review);
        trailer1 = (TextView) findViewById(R.id.trailer1);
        trailer2 = (TextView) findViewById(R.id.trailer2);

        final FloatingActionButton favorite = (FloatingActionButton) findViewById(R.id.favorite);

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite.setImageResource(R.drawable.heart);
                Toast.makeText(getApplicationContext(), "You have liked the movie", Toast.LENGTH_SHORT).show();
            }
        });

        try{
            movie = new JSONObject(getIntent().getStringExtra("movie"));

            //Log.v(getClass().getSimpleName(), movie.getString(MOVIE_OVERVIEW) + "");
            movieName.setText(movie.getString(MOVIE_NAME));
            releaseYear.setText(movie.getString(RELEASE_DATE));
            String ratingText = "Rating: " + movie.getString(MOVIE_RATING) + "/10";
            rating.setText(ratingText);
            overview.setText(movie.getString(MOVIE_OVERVIEW));

            Log.v(getClass().getSimpleName(), BASE_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER));

            fetchReview(movie.getString(MOVIE_ID));
            fetchTrailers(movie.getString(MOVIE_ID));

            Picasso.with(getApplicationContext())
                    .load(BASE_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER)).into(poster);

        } catch(JSONException e){
            e.printStackTrace();
            return;
        }

        trailer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tag = trailer1.getTag().toString();
                    Intent watch = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + tag));
                    startActivity(watch);
                } catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(), "Trailer link is loading. Please try again after sometime",
                            Toast.LENGTH_SHORT).show();
                    Log.v(getClass().getSimpleName(), "String not yet loaded. Volley is still working");
                }
            }
        });

        trailer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tag = trailer2.getTag().toString();
                    Intent watch = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + tag));
                    startActivity(watch);
                } catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(), "Trailer link is loading. Please try again after sometime",
                            Toast.LENGTH_SHORT).show();
                    Log.v(getClass().getSimpleName(), "String not yet loaded. Volley is still working");
                }
            }
        });
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
                        Toast.makeText(getApplicationContext(), "Reviews could not be loaded",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
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
                        Toast.makeText(getApplicationContext(), "Trailers could not be loaded",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void extractTrailers(String JSONString){
        try{
            JSONObject object = new JSONObject(JSONString);
            JSONArray array = object.getJSONArray("youtube");

            if (array.length() >= 2){
                trailer1.setTag(array.getJSONObject(0).getString("source"));
                trailer2.setTag(array.getJSONObject(1).getString("source"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
}
