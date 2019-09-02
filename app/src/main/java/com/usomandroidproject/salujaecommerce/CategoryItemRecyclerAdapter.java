package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryItemRecyclerAdapter extends RecyclerView.Adapter<CategoryItemRecyclerAdapter.CategoryTitleViewHolder> {
    private List<Product> list;
    Context context;

    public CategoryItemRecyclerAdapter(List<Product> Data, Context context) {
        list = Data;
        this.context = context;
    }

    @Override
    public CategoryTitleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item_view_holder, parent, false);
        CategoryTitleViewHolder holder = new CategoryTitleViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CategoryTitleViewHolder holder, int position) {

        holder.titleTextView.setText(list.get(position).getName());

        String url = list.get(position).getIconImageFullPath();

        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_default)
                .error(R.drawable.ic_default)
                .fit()
                .into(holder.coverImageView);

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        holder.coverImageView.setImageResource(R.drawable.ic_default);
        String discountText = (int)list.get(position).getOfferPrice() != 0 ?
                String.format("%s%s",context.getResources().getString(R.string.Rs),formatter.format(list.get(position).getOfferPrice())) : "";
        holder.homeDiscountPrice.setText(discountText);
        holder.homeActualPrice.setText(String.format("%s%s",context.getResources().getString(R.string.Rs),
                formatter.format(list.get(position).getPrice()))); // SomeString = your old price
        if((int)list.get(position).getOfferPrice() != 0) {
            holder.homeActualPrice.setPaintFlags(holder.homeActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        double perDouble = list.get(position).getPerOff();
        String perOff = "";

        if(perDouble%1==0) {
            perOff = (int) list.get(position).getPerOff() != 0 ?
                    String.format("(%s %% OFF)", (int)perDouble) : "";
        }
        else
        {
            perOff = (int) list.get(position).getPerOff() != 0 ?
                    String.format("(%s %% OFF)", perDouble) : "";
        }
        holder.homeperOff.setText(perOff);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CategoryTitleViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView, homeDiscountPrice, homeActualPrice, homeperOff;
        public ImageView coverImageView;

        public CategoryTitleViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.productTitle);
            coverImageView = (ImageView) v.findViewById(R.id.productImage);
            homeActualPrice = (TextView)v.findViewById(R.id.homeActualPrice);
            homeDiscountPrice = (TextView)v.findViewById(R.id.homeDiscountPrice);
            homeperOff = (TextView)v.findViewById(R.id.homeOfferCart);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {

                        Intent intent = new Intent(context, DescriptionActivity.class);
                        intent.putExtra("ProductId",list.get(pos).getId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}

