package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mm090d on 10/28/2017.
 */

public class FavoriteMoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.example.android.sunshine/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.example.android.sunshine/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_FAVORITE_MOVIES = "favorites";

    private FavoriteMoviesContract() {}

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String ID = "title";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_OVERVIEW = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE = "release_date";
    }
}
