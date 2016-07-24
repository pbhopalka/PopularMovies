package com.example.pbhopalka.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetails extends AppCompatActivity {

    final String MOVIE_NAME = "title";
    final String MOVIE_POSTER = "poster_path";
    final String RELEASE_DATE = "release_date";
    final String MOVIE_RATING = "vote_average";
    final String MOVIE_OVERVIEW = "overview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w185/";

        JSONObject movie;
        TextView movieName = (TextView) findViewById(R.id.movieName);
        ImageView poster = (ImageView) findViewById(R.id.poster);
        TextView releaseYear = (TextView) findViewById(R.id.year);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView overview = (TextView) findViewById(R.id.synopsis);

        try{
            movie = new JSONObject(getIntent().getStringExtra("movie"));

            //Log.v(getClass().getSimpleName(), movie.getString(MOVIE_OVERVIEW) + "");
            movieName.setText(movie.getString(MOVIE_NAME));
            releaseYear.setText(movie.getString(RELEASE_DATE));
            rating.setText("Rating: " + movie.getString(MOVIE_RATING) + "/10");
            overview.setText(movie.getString(MOVIE_OVERVIEW));

            Log.v(getClass().getSimpleName(), BASE_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER));

            Picasso.with(getApplicationContext())
                    .load(BASE_URL + POSTER_SIZE + movie.getString(MOVIE_POSTER)).into(poster);

        } catch(JSONException e){
            e.printStackTrace();
            return;
        }
    }
}
