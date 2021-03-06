package com.example.android.popularmovies;

import android.databinding.DataBindingUtil;
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

    public ReviewsAdapter(List<Review> reviews) {
        reviewList = reviews;
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

    public List<Review> getReviewList() {
        return reviewList;
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

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ReviewListItemBinding mBinding;

        ReviewViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        void bind(Review review) {
            mBinding.tvReviewAuthor.setText(review.getAuthor());
            mBinding.tvReviewContent.setText(review.getContent());
        }
    }
}
