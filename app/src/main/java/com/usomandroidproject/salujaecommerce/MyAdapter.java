package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    public List<Product> mItems = new ArrayList<Product>();
    LayoutInflater mInflater;
    Context context;

    public MyAdapter(Context context, List<Product> products) {
        this.context = context;
        mItems = products;
        mInflater = LayoutInflater.from(context);
    }

    public void AddListItemToAdapter(List<Product> list) {
        //add item to current array
        mItems.addAll(list);
        //notify UI
        this.notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Product getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name, gridActualPrice, gridDiscountPrice, gridOfferCart;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.textGrid, v.findViewById(R.id.textGrid));
            v.setTag(R.id.gridActualPrice, v.findViewById(R.id.gridActualPrice));
            v.setTag(R.id.gridDiscountPrice, v.findViewById(R.id.gridDiscountPrice));
            v.setTag(R.id.gridOfferCart, v.findViewById(R.id.gridOfferCart));
            v.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 900));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.textGrid);
        gridActualPrice = (TextView) v.getTag(R.id.gridActualPrice);
        gridDiscountPrice = (TextView) v.getTag(R.id.gridDiscountPrice);
        gridOfferCart = (TextView) v.getTag(R.id.gridOfferCart);

        Product item = getItem(i);
        String url = item.getIconImageFullPath();
        Glide.with(context).load(url)
                .placeholder(R.drawable.ic_default)
                .error(R.drawable.ic_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(picture);
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        //picture.setImageResource(item.getDrawableId());
        name.setText(item.getName());
        gridActualPrice.setText(String.format("%s%s", context.getResources().getString(R.string.Rs), formatter.format
                (item.getPrice())));

        if ((int) item.getOfferPrice() == 0) {
            gridDiscountPrice.setText("");
        } else {
            gridDiscountPrice.setText(String.format("%s%s", context.getResources().getString(R.string.Rs), formatter.format
                    (item.getOfferPrice())));
            gridActualPrice.setPaintFlags(gridActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        String perOff = (int)item.getPerOff() != 0 ? String.format("(%s %% OFF)", item.getPerOff()) : "";
        gridOfferCart.setText(perOff);

        return v;
    }
}
