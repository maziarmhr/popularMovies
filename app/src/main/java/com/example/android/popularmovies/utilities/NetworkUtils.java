/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.utilities;

import android.net.Uri;
import com.example.android.popularmovies.BuildConfig;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    private static final String POSTER_BASE_URL =
            "http://image.tmdb.org/t/p/";
    private static final String YOUTUBE_APP_BASE_URL =
            "vnd.youtube";
    private static final String YOUTUBE_WEB_BASE_URL =
            "http://www.youtube.com";
    private static final String YOUTUBE_WEB_PATH = "watch";
    private static final String YOUTUBE_WEB_QUERY_PARAM = "v";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String TRAILERS_PATH = "videos";
    public static final String REVIEWS_PATH = "reviews";

    public enum PosterSizes {
        XXS("w92"),
        XS("w154"),
        S("w185"),
        M("w342"),
        L("w500"),
        XL("w780"),
        ORIGINAL("original");

        private final String mSize;

        /**
         * @param size of the poster
         */
        PosterSizes(final String size) {
            this.mSize = size;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return mSize;
        }
    }

    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    private static final String API_KEY_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movie server using a category.
     *
     * @param category The category for movies that will be queried for.
     * @return The URL to use to query the movies server.
     */
    public static URL buildUrl(String category) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(category)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.d(TAG, e.toString());
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Builds the URL used to get a movie trailers or reviews.
     *
     * @param id The id of movie to query the trailers for
     * @param itemType type of item to query for
     * @return The URL to use to query the movies server.
     */
    public static URL buildMovieItemUrl(long id, String itemType) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(itemType)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.d(TAG, e.toString());
        }

        Log.v(TAG, itemType + " Built URI" + url);

        return url;
    }

    /**
     *
     * @param posterPath The relative path to movie poster
     * @return The Uri to use to query the movie server.
     */
    public static Uri buildPosterUri(String posterPath, PosterSizes size) {
        return Uri.parse(POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(size.toString())
                .appendEncodedPath(posterPath)
                .build();
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Uri buildYoutubeWebUri( String videoKey) {
        return Uri.parse(YOUTUBE_WEB_BASE_URL)
                .buildUpon()
                .appendEncodedPath(YOUTUBE_WEB_PATH)
                .appendQueryParameter(YOUTUBE_WEB_QUERY_PARAM, videoKey)
                .build();
    }

    public static Uri buildYoutubeAppUri( String videoKey) {
        return Uri.parse(YOUTUBE_APP_BASE_URL + ":" + videoKey);
    }
}