package com.example.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.example.android.popularmovies.utilities.MovieDbJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by mm090d on 8/5/2017.
 */

public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
    private Context context;
    private AsyncTaskListener<List<Movie>> listener;

    public FetchMoviesTask(Context ctx, AsyncTaskListener<List<Movie>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreTaskStart();
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String category = params[0];
        URL popularMoviesRequestUrl = NetworkUtils.buildUrl(category);

        try {
            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(popularMoviesRequestUrl);

            return MovieDbJsonUtils
                    .getMovieFromJson(jsonMovieResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        listener.onTaskComplete(movies);
    }
}
