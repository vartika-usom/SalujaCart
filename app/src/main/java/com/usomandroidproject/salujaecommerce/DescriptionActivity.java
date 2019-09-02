package com.usomandroidproject.salujaecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DescriptionActivity extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    TextView actualPriceText, discountPriceText, offerText, productDetails, productHeading, codText;
    String actualPrice = "3695", discountPrice = "2,032", offer = "45%";
    int productId;
    Button addToCart, pincodeCheck, goToCart;
    ProgressDialog progressDialog;
    Product product;
    List<String> mediaList;
    RecyclerView recyclerViewProduct;
    LinearLayoutManager MyLayoutManager;
    List<Product> similarProductItem;
    EditText pincode;
    String userInfo;
    User user;
    boolean Cod = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        actualPriceText = (TextView) findViewById(R.id.actualPrice);
        discountPriceText = (TextView) findViewById(R.id.discountPrice);
        offerText = (TextView) findViewById(R.id.offer);
        addToCart = (Button) findViewById(R.id.addToCart);
        productDetails = (TextView) findViewById(R.id.productDetails);
        productHeading = (TextView) findViewById(R.id.textHeading);
        recyclerViewProduct = (RecyclerView) findViewById(R.id.viewSimilarProduct);
        pincode = (EditText) findViewById(R.id.pincode);
        goToCart = (Button) findViewById(R.id.GoToCart);
        pincodeCheck = (Button) findViewById(R.id.pincodeCheck);
        codText = (TextView) findViewById(R.id.codText);
        progressDialog = new ProgressDialog(DescriptionActivity.this);

        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);
        similarProductItem = new ArrayList<>();
        mediaList = new ArrayList<>();

        productId = getIntent().getIntExtra("ProductId", 0);
        userInfo = BaseClass.getStringFromPreferences(DescriptionActivity.this, null, Config.USERINFO);
        user = BaseClass.convertStringToUser(userInfo);
        // bring data from server for product id
        getData();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        pincodeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pincodeValue = pincode.getText().toString();
                //call API to verify
                if(pincodeValue.length() == 6) {
                    verifyPin(pincodeValue);
                }
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo == null) {
                    String productCart = String.format("%s;%s", productId, 1);
                    BaseClass.addCartItem(DescriptionActivity.this, productCart);
                    BaseClass.updateUserCount(DescriptionActivity.this, false);
                    Toast.makeText(DescriptionActivity.this, "Product has been added to cart successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DescriptionActivity.this, CartListActivity.class);
                    startActivity(intent);
                } else {
                    //add product to user cart volley call
                    addProductToCart(user.getId());
                }

            }
        });

        goToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescriptionActivity.this, CartListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void verifyPin(String pinCode) {
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(DescriptionActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "https://staging-express.delhivery.com/c/api/pin-codes/json/" +
                        "?token=030bf950e970fc5daea3cc9f6000013b5373e61f&filter_codes=" + pinCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject job = new JSONObject(response);
                            JSONArray jsonArray = job.getJSONArray("delivery_codes");
                            progressDialog.dismiss();
                            if (jsonArray.length() == 0) {
                                Cod = false;
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job1 = jsonArray.getJSONObject(i).getJSONObject("postal_code");
                                    String cod = job1.getString("cod");
                                    if (cod.equals("Y")) {
                                        Cod = true;
                                    } else {
                                        Cod = false;
                                    }
                                }
                            }
                            codText.setVisibility(View.VISIBLE);
                            if (Cod) {
                                addToCart.setEnabled(true);
                                codText.setText(String.format("Delivery Available in this area | %s100", getResources().getString(R.string.Rs)));
                                codText.setTextColor(getResources().getColor(R.color.persian_green_accent));
                            } else {
                                codText.setText("Currently out of stock in this area.");
                                codText.setTextColor(Color.RED);
                                if(addToCart.isEnabled())
                                {
                                    addToCart.setEnabled(false);
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(DescriptionActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(DescriptionActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DescriptionActivity.this,
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

    public void addProductToCart(int userId) {
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(DescriptionActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Cart/AddProductInCart?ProductId=" + productId + "&&UserId=" + userId
                        + "&&Quantity=" + 1 + "&&Price=" + product.getOfferPrice(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            BaseClass.updateUserCount(DescriptionActivity.this, false);
                            Toast.makeText(DescriptionActivity.this, "Product added to cart successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DescriptionActivity.this, CartListActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(DescriptionActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(DescriptionActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DescriptionActivity.this,
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

    public void setData() {
        if (mediaList.size() > 0) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, mediaList);

            viewPager.setAdapter(viewPagerAdapter);

            dotscount = viewPagerAdapter.getCount();
            if (dotscount > 0) {
                dots = new ImageView[dotscount];

                for (int i = 0; i < dotscount; i++) {

                    dots[i] = new ImageView(this);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }
        }

        productHeading.setText(product.getName());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String discountText = (int) product.getOfferPrice() != 0 ? String.format("%s%s", getResources().getString(R.string.Rs), formatter.format(product.getOfferPrice())) : "";
        discountPriceText.setText(discountText);
        actualPriceText.setText(String.format("%s%s", getResources().getString(R.string.Rs), formatter.format(product.getPrice()))); // SomeString = your old price
        actualPriceText.setPaintFlags(actualPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String perOff = (int) product.getPerOff() != 0 ? String.format("(%s %% OFF)", product.getPerOff()) : "";
        offerText.setText(perOff);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            productDetails.setText(Html.fromHtml(product.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            productDetails.setText(Html.fromHtml(product.getDescription()));
        }
        //productDetails.setText(Html.fromHtml(product.getDescription()).toString()));

        MyLayoutManager = new LinearLayoutManager(DescriptionActivity.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (similarProductItem.size() > 0 & similarProductItem != null) {
            recyclerViewProduct.setAdapter(new CategoryItemRecyclerAdapter(similarProductItem, DescriptionActivity.this));
        }

        recyclerViewProduct.setLayoutManager(MyLayoutManager);

    }

    public void getData() {
        int userId = 0;
        if (userInfo != null) {
            userId = user.getId();
        } else {
            String cartItems = BaseClass.getStringFromPreferences(DescriptionActivity.this, null, Config.CARTITEMS);
            String productIds = BaseClass.getProductFromString(cartItems);
            if (productIds.contains(String.valueOf(productId))) {
                goToCart.setVisibility(View.VISIBLE);
                addToCart.setVisibility(View.INVISIBLE);
            }
        }
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(DescriptionActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Product/GetProductById?productId=" + productId + "&&userId=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jObject = new JSONObject(response);
                            JSONObject productObject = jObject.getJSONObject("Data");

                            product = BaseClass.getProductFromJson(productObject);
                            JSONArray jsonArray = jObject.getJSONArray("Media");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mediaList.add(jsonArray.getJSONObject(i).getString("ImagePath"));
                            }

                            JSONArray product = jObject.getJSONArray("SimilarProduct");
                            for (int i = 0; i < product.length(); i++) {
                                similarProductItem.add(BaseClass.getProductFromJson(product.getJSONObject(i)));
                            }
                            boolean isadded = jObject.getBoolean("IsAddedInCart");
                            if (isadded) {
                                goToCart.setVisibility(View.VISIBLE);
                                addToCart.setVisibility(View.INVISIBLE);
                            }
                            setData();

                        } catch (Exception e) {
                            Toast.makeText(DescriptionActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(DescriptionActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(DescriptionActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(DescriptionActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DescriptionActivity.this,
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
}
