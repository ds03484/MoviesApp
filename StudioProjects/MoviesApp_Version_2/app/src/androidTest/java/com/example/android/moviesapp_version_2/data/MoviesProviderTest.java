package com.example.android.moviesapp_version_2.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.android.moviesapp_version_2.data.MovieContract.MovieEntry;

/**
 * Created by ds034_000 on 1/19/2017.
 */

public class MoviesProviderTest extends AndroidTestCase {
    public static final String LOG_TAG = MoviesProviderTest.class.getSimpleName();

    //provider delete functionality
    public void deleteRecords(){
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Movies table during delete", 0, cursor.getCount());
        cursor.close();
    }

    //1. test case delete records from movies db
    public void deleteAllRecords(){
        deleteRecords();
    }

    //delete all records before running test cases
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    //register content provider
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testBasicQuery(){
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

        assertTrue("able to Insert MovieEntry into the Database", movieRowId != -1);

        db.close();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue(movieCursor.getCount() > 0);
    }

    //setup a set of values to bulk insert
    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertValues(){
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++){
            ContentValues MovieValues = new ContentValues();
            MovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 550);
            MovieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Fight Club");
            MovieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \\\"fight clubs\\\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.");
            MovieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 8.274355);
            MovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/adw6Lq9FiC9zjYEpOqfq03ituwp.jpg");
            MovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1999-10-14");
            MovieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.1);
            returnContentValues[i] = MovieValues;
        }

        return returnContentValues;
    }

    //test case for bulk insert
    public void testBulkInsert(){
        ContentValues[] bulkInsertContentValues = createBulkInsertValues();
        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order == by DATE ASCENDING
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.close();
    }
}
