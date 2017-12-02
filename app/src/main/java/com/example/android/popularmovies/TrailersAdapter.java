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
import android.widget.ImageView;

import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.databinding.TrailerListItemBinding;

import java.util.List;

/**
 * Created by mm090d on 10/29/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {
    private List<Trailer> trailerList;

    private final TrailersAdapterOnClickHandler mClickHandler;

    TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailersAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TrailerListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if(trailerList != null) {
            Trailer trailer = trailerList.get(position);

            holder.bind(trailer);
        }
    }

    @Override
    public int getItemCount() {
        if (null == trailerList) return 0;
        return trailerList.size();
    }

    public void setTrailerData(List<Trailer> trailerList) {
        this.trailerList = trailerList;
        notifyDataSetChanged();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context mContext;
        private TrailerListItemBinding mBinding;

        TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContext = itemView.getContext();
            mBinding = DataBindingUtil.bind(itemView);
        }

        void bind(Trailer trailer) {
            mBinding.tvTrailerName.setText(trailer.getName());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            //mClickHandler.onClick(trailerList.get(adapterPosition));
            Trailer trailer = trailerList.get(adapterPosition);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
            try {
                mContext.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                mContext.startActivity(webIntent);
            }
        }
    }
}
