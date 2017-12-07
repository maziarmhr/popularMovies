package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.data.FavoriteMoviesContract;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.utilities.MovieDbJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PORTRAIT_MOVIE_LIST_COLUMNS = 2;
    private static final int LANDSCAPE_MOVIE_LIST_COLUMNS = 4;

    private static final int MOVIES_LOADER_ID = 22;

    private static final String SEARCH_MOVIES_CATEGORY_EXTRA = "category";
    private static final String RECYCLER_POSITION_KEY = "recycler_position";

    private int mLastRecyclerViewPosition = 0;

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * Movie data.
     */
    private static final String[] MAIN_MOVIE_PROJECTION = {
            FavoriteMoviesContract.MovieEntry._ID,
            FavoriteMoviesContract.MovieEntry.COLUMN_TITLE,
            FavoriteMoviesContract.MovieEntry.COLUMN_RELEASE,
            FavoriteMoviesContract.MovieEntry.COLUMN_RATING,
            FavoriteMoviesContract.MovieEntry.COLUMN_POSTER,
            FavoriteMoviesContract.MovieEntry.COLUMN_OVERVIEW
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_RELEASE = 2;
    public static final int INDEX_MOVIE_RATING = 3;
    public static final int INDEX_MOVIE_POSTER = 4;
    public static final int INDEX_MOVIE_OVERVIEW = 5;

    ActivityMainBinding mBinding;

    private RecyclerView mRecyclerView;

    private MoviesAdapter mMoviesAdapter;
    private int mMenuOptionSelected = R.id.action_popular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mLastRecyclerViewPosition = 0;
        mRecyclerView = mBinding.rvMoviesList;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, PORTRAIT_MOVIE_LIST_COLUMNS));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, LANDSCAPE_MOVIE_LIST_COLUMNS));
        }

        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            mMenuOptionSelected = savedInstanceState.getInt(SEARCH_MOVIES_CATEGORY_EXTRA);
            mLastRecyclerViewPosition = savedInstanceState.getInt(RECYCLER_POSITION_KEY);
        }
        loadMoviesData(mMenuOptionSelected);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEARCH_MOVIES_CATEGORY_EXTRA, mMenuOptionSelected);
        outState.putInt(RECYCLER_POSITION_KEY, ((GridLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("Movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    private void loadMoviesData(int sortOrder) {
        if(isOnline() || sortOrder == R.id.action_favorites) {
            showMovieDataView();
            mMoviesAdapter.setMovieData(null);
            Bundle queryBundle = new Bundle();

            queryBundle.putInt(SEARCH_MOVIES_CATEGORY_EXTRA, sortOrder);
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.restartLoader(MOVIES_LOADER_ID, queryBundle, this);
        } else {
            showErrorMessage();
        }
    }

    private void showMovieDataView() {
        mBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> cachedMovies;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (cachedMovies != null) {
                    deliverResult(cachedMovies);
                } else {
                    if (args == null) {
                        return;
                    }
                    mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                int searchCategory = args.getInt(SEARCH_MOVIES_CATEGORY_EXTRA);
                switch (searchCategory) {
                    case R.id.action_popular:
                        return loadFromCloud(NetworkUtils.POPULAR);
                    case R.id.action_rating:
                        return loadFromCloud(NetworkUtils.TOP_RATED);
                    case R.id.action_favorites:
                        Cursor cursor = getContentResolver().query(FavoriteMoviesContract.MovieEntry.CONTENT_URI, MAIN_MOVIE_PROJECTION, null, null, null);
                        List<Movie> favoriteMovies = new ArrayList<>();
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            Movie movie = new Movie(cursor.getLong(INDEX_MOVIE_ID),
                                    cursor.getString(INDEX_MOVIE_TITLE),
                                    cursor.getString(INDEX_MOVIE_OVERVIEW),
                                    cursor.getDouble(INDEX_MOVIE_RATING),
                                    new Date(cursor.getLong(INDEX_MOVIE_RELEASE)),
                                    cursor.getString(INDEX_MOVIE_POSTER)
                            );

                            favoriteMovies.add(movie);
                        }
                        return favoriteMovies;
                    default:
                        return Collections.emptyList();
                }
            }

            private List<Movie> loadFromCloud(String searchCategory) {
                if (searchCategory == null || TextUtils.isEmpty(searchCategory)) {
                    return Collections.emptyList();
                }
                URL popularMoviesRequestUrl = NetworkUtils.buildUrl(searchCategory);

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(popularMoviesRequestUrl);

                    return MovieDbJsonUtils
                            .getMovieFromJson(jsonMovieResponse);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    return Collections.emptyList();
                }
            }

            @Override
            public void deliverResult(List<Movie> data) {
                cachedMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieDataView();
            mMoviesAdapter.setMovieData(data);
            if(mLastRecyclerViewPosition > 0 ) {
                mRecyclerView.scrollToPosition(mLastRecyclerViewPosition);
            }
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mMenuOptionSelected = item.getItemId();

        switch (mMenuOptionSelected) {
            case R.id.action_popular:
            case R.id.action_rating:
            case R.id.action_favorites:

                loadMoviesData(mMenuOptionSelected);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
