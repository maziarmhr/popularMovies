package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.databinding.ActivityDetailBinding;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        ButterKnife.bind(this);

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
            }
        }
    }
}
