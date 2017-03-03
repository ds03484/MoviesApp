package com.example.android.moviesapp_version_2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ds034_000 on 1/9/2017.
 */

public class MovieContract {
    //my variables

    //Apps content authority name
    public static final String CONTENT_AUTHORITY = "com.example.android.moviesapp_version_2.app";

    //content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";


        // movie id returned by API
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Movie title
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        //Movie image
        public static final String COLUMN_POSTER_PATH = "poster_path";

        //Movie overview
        public static final String COLUMN_OVERVIEW = "overview";

        //Movie average rating
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //Movie release date
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //Movie popularity
        public static final String COLUMN_POPULARITY = "popularity";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //build URI based on user setting choice
        public static Uri buildMovieOrder(String movieSetting) {
            return CONTENT_URI.buildUpon().appendPath(movieSetting).build();
        }

        public static int getMovieIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
        public static String getMovieSettingFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static Uri buildMovieOrderWithMovieId(int movieId){
           return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }
    }
}
