package com.usomandroidproject.salujaecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class OrderActivity extends AppCompatActivity {
    ListView orderList;
    ProgressDialog progressDialog;
    OrderAdapter orderAdapter;
    List<OrderItems> cartItemsList;
    String userInfo;
    int userId = 0;
    OrderItems productDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("MY ORDERS");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        orderList = (ListView) findViewById(R.id.orderItems);
        progressDialog = new ProgressDialog(OrderActivity.this);
        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);
        userInfo = BaseClass.getStringFromPreferences(OrderActivity.this, null, Config.USERINFO);
        userId = Integer.parseInt(userInfo.split("-;")[0]);

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderItems productDetails = cartItemsList.get(position);
                int orderId =  productDetails.getOrderId();
                Intent intent = new Intent(OrderActivity.this,OrderDetailActivity.class);
                intent.putExtra("OrderId", orderId);
                startActivity(intent);
            }
        });

        getCartDetails();
    }


    public void getCartDetails() {

        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(OrderActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Order/GetOrderDetailByUserId?userId=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            BaseClass.getOrders(response, OrderActivity.this);
                            cartItemsList = ((Cache)getApplicationContext()).getOrderItems();
                            if (cartItemsList.size() > 0) {
                                orderAdapter = new OrderAdapter(OrderActivity.this, cartItemsList);
                                orderList.setAdapter(orderAdapter);

                            } else {
//                                cardView.setVisibility(View.INVISIBLE);
//                                getSupportFragmentManager().beginTransaction().
//                                        add(R.id.cart_activity, new PlaceHolderFragment()).commit();
                            }
                            //Toast.makeText(CartListActivity.this, cacheProduct.size()+";"+cacheProduct.get(0).getCartId(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(OrderActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(OrderActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(OrderActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(OrderActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderActivity.this,
                            "Oops. Timeout error!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        rq.add(sr);

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
