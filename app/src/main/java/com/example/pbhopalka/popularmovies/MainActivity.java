package com.example.pbhopalka.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movieDetail_fragment) != null){
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movieDetail_fragment, new MovieDetailsFragment())
                        .commit();
            }
        }
        else
            twoPane = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
        //String EXTRA_NO_HEADERS = ":android:no_headers";
        int itemId = item.getItemId();
        if (itemId == R.id.show_favorites){
            Intent favoritesIntent = new Intent(this, FavoriteMovies.class);
            startActivity(favoritesIntent);
            return true;
        }
        if (itemId == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    SettingsActivity.GeneralPreferenceFragment.class.getName());
            settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
