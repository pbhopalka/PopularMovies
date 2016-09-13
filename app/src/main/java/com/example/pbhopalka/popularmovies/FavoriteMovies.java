package com.example.pbhopalka.popularmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteMovies extends AppCompatActivity {

    final String FAV_MOVIE = "FAV_MOVIE";
    final String MOVIE_NAME = "title";

    ArrayAdapter<String> movieArrayAdapter;
    JSONArray moviesArray;

    ListView favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        favoritesList = (ListView) findViewById(R.id.favoriteList);

        favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject object = moviesArray.getJSONObject(position);
                    Intent intent = new Intent(FavoriteMovies.this, MovieDetails.class);
                    intent.putExtra("movie", object.toString());
                    startActivity(intent);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        getMovieList();

        favoritesList.setAdapter(movieArrayAdapter);
    }

    private void getMovieList() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String JSONmovies = prefs.getString(FAV_MOVIE, "");
        ArrayList<String> moviesList;

        try {
            moviesArray = new JSONArray(JSONmovies);
            moviesList = new ArrayList<>();
            for (int i = 0; i < moviesArray.length(); i++){
                JSONObject object = moviesArray.getJSONObject(i);
                String movieName = object.getString(MOVIE_NAME);
                moviesList.add(movieName);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return;
        }

        movieArrayAdapter = new ArrayAdapter<>(this, R.layout.activity_list_view, moviesList);
    }
}
