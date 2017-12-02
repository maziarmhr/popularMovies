package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mm090d on 7/30/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> mMovieList;

    private final MoviesAdapterOnClickHandler mClickHandler;

    MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if(mMovieList != null) {
            Movie movie = mMovieList.get(position);

            holder.bind(movie);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    public void setMovieData(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context mContext;
        private final ImageView mPosterImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
            mPosterImageView = itemView.findViewById(R.id.iv_movie_poster);
        }

        void bind(Movie movie) {
            Picasso.with(mContext).load(NetworkUtils.buildPosterUri(movie.getPoster(),NetworkUtils.PosterSizes.L)).into(mPosterImageView);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mMovieList.get(adapterPosition));
        }
    }
}
