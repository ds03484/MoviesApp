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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.moviesapp_version_2.data.MovieContract;
import com.example.android.moviesapp_version_2.sync.MoviesSyncAdapter;

/**
 * Created by ds034_000 on 1/31/2017.
 */

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener  {
    //log tag
    public static final String LOG_TAG = MoviesFragment.class.getSimpleName();


    //variables needed to hook up the recyclerView
    private MoviesAdapter adapter;
    private RecyclerView  mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

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


        /*
            The next few lines of code will populate the movies grid with data
        */
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        View emptyView = rootView.findViewById(R.id.movie_gridview_empty);

        mRecyclerView.setHasFixedSize(true);

        adapter = new MoviesAdapter(getActivity(), new MoviesAdapter.MoviesAdapterOnClickHandler() {
            @Override
            public void onClick(int movieId, MoviesAdapter.MoviesAdapterViewHolder vh) {
                ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieOrderWithMovieId(movieId));
                mPosition = vh.getAdapterPosition();
            }
        }, emptyView);

        mRecyclerView.setAdapter(adapter);

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
        // When no item is selected, mPosition will be set to RecyclerView.NO_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    //restart main activity once a change has been made to settings
    void onSettingChange( ) {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
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
            //mGridView.smoothScrollToPosition(mPosition);
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    //update empty view based upon any error message returned
    private void updateEmptyView(){
        if(adapter.getItemCount() == 0){
            TextView view = (TextView) getView().findViewById(R.id.movie_gridview_empty);
            if(null != view){
                int message = R.string.empty_movie_list;
                @MoviesSyncAdapter.Status int status = Utility.getStatus(getActivity());
                switch(status){
                    case MoviesSyncAdapter.STATUS_SERVER_DOWN:
                        message = R.string.empty_movie_list_server_down;
                        break;
                    case MoviesSyncAdapter.STATUS_SERVER_INVALID:
                        message = R.string.empty_movie_list_server_error;
                        break;
                    default:
                        if(!Utility.isNetworkAvailable(getActivity())){
                            message = R.string.empty_movie_list_no_network;
                        }
                }
                view.setText(message);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.pref_status_key)) ) {
            updateEmptyView();
        }
    }

}
