package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class SliderRecyclerAdapter extends RecyclerView.Adapter<SliderBannerViewHolder> {
    private List<String> list;
    Context context;

    public SliderRecyclerAdapter(List<String> Data, Context context) {
        list = Data;
        this.context = context;
    }

    @Override
    public SliderBannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_banner_item, parent, false);
        SliderBannerViewHolder holder = new SliderBannerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SliderBannerViewHolder holder, int position) {

        String url = list.get(position);
            Glide.with(context).load(url)
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .into(holder.coverImageView);

        //holder.coverImageView.setImageResource(R.drawable.ic_default);
        holder.coverImageView.setTag(url);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

