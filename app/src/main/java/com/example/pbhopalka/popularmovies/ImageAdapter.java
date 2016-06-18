package com.example.pbhopalka.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by pbhopalka on 15/6/16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> posterPath;

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    public ImageAdapter(Context c, ArrayList<String> posterPath){
        mContext = c;
        this.posterPath = posterPath;
    }

    @Override
    public int getCount() {
        return posterPath.size();
    }

    @Override
    public Object getItem(int position) {
        return posterPath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w185/";

        if (convertView != null)
            imageView = (ImageView) convertView;
        else{
            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0,0,0,0);
        }
        //Log.v(LOG_TAG, BASE_URL + POSTER_SIZE + posterPath.get(position));
        Picasso.with(mContext).load(BASE_URL + POSTER_SIZE + posterPath.get(position)).into(imageView);

        return imageView;
    }
}
