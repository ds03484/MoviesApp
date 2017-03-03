package com.example.android.moviesapp_version_2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviesapp_version_2.data.MovieContract.MovieEntry;

/**
 * Created by ds034_000 on 1/9/2017.
 */

public class MoviesDB extends SQLiteOpenHelper {
    //my variables

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MoviesDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        //Create table to hold Movie Data
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL PRIMARY KEY," +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL" +
        " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
