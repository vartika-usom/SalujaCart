package com.usomandroidproject.salujaecommerce;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    int orderId;
    List<Order> orderList;
    List<OrderItems> orderItems, currentOrderItems;
    Order currentOrder;
    TextView orderdate, orderNumber, orderTotal;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ORDER DETAILS");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        orderdate = (TextView) findViewById(R.id.orderdateValue);
        orderNumber = (TextView) findViewById(R.id.orderNumValue);
        orderTotal = (TextView) findViewById(R.id.orderTotalValue);
        linearLayout = (LinearLayout) findViewById(R.id.iterativeLinear);
        currentOrderItems = new ArrayList<>();
        orderId = getIntent().getIntExtra("OrderId", 0);

        orderList = ((Cache) getApplicationContext()).getOrders();
        orderItems = ((Cache) getApplicationContext()).getOrderItems();
        for (Order order : orderList
        ) {
            if (order.getId() == orderId) {
                currentOrder = order;
                break;
            }
        }

        for (OrderItems order : orderItems
        ) {
            if (order.getOrderId() == orderId) {
                currentOrderItems.add(order);
            }
        }

        orderdate.setText(currentOrder.getOrderDate());
        orderNumber.setText(currentOrder.getOrderNumber());
        orderTotal.setText(String.format("%s%s(%s items)",
                getResources().getString(R.string.Rs),currentOrder.getTotalAmountPaid(), currentOrderItems.size()));

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < currentOrderItems.size(); i++) {
            View convertView = li.inflate(R.layout.order_adapter, null);

            ImageView iv = (ImageView) convertView.findViewById(R.id.imageOrder);
            TextView _headline = (TextView) convertView.findViewById(R.id.headlineOrder);
            TextView _actualPrice = (TextView) convertView.findViewById(R.id.actualPriceOrder);
            TextView _discountPrice = (TextView) convertView.findViewById(R.id.discountPriceOrder);
            TextView deliveStatus = (TextView)convertView.findViewById(R.id.deliverStatus);
            TextView deliverDate = (TextView)convertView.findViewById(R.id.deliverDate);
            TextView qty = (TextView) convertView.findViewById(R.id.qytOrder);
            TextView _offerPrice = (TextView)convertView.findViewById(R.id.offerOrder);
//            TextView button = (TextView) convertView.findViewById(R.id.viewOrderButton);
//            button.setVisibility(View.GONE);
            OrderItems productDetails = currentOrderItems.get(i);

            Glide.with(OrderDetailActivity.this).load(productDetails.getImageUrl())
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);

            _headline.setText(productDetails.getTitle());

            DecimalFormat formatter = new DecimalFormat("#,###,###");

            double actualPrice = productDetails.getActualPrice() * productDetails.getQuantity();
            _actualPrice.setText(String.format("%s%s",getResources().getString(R.string.Rs),
                    formatter.format(actualPrice)));

            double discountPrice = productDetails.getDiscountPrice() *  productDetails.getQuantity();
            double savedAmount = actualPrice - discountPrice;

            _discountPrice.setText(String.format("%s%s",getResources().getString(R.string.Rs),
                    formatter.format(discountPrice)));

            _actualPrice.setPaintFlags(_actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String offerDisplay = String.format("Saved %s %s",getResources().getString(R.string.Rs),
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

            deliverDate.setText(productDetails.getDeliveryDate() != ""?String.format("(%s)",productDetails.getDeliveryDate()):"");

            linearLayout.addView(convertView);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }
}
