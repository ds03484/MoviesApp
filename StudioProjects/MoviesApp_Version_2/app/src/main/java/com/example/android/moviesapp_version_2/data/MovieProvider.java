package com.example.android.moviesapp_version_2.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import static com.example.android.moviesapp_version_2.data.MovieContract.CONTENT_AUTHORITY;
import static com.example.android.moviesapp_version_2.data.MovieContract.MovieEntry;
import static com.example.android.moviesapp_version_2.data.MovieContract.MovieEntry.TABLE_NAME;
import static com.example.android.moviesapp_version_2.data.MovieContract.PATH_MOVIE;

/**
 * Created by ds034_000 on 1/10/2017.
 */

public class MovieProvider extends ContentProvider {
    //my variables
    private static final SQLiteQueryBuilder qBuilder;

    static{
        qBuilder = new SQLiteQueryBuilder();

        qBuilder.setTables(TABLE_NAME);
    }

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDB dbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;
    static final int MOVIE_SETTING = 102;

    //return results for the detail view based on image clicked
    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder){
        int movieId = MovieEntry.getMovieIdFromUri(uri);
        return qBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null,
                null,
                sortOrder
        );
    }

    //return results based on setting selections
    private Cursor getMovieBySetting(Uri uri, String[] projection, String sortOrder){
        String order = MovieEntry.getMovieSettingFromUri(uri);
        return qBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                order
        );
    }


    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PATH_MOVIE, MOVIE);
        matcher.addURI(authority, PATH_MOVIE + "/#", MOVIE_ID);
        matcher.addURI(authority, PATH_MOVIE + "/*", MOVIE_SETTING);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDB(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        if(match == MOVIE){
            return MovieEntry.CONTENT_TYPE;
        }
        else if(match == MOVIE_ID){
            return MovieEntry.CONTENT_ITEM_TYPE;
        }
        else if(match == MOVIE_SETTING){
            return MovieEntry.CONTENT_TYPE;
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        if(sUriMatcher.match(uri) == MOVIE){
            retCursor = dbHelper.getReadableDatabase().query(
                    MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }
        else if(sUriMatcher.match(uri) == MOVIE_ID) {
            retCursor = getMovieById(uri, projection, sortOrder);
        }
        else if(sUriMatcher.match(uri) == MOVIE_SETTING){
            retCursor = getMovieBySetting(uri, projection, sortOrder);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        if(match == MOVIE){
            long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
            if ( _id > 0 )
                returnUri = MovieEntry.buildMovieUri(_id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        if(match == MOVIE){
            rowsDeleted = db.delete(
                    MovieEntry.TABLE_NAME, selection, selectionArgs);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if(match == MOVIE){
            rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
        }
        else{
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        if(match == MOVIE){
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        }
        else{
            return super.bulkInsert(uri, values);
        }
    }

    //Will be used with testing methods to insure content provider functions correctly
    @Override
    @TargetApi(11)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
