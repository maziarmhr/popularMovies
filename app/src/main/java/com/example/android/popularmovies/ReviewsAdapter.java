package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.databinding.ReviewListItemBinding;

import java.util.List;

/**
 * Created by mm090d on 10/29/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> reviewList;

    private final ReviewsAdapterOnClickHandler mClickHandler;

    ReviewsAdapter(ReviewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewsAdapterOnClickHandler {
        void onClick(Review review);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReviewListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if(reviewList != null) {
            Review review = reviewList.get(position);

            holder.bind(review);
        }
    }

    @Override
    public int getItemCount() {
        if (null == reviewList) return 0;
        return reviewList.size();
    }

    public void setReviewData(List<Review> reviewList) {
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context mContext;
        private ReviewListItemBinding mBinding;

        ReviewViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
            mBinding = DataBindingUtil.bind(itemView);
        }

        void bind(Review review) {
            mBinding.tvReviewAuthor.setText(review.getAuthor());
            mBinding.tvReviewContent.setText(review.getContent());
        }

        @Override
        public void onClick(View v) {

        }
    }
}
