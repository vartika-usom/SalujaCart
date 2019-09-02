package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends BaseAdapter {
    Context c;

    List<Product.Cart> products;

    public CartAdapter(Context c, List<Product.Cart> products) {
        this.products = products;
        this.c = c;
    }

//    public void AddListItemToAdapter(List<News> newsMore) {
//        //add item to current array
//        news.addAll(newsMore);
//        //notify UI
//        this.notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            convertView = LayoutInflater.from(c).inflate(R.layout.cart_adapter, parent, false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageCart);
            TextView _headline = (TextView) convertView.findViewById(R.id.headlineCart);
            TextView _actualPrice = (TextView) convertView.findViewById(R.id.actualPriceCart);
            TextView _discountPrice = (TextView) convertView.findViewById(R.id.discountPriceCart);
            TextView _offerPrice = (TextView) convertView.findViewById(R.id.offerCart);
            TextView qty = (TextView) convertView.findViewById(R.id.quantity);

            Product.Cart productDetails = products.get(position);

            Glide.with(c).load(productDetails.getIconImageFullPath())
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);

            _headline.setText(productDetails.getName());

            DecimalFormat formatter = new DecimalFormat("#,###,###");

            _actualPrice.setText(String.format("%s%s",c.getResources().getString(R.string.Rs),formatter.format(productDetails.getPrice() * productDetails.getQuantity())));

            _discountPrice.setText(String.format("%s%s",c.getResources().getString(R.string.Rs),formatter.format(productDetails.getOfferPrice() *  productDetails.getQuantity())));

            _actualPrice.setPaintFlags(_actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String offerDisplay = String.format("%s %% OFF",productDetails.getPerOff());

            _offerPrice.setText(offerDisplay);

            qty.setText(String.valueOf(productDetails.getQuantity()));


        } catch (Exception ex) {
            Toast.makeText(c, "hello"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return convertView;
    }

}
