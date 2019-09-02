package com.usomandroidproject.salujaecommerce;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SliderBannerViewHolder extends RecyclerView.ViewHolder {

    public ImageView coverImageView;

    public SliderBannerViewHolder(View v) {
        super(v);
        coverImageView = (ImageView) v.findViewById(R.id.sliderImageBanner);
    }
}
