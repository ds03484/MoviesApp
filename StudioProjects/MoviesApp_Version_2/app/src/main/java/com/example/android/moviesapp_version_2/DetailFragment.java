package com.example.android.moviesapp_version_2;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp_version_2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ds034_000 on 2/9/2017.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;

    private Uri mUri;


    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    //tie indexes to movies db columns
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_ORIGINAL_TITLE= 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_VOTE_AVERAGE = 5;
    public static final int COL_POPULARITY = 6;
    public static final int COL_RELEASE_DATE = 7;

    //views for detail layout
    private ImageView movieIconView;
    private TextView titleView;
    private TextView plotView;
    private TextView releaseDateView;
    private TextView ratingsView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        movieIconView = (ImageView) rootView.findViewById(R.id.movie_poster);
        titleView = (TextView) rootView.findViewById(R.id.original_title);
        plotView = (TextView) rootView.findViewById(R.id.plot);
        releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        ratingsView = (TextView) rootView.findViewById(R.id.user_rating);

        return rootView;
    }

    //settings for detail UI
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            String sortOrder = "";
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(sharedPref.getBoolean("popularity", true)){
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            }
            if(sharedPref.getBoolean("vote_average", true)){
                sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
            }

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            //get and set movie image
            String poster_path = data.getString(COL_POSTER_PATH);
            String imageUrl = "http://image.tmdb.org/t/p/w185/" + poster_path;


            Picasso.with(getActivity()).load(imageUrl).into(movieIconView);

            //get and set movie title
            String title = data.getString(COL_ORIGINAL_TITLE);
            titleView.setText(title);

            //get and set plot(overview)
            String overview = data.getString(COL_OVERVIEW);
            plotView.setText(overview);

            //get and set ratings
            String ratings = Double.toString(data.getDouble(COL_VOTE_AVERAGE));
            ratingsView.setText("Voting average: " + ratings);

            //get and set release date
            String date = data.getString(COL_RELEASE_DATE);
            DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;
            try {
                 date1 = inputFormatter1.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat outputFormatter1 = new SimpleDateFormat("MM-dd-yyyy");
            String output = outputFormatter1.format(date1);

            releaseDateView.setText("Movie release date: " + output);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
