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

import android.util.Log;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility functions to handle TheMovieDb JSON data.
 */
public final class MovieDbJsonUtils {

    /**
     * This method parses JSON from a web response and returns a list of Movie objects
     * describing the movie.
     *
     * @param movieJsonStr JSON response from server
     *
     * @return List of Movies for the category
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movie> getMovieFromJson(String movieJsonStr)
            throws JSONException {

        final String MDB_LIST = "results";

        final String MDB_ID = "id";
        final String MDB_ORG_TITLE = "original_title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_VOTE_AVERAGE = "vote_average";
        final String MDB_POSTER_PATH = "poster_path";

        final String MDB_MESSAGE_CODE = "status_code";

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(MDB_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(MDB_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

        List<Movie> parsedMovieList = new ArrayList<>();

        for (int i = 0; i < movieArray.length(); i++) {
            try {
                long id;
                String title;
                String overview;
                double rating;
                Date releaseDate;
                String poster_path;

                JSONObject movieObject = movieArray.getJSONObject(i);

                id = movieObject.getLong(MDB_ID);
                title = movieObject.getString(MDB_ORG_TITLE);
                overview = movieObject.getString(MDB_OVERVIEW);
                rating = movieObject.getDouble(MDB_VOTE_AVERAGE);
                String date = movieObject.getString(MDB_RELEASE_DATE);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                releaseDate = format.parse(date);
                poster_path = movieObject.getString(MDB_POSTER_PATH);
                Log.d("Maz", Long.toString(id));
                parsedMovieList.add(new Movie(id, title, overview, rating, releaseDate, poster_path));
            } catch(Exception e) {
                Log.d(MovieDbJsonUtils.class.getSimpleName(), e.getMessage());
            }
        }

        return parsedMovieList;
    }

    /**
     * This method parses JSON from a web response and returns a list of Trailer objects
     * for a movie.
     *
     * @param trailerJsonStr JSON response from server
     *
     * @return List of Trailers for the Movie
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Trailer> getTrailerFromJson(String trailerJsonStr)
            throws JSONException {

        final String MDB_LIST = "results";

        final String MDB_ID = "id";
        final String MDB_NAME = "name";
        final String MDB_KEY = "key";

        final String MDB_MESSAGE_CODE = "status_code";

        JSONObject trailersJson = new JSONObject(trailerJsonStr);

        /* Is there an error? */
        if (trailersJson.has(MDB_MESSAGE_CODE)) {
            int errorCode = trailersJson.getInt(MDB_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray trailersArray = trailersJson.getJSONArray(MDB_LIST);

        List<Trailer> parsedTrailerList = new ArrayList<>();

        for (int i = 0; i < trailersArray.length(); i++) {
            try {
                String name;
                String key;

                JSONObject trailerObject = trailersArray.getJSONObject(i);

                name = trailerObject.getString(MDB_NAME);
                key = trailerObject.getString(MDB_KEY);
                parsedTrailerList.add(new Trailer( name, key));
            } catch(Exception e) {
                Log.d(MovieDbJsonUtils.class.getSimpleName(), e.getMessage());
            }
        }

        return parsedTrailerList;
    }

    /**
     * This method parses JSON from a web response and returns a list of Review objects
     * for a movie.
     *
     * @param reviewJsonStr JSON response from server
     *
     * @return List of Reviews for the Movie
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Review> getReviewsFromJson(String reviewJsonStr)
            throws JSONException {

        final String MDB_LIST = "results";

        final String MDB_ID = "id";
        final String MDB_AUTHOR = "author";
        final String MDB_CONTENT = "content";

        final String MDB_MESSAGE_CODE = "status_code";

        JSONObject reviewsJson = new JSONObject(reviewJsonStr);

        /* Is there an error? */
        if (reviewsJson.has(MDB_MESSAGE_CODE)) {
            int errorCode = reviewsJson.getInt(MDB_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray reviewsArray = reviewsJson.getJSONArray(MDB_LIST);

        List<Review> parsedReviewList = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            try {
                String author;
                String content;

                JSONObject reviewObject = reviewsArray.getJSONObject(i);

                author = reviewObject.getString(MDB_AUTHOR);
                content = reviewObject.getString(MDB_CONTENT);
                parsedReviewList.add(new Review( author, content));
            } catch(Exception e) {
                Log.d(MovieDbJsonUtils.class.getSimpleName(), e.getMessage());
            }
        }

        return parsedReviewList;
    }
}