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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.moviesapp_version_2.data.MovieContract;
import com.example.android.moviesapp_version_2.sync.MoviesSyncAdapter;

/**
 * Created by ds034_000 on 1/31/2017.
 */

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>   {
    //log tag
    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    //adapter to tie to view
    private MoviesAdapter adapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";


    private static final int MOVIE_LOADER = 0;

    //array to represent the columns in movies db
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    //tie indexes to movies db columns
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_POSTER_PATH = 2;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri uri);
    }

    //constructor
    public MoviesFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    //settings for main UI
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    //display list of movie posters
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //populate adapter with movies data
        adapter = new MoviesAdapter(getActivity(), null, 0);

        // Get a reference to the GridView, and attach this adapter to it.
        mGridView = (GridView) rootView.findViewById(R.id.movies_gridView);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieOrderWithMovieId(cursor.getInt(COL_MOVIE_ID)));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    //restart main activity once a change has been made to settings
    void onSettingChange( ) {
        update();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void update(){
        MoviesSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        // Sort order:  Descending, by option selected in settings.
        String sortOrder = "";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPref.getBoolean("popularity", true)){
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        }
        if(sharedPref.getBoolean("vote_average", true)){
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        Log.d("MoviesFragment", sortOrder);

//        //String locationSetting = Utility.getPreferredLocation(getActivity());
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());

        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
