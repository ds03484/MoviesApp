package com.example.android.moviesapp_version_2;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviesapp_version_2.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by ds034_000 on 1/31/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private MoviesAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImage;

        public MoviesAdapterViewHolder(View view){
            super(view);
            mImage = (ImageView) view.findViewById(R.id.movie_icon);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movieId = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            mClickHandler.onClick(mCursor.getInt(movieId), this);
        }
    }

    public static interface MoviesAdapterOnClickHandler {
        void onClick(int movieId, MoviesAdapterViewHolder vh);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler ch, View emptyView){
        mContext = context;
        mClickHandler = ch;
        mEmptyView = emptyView;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        if(viewGroup instanceof RecyclerView){
            int layoutId = R.layout.movie_item;

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new MoviesAdapterViewHolder(view);
        }
        else{
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position){
        mCursor.moveToPosition(position);
        String poster_path = mCursor.getString(MoviesFragment.COL_POSTER_PATH);

        String imageUrl = "http://image.tmdb.org/t/p/w185/" + poster_path;
        Picasso.with(mContext).load(imageUrl).into(moviesAdapterViewHolder.mImage);

    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

}
