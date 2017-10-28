package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler{

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;

    private static final int PORTRAIT_MOVIE_LIST_COLUMNS = 2;
    private static final int LANDSCAPE_MOVIE_LIST_COLUMNS = 4;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_list);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, PORTRAIT_MOVIE_LIST_COLUMNS));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, LANDSCAPE_MOVIE_LIST_COLUMNS));
        }

        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        loadMoviesData(NetworkUtils.POPULAR);
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("Movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    private void loadMoviesData(String sortOrder) {
        if(isOnline()) {
            showMovieDataView();
            mMoviesAdapter.setMovieData(null);
            new FetchMoviesTask(this, new FetchMoviesTaskListener()).execute(sortOrder);
        } else {
            showErrorMessage();
        }
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private class FetchMoviesTaskListener implements AsyncTaskListener<List<Movie>>
    {
        @Override
        public void onPreTaskStart() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTaskComplete(List<Movie> movies)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovieDataView();
                mMoviesAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            loadMoviesData(NetworkUtils.POPULAR);
            return true;
        }

        if (id == R.id.action_rating) {
            loadMoviesData(NetworkUtils.TOP_RATED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
