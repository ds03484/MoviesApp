package com.example.android.moviesapp_version_2.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.moviesapp_version_2.BuildConfig;
import com.example.android.moviesapp_version_2.R;
import com.example.android.moviesapp_version_2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by ds034_000 on 1/25/2017.
 */

public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    // Interval at which to sync with movies db, in seconds.
    // 60 seconds (1 minute) * (60 * 24) * 5 = 5 days
    public static final int SYNC_INTERVAL = 60 * (60 * 24) * 5;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL;
    //private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String MovieJsonStr = null;

        try{
            //Construct the url
            final String BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIES_DB_API_KEY).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            MovieJsonStr = buffer.toString();
            getMovieDataJSON(MovieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    private void getMovieDataJSON(String json) throws JSONException {
        //take json string and parse it
        //then insert data into movies.db

        try{
            JSONObject movieJson = new JSONObject(json);
            JSONArray moviesArray = movieJson.getJSONArray("results");

            //content values to be inserted into database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for(int i=0; i< moviesArray.length(); i++){
                //values to gather from json
                int movieId;
                String title;
                String poster_path;
                String overview;
                double vote_average;
                double popularity;
                String release_date;

                JSONObject movie = moviesArray.getJSONObject(i);
                movieId = movie.getInt("id");
                title = movie.getString("original_title");
                poster_path = movie.getString("poster_path");
                overview = movie.getString("overview");
                vote_average = movie.getDouble("vote_average");
                popularity = movie.getDouble("popularity");
                release_date = movie.getString("release_date");

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);

                cVVector.add(movieValues);
            }

            //add to database
            if(cVVector.size() > 0){
                //delete old data first
                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

                //insert new data from api
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);


            }
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    //scheldule the sync adapter to insert data
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    //used to access the account services
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
