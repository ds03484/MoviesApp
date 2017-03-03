package com.example.android.moviesapp_version_2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by ds034_000 on 1/31/2017.
 */

public class MoviesAdapter extends CursorAdapter {


    public static class ViewHolder {
        public final ImageView movieImage;

        public ViewHolder(View view){
            movieImage = (ImageView) view.findViewById(R.id.movie_icon);
        }
    }

    public MoviesAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.movie_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        String poster_path = cursor.getString(MoviesFragment.COL_POSTER_PATH);
        String imageUrl = "http://image.tmdb.org/t/p/w185/" + poster_path;

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(imageUrl).into(viewHolder.movieImage);

    }
}
