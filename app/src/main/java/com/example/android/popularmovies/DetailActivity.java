package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    ActivityDetailBinding mBinding;
    TrailersAdapter mTrailersAdapter;
    ReviewsAdapter mReviewsAdapter;
    private Movie mMovie;
    private boolean isMovieFavorite;

    private static final int TRAILER_LOADER_ID = 33;
    private static final int REVIEWS_LOADER_ID = 44;

    private static final String SEARCH_REQUEST_TYPE = "request_type";
    private static final String SEARCH_MOVIE_ID = "movie_id";
    private static final String TRAILERS_KEY = "trailers_key";
    private static final String REVIEWS_KEY = "reviews_key";

    public class TrailerLoaderListener implements LoaderManager.LoaderCallbacks<List<Trailer>> {

        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(DetailActivity.this) {
                List<Trailer> cachedTrailers;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (cachedTrailers != null) {
                        deliverResult(cachedTrailers);
                    } else {
                        if (args == null) {
                            return;
                        }
                        forceLoad();
                    }
                }

                @Override
                public List<Trailer> loadInBackground() {
                    String searchCategory = args.getString(SEARCH_REQUEST_TYPE);
                    long movieId = args.getLong(SEARCH_MOVIE_ID);
                    if (searchCategory == null || TextUtils.isEmpty(searchCategory)) {
                        return Collections.emptyList();
                    }

                    URL itemRequestUrl = NetworkUtils.buildMovieItemUrl(movieId, searchCategory);

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(itemRequestUrl);

                        return MovieDbJsonUtils
                                .getTrailerFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        return Collections.emptyList();
                    }
                }

                @Override
                public void deliverResult(List<Trailer> data) {
                    cachedTrailers = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
            if (data != null) {
                mTrailersAdapter.setTrailerData(data);
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
                List<Review> cachedReviews;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (cachedReviews != null) {
                        deliverResult(cachedReviews);
                    } else {
                        if (args == null) {
                            return;
                        }
                        forceLoad();
                    }
                }

                @Override
                public List<Review> loadInBackground() {
                    String searchCategory = args.getString(SEARCH_REQUEST_TYPE);
                    long movieId = args.getLong(SEARCH_MOVIE_ID);
                    if (searchCategory == null || TextUtils.isEmpty(searchCategory)) {
                        return Collections.emptyList();
                    }

                    URL itemRequestUrl = NetworkUtils.buildMovieItemUrl(movieId, searchCategory);

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(itemRequestUrl);

                        return MovieDbJsonUtils
                                .getReviewsFromJson(jsonMovieResponse);

                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        return Collections.emptyList();
                    }
                }

                @Override
                public void deliverResult(List<Review> data) {
                    cachedReviews = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            if (data != null) {
                mReviewsAdapter.setReviewData(data);
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

        if (intent != null && intent.hasExtra("Movie")) {
                Bundle bundle = getIntent().getExtras();
                mMovie = bundle.getParcelable("Movie");
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        updateFavorite(isFavorite(mMovie.getId()));
                    }
                });

                Picasso.with(this).load(NetworkUtils.buildPosterUri(mMovie.getPoster(), NetworkUtils.PosterSizes.L)).into(mBinding.ivDetailPoster);
                mBinding.tvDetailTitle.setText(mMovie.getTitle());
                mBinding.tvDetailOverview.setText(mMovie.getOverview());
                mBinding.tvDetailRating.setText(String.format(getString(R.string.movie_rating), mMovie.getRating()));
                mBinding.tvDetailRelease.setText(mMovie.getReleaseDate());

                List<Trailer> trailers = null;
                List<Review> reviews = null;
                if(savedInstanceState != null) {
                    trailers = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
                    reviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
                }
                mTrailersAdapter = new TrailersAdapter(trailers);
                mBinding.rvTrailers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mBinding.rvTrailers.setAdapter(mTrailersAdapter);

                mReviewsAdapter = new ReviewsAdapter(reviews);
                mBinding.rvReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mBinding.rvReviews.setAdapter(mReviewsAdapter);

                if(savedInstanceState == null) {
                    Bundle trailerQueryBundle = new Bundle();

                    trailerQueryBundle.putString(SEARCH_REQUEST_TYPE, NetworkUtils.TRAILERS_PATH);
                    trailerQueryBundle.putLong(SEARCH_MOVIE_ID, mMovie.getId());
                    LoaderManager loaderManager = getSupportLoaderManager();
                    loaderManager.restartLoader(TRAILER_LOADER_ID, trailerQueryBundle, new TrailerLoaderListener());

                    Bundle reviewQueryBundle = new Bundle();
                    reviewQueryBundle.putString(SEARCH_REQUEST_TYPE, NetworkUtils.REVIEWS_PATH);
                    reviewQueryBundle.putLong(SEARCH_MOVIE_ID, mMovie.getId());
                    loaderManager.restartLoader(REVIEWS_LOADER_ID, reviewQueryBundle, new ReviewLoaderListener());
                }
        }
    }

    public void onClickFavorite(View view) {
        final boolean isFavorite = isMovieFavorite;
        updateFavorite(!isMovieFavorite);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(isFavorite) {
                    Uri uri = FavoriteMoviesContract.MovieEntry.buildMovieWithIdUri(mMovie.getId());
                    getContentResolver().delete(uri, null, null);
                } else {

                    ContentValues movieContentValues = new ContentValues();

                    movieContentValues.put(FavoriteMoviesContract.MovieEntry._ID, mMovie.getId());
                    movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
                    movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                    movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
                    movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_RELEASE, mMovie.getReleaseDate());
                    movieContentValues.put(FavoriteMoviesContract.MovieEntry.COLUMN_POSTER, mMovie.getPoster());

                    getContentResolver().insert(FavoriteMoviesContract.MovieEntry.CONTENT_URI, movieContentValues);
                }
            }
        });
    }

    private boolean isFavorite(long movieId) {
        Uri uri = FavoriteMoviesContract.MovieEntry.buildMovieWithIdUri(movieId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        return (cursor != null && cursor.moveToFirst());
    }

    private void updateFavorite(boolean isFavorite) {
        isMovieFavorite = isFavorite;
        if(isFavorite) {
            mBinding.ibFavorite.setBackgroundResource(R.drawable.star_yellow);
        } else {
            mBinding.ibFavorite.setBackgroundResource(R.drawable.star_white);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRAILERS_KEY, (ArrayList<? extends Parcelable>) mTrailersAdapter.getTrailerList());
        outState.putParcelableArrayList(REVIEWS_KEY, (ArrayList<? extends Parcelable>) mReviewsAdapter.getReviewList());
    }
}
