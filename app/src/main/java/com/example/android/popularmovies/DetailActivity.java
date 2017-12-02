package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.example.android.popularmovies.data.FavoriteMoviesContract;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.databinding.ActivityDetailBinding;
import com.example.android.popularmovies.utilities.MovieDbJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;
//TODO Fix Trailer and review display
//TODO Change color Theme
public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler{

    ActivityDetailBinding mBinding;
    TrailersAdapter mTrailersAdapter;
    ReviewsAdapter mReviewsAdapter;
    private Movie mMovie;

    private static final int TRAILER_LOADER_ID = 33;
    private static final int REVIEWS_LOADER_ID = 44;

    private static final String SEARCH_REQUEST_TYPE = "request_type";
    private static final String SEARCH_MOVIE_ID = "movie_id";

    @Override
    public void onClick(Trailer trailer) {

    }

    public class TrailerLoaderListener implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(DetailActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if(args == null) {
                        return;
                    }
                    //mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

                @Override
                public List<Trailer> loadInBackground() {
                    String searchCategory = args.getString(SEARCH_REQUEST_TYPE);
                    long movieId = args.getLong(SEARCH_MOVIE_ID);
                    if (searchCategory == null || TextUtils.isEmpty(searchCategory)) {
                        return null;
                    }

                    URL itemRequestUrl = NetworkUtils.buildMovieItemUrl(movieId, searchCategory);

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(itemRequestUrl);

                        return MovieDbJsonUtils
                                .getTrailerFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
            if (data != null) {
                //showMovieDataView();
                mTrailersAdapter.setTrailerData(data);
            } else {
                //showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    }

    public class ReviewLoaderListener implements LoaderManager.LoaderCallbacks<List<Review>> {

        @Override
        public Loader<List<Review>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Review>>(DetailActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if(args == null) {
                        return;
                    }
                    //mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

                @Override
                public List<Review> loadInBackground() {
                    String searchCategory = args.getString(SEARCH_REQUEST_TYPE);
                    long movieId = args.getLong(SEARCH_MOVIE_ID);
                    if (searchCategory == null || TextUtils.isEmpty(searchCategory)) {
                        return null;
                    }

                    URL itemRequestUrl = NetworkUtils.buildMovieItemUrl(movieId, searchCategory);

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(itemRequestUrl);

                        return MovieDbJsonUtils
                                .getReviewsFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            if (data != null) {
                //showMovieDataView();
                mReviewsAdapter.setReviewData(data);
            } else {
                //showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();

        if (intent != null) {

            if (intent.hasExtra("Movie")) {
                Bundle bundle = getIntent().getExtras();
                mMovie = bundle.getParcelable("Movie");
                Picasso.with(this).load(NetworkUtils.buildPosterUri(mMovie.getPoster(), NetworkUtils.PosterSizes.L)).into(mBinding.ivDetailPoster);
                mBinding.tvDetailTitle.setText(mMovie.getTitle());
                mBinding.tvDetailOverview.setText(mMovie.getOverview());
                mBinding.tvDetailRating.setText(String.format(getString(R.string.movie_rating), mMovie.getRating()));
                mBinding.tvDetailRelease.setText(String.format(getString(R.string.movie_release_date),mMovie.getReleaseDate()));
                mTrailersAdapter = new TrailersAdapter(null);
                mBinding.rvTrailers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mBinding.rvTrailers.setAdapter(mTrailersAdapter);

                mReviewsAdapter = new ReviewsAdapter(null);
                mBinding.rvReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mBinding.rvReviews.setAdapter(mReviewsAdapter);

                Bundle queryBundle = new Bundle();

                queryBundle.putString(SEARCH_REQUEST_TYPE, NetworkUtils.TRAILERS_PATH);
                queryBundle.putLong(SEARCH_MOVIE_ID, mMovie.getId());
                LoaderManager loaderManager = getSupportLoaderManager();
                loaderManager.restartLoader(TRAILER_LOADER_ID, queryBundle, new TrailerLoaderListener());

                queryBundle.putString(SEARCH_REQUEST_TYPE, NetworkUtils.REVIEWS_PATH);
                loaderManager.restartLoader(REVIEWS_LOADER_ID, queryBundle, new ReviewLoaderListener());
            }
        }
    }

    public void onClickFavorite(View view) {
        //TODO Implement delete operation too
        ContentValues movieContentValues = new ContentValues();
        //TODO how unique id is enforced? is duplicate id possible?
        movieContentValues.put(FavoriteMoviesContract.MovieEntry._ID, mMovie.getId());
        movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
        movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_RELEASE, mMovie.getReleaseDate());
        movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_POSTER, mMovie.getPoster());
        //TODO Check what needs to be done with URI here
        Uri uri = getContentResolver().insert(FavoriteMoviesContract.MovieEntry.CONTENT_URI, movieContentValues);
        //TODO Pull poster if internet connected
    }

    private boolean isFavorite(int movieId) {
        //TODO implement individual content id too
        Cursor cursor = getContentResolver().query(FavoriteMoviesContract.MovieEntry.CONTENT_URI, MAIN_MOVIE_PROJECTION, null, null, null);
        return (cursor != null && cursor.moveToFirst());
    }
}
