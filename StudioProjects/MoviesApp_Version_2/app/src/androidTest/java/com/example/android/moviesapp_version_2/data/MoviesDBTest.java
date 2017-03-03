package com.example.android.moviesapp_version_2.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by ds034_000 on 1/9/2017.
 */
public class MoviesDBTest extends AndroidTestCase {
    public static final String LOG_TAG = MoviesDB.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDB.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable{

        //hash to keep track of all database table names
        final HashSet<String> tableNameHashSet = new HashSet<String>();

        //remove Movies database to begin testing, first step check if sqlLite is open
        mContext.deleteDatabase(MoviesDB.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDB(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //Query to check if any tables exist yet
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        //this means the movies table doesn't exist yet
        assertTrue("Error: Your database was created without the movies table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);


        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());
        db.close();

    }

    public void testMovieTable() {
        insertMovieRecord();
    }

    public long insertMovieRecord(){

        MoviesDB dbHelper = new MoviesDB(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 550);
        testValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Fight Club");
        testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.");
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 8.274355);
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/adw6Lq9FiC9zjYEpOqfq03ituwp.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1999-10-14");
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.1);

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        db.close();
        return movieRowId;
    }
}