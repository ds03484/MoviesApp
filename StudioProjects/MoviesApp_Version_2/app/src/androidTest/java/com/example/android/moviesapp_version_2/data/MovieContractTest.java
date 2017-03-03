package com.example.android.moviesapp_version_2.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by ds034_000 on 1/9/2017.
 */

public class MovieContractTest extends AndroidTestCase {
    private static final String TEST_MOVIE_URI = "/popularity";

    public void testBuildMovieUri(){
        Uri movieUri = MovieContract.MovieEntry.buildMovieOrder(TEST_MOVIE_URI);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieOrder in " +
                        "MovieContract.",
                movieUri);

        assertEquals("Error: Movie Uri not properly appended to the end of the Uri",
                TEST_MOVIE_URI, movieUri.getLastPathSegment());

        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.example.android.moviesapp_version_2.app/movie/%2Fpopularity");
    }
}