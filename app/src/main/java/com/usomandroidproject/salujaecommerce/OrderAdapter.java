package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends BaseAdapter {
    Context c;

    List<OrderItems> products;

    public OrderAdapter(Context c, List<OrderItems> products) {
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
            convertView = LayoutInflater.from(c).inflate(R.layout.order_adapter, parent, false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageOrder);
            TextView _headline = (TextView) convertView.findViewById(R.id.headlineOrder);
            TextView _actualPrice = (TextView) convertView.findViewById(R.id.actualPriceOrder);
            TextView _discountPrice = (TextView) convertView.findViewById(R.id.discountPriceOrder);
            TextView deliveStatus = (TextView)convertView.findViewById(R.id.deliverStatus);
            TextView deliverDate = (TextView)convertView.findViewById(R.id.deliverDate);
            TextView qty = (TextView) convertView.findViewById(R.id.qytOrder);
            TextView _offerPrice = (TextView)convertView.findViewById(R.id.offerOrder);
            //TextView button = (TextView)convertView.findViewById(R.id.viewOrderButton);

            final OrderItems productDetails = products.get(position);

            Glide.with(c).load(productDetails.getImageUrl())
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);

            _headline.setText(productDetails.getTitle());

            DecimalFormat formatter = new DecimalFormat("#,###,###");

            double actualPrice = productDetails.getActualPrice() * productDetails.getQuantity();
            _actualPrice.setText(String.format("%s%s",c.getResources().getString(R.string.Rs),
                    formatter.format(actualPrice)));

            double discountPrice = productDetails.getDiscountPrice() *  productDetails.getQuantity();
            double savedAmount = actualPrice - discountPrice;

            _discountPrice.setText(String.format("%s%s",c.getResources().getString(R.string.Rs),
                    formatter.format(discountPrice)));

            _actualPrice.setPaintFlags(_actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String offerDisplay = String.format("Saved %s %s",c.getResources().getString(R.string.Rs),
                    savedAmount);

            _offerPrice.setText(offerDisplay);

            String quantity = String.format("Color: %s | Qty: %s",productDetails.getColor(),productDetails.getQuantity());
            qty.setText(quantity);

            String deliver = "";
            if(productDetails.isDelivered())
            {
                deliver = "Delivered";
            }
            else
            {
                deliver = "Pending";
            }

            deliveStatus.setText(deliver);

            deliverDate.setText(productDetails.getDeliveryDate() == "" ?"":
                    String.format("(%s)",productDetails.getDeliveryDate()));

//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int orderId =  productDetails.getOrderId();
//                    Intent intent = new Intent(c,OrderDetailActivity.class);
//                    intent.putExtra("OrderId", orderId);
//                    c.startActivity(intent);
//                }
//            });

        } catch (Exception ex) {
            Toast.makeText(c, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return convertView;
    }

}
