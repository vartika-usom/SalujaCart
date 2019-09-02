package com.usomandroidproject.salujaecommerce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.Map;

public class CartListActivity extends AppCompatActivity {

    ListView cartDetails;
    String cartItems;
    View view;
    int userId = 0;
    List<Product.Cart> cartItemsList;
    Map<Integer,Integer> productQuantityList;
    TextView textView, insideTextView;
    RelativeLayout insideRelative;
    CartAdapter cartAdapter;
    String userInfo;
    String productIds;
    ProgressDialog progressDialog;
    List<Product.Cart> cacheProduct;
    TextView totalMrp, bagDiscount, tax, orderTotal, deliveryCharges, totalAmmount,
            totalFooterAmount, discountFooterAmount, footerLink, proceedToPay;
    String _totalMrp, _bagDiscount, _tax, _orderTotal, _deliveryCharges, _totalAmmount;
    CardView cardView;
    int totalActualPrice = 0, totalDiscount = 0, totalAmountDisplay = 0;
    DecimalFormat formatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        cartDetails = (ListView) findViewById(R.id.cartItems);
        textView = (TextView) findViewById(R.id.quantity);
        proceedToPay = (TextView) findViewById(R.id.proceedToPay);
        progressDialog = new ProgressDialog(CartListActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("SHOPPING BAG");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.listfooterview, null);
        totalMrp = (TextView) view.findViewById(R.id.totalMrpValue);
        bagDiscount = (TextView) view.findViewById(R.id.bagDiscountValue);
        tax = (TextView) view.findViewById(R.id.taxValue);
        orderTotal = (TextView) view.findViewById(R.id.orderValue);
        deliveryCharges = (TextView) view.findViewById(R.id.deliveryValue);
        totalAmmount = (TextView) view.findViewById(R.id.totalAmount);
        totalFooterAmount = (TextView) findViewById(R.id.totalFooterAmount);
        discountFooterAmount = (TextView) findViewById(R.id.discountFooterAmount);
        footerLink = (TextView) findViewById(R.id.viewDetail);
        cardView = (CardView) findViewById(R.id.proceedCard);

        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);
        cartDetails.addFooterView(view);
        userInfo = BaseClass.getStringFromPreferences(CartListActivity.this, null, Config.USERINFO);
        cartItemsList = new ArrayList<>();
        if (userInfo == null) {
            cartItems = BaseClass.getStringFromPreferences(CartListActivity.this, null, Config.CARTITEMS);
            productIds = BaseClass.getProductFromString(cartItems);
            if (productIds != "") {
                getCacheDetail();
            } else {
                cardView.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().
                        add(R.id.cart_activity, new PlaceHolderFragment()).commit();
            }
        } else {
            userId = Integer.parseInt(userInfo.split("-;")[0]);
            getCartDetails();
        }

        formatter = new DecimalFormat("#,###,###");

//        cartItemsList.add(new Product(1,"Mobile VIVO", 8999, 7999, 40, R.drawable.ic_default, 1));
//        cartItemsList.add(new Product(2,"Mobile VIVO", 8999, 7999, 40, R.drawable.ic_default, 1));

        if (userInfo == null) {
            //cartItemsList = BaseClass.updateQuantityInProduct(cartItemsList, CartListActivity.this);
        }

        footerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDetails.smoothScrollToPosition(cartAdapter.getCount());
            }
        });

        proceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo == null) {
                    Intent proceed = new Intent(CartListActivity.this, LoginActivity.class);
                    startActivity(proceed);
                } else {
                    Intent proceed = new Intent(CartListActivity.this, AddressActivity.class);
                    proceed.putExtra("Amount", totalAmountDisplay);
                    startActivity(proceed);
                }
            }
        });

        cartDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CartListActivity.this, DescriptionActivity.class);
                int productId = cacheProduct.get(position).getId();
                intent.putExtra("ProductId", productId);
                startActivity(intent);
            }
        });

    }

    public void getCartDetails() {

        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(CartListActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Cart/GetUserCartDetail?userId=" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            cartItemsList = BaseClass.getProductForCart(response, CartListActivity.this);
                            if (cartItemsList.size() > 0) {
                                cartAdapter = new CartAdapter(CartListActivity.this, cartItemsList);
                                cartDetails.setAdapter(cartAdapter);
                                cacheProduct = ((Cache) getApplicationContext()).getCartProduct();
                                setTotalData();
                            } else {
                                cardView.setVisibility(View.INVISIBLE);
                                getSupportFragmentManager().beginTransaction().
                                        add(R.id.cart_activity, new PlaceHolderFragment()).commit();
                            }
                            //Toast.makeText(CartListActivity.this, cacheProduct.size()+";"+cacheProduct.get(0).getCartId(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(CartListActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(CartListActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(CartListActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(CartListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartListActivity.this,
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

    public void getCacheDetail() {

        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(CartListActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Product/GetProductByProductIds?productIds=" + productIds,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject job = new JSONObject(response);
                            JSONArray jArray = job.getJSONArray("Data");

                            productQuantityList = BaseClass.getIntegerIntegerMap(CartListActivity.this);

                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject jProduct = jArray.getJSONObject(i);
                                Product product = BaseClass.getProductFromJson1(jProduct);

                                cartItemsList.add(new Product.Cart(i, product.getName(), product.getPrice(), product.getId(), product.getOfferPrice(),
                                        product.getPerOff(), productQuantityList.get(product.getId()), product.getProductCategoryId(), product.getProductCategoryName(),
                                        product.getIconImageFullPath(), product.getNumberOfProductInStock(), product.getDescription()));
                            }

                            ((Cache) getApplicationContext()).setCartProduct(cartItemsList);
                            cartAdapter = new CartAdapter(CartListActivity.this, cartItemsList);
                            cartDetails.setAdapter(cartAdapter);
                            cacheProduct = ((Cache) getApplicationContext()).getCartProduct();
                            setTotalData();
                        } catch (Exception e) {
                            Toast.makeText(CartListActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(CartListActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(CartListActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(CartListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartListActivity.this,
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

    public void editProductToCart(final int cartId, final int quantity, final int offerPrice, final int actualPrice) {
        progressDialog.show();

        RequestQueue rq = Volley.newRequestQueue(CartListActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Cart/EditCart?id=" + cartId + "&&quantity=" + quantity,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            updateTotalFromBoth(quantity, cartId);
                            //Toast.makeText(CartListActivity.this, "Product updated to cart successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CartListActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(CartListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartListActivity.this,
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

    public void updateTotalFromBoth(int quantity, int cartId) {
        Product.Cart cartUpdated = null;

        for (Product.Cart cart : cacheProduct
        ) {
            if (cart.getCartId() == cartId) {
                cartUpdated = cart;
            }
        }

        cacheProduct.remove(cartUpdated);
        cartUpdated.setQuantity(quantity);
        cacheProduct.add(cartUpdated);

        ((Cache) getApplicationContext()).setCartProduct(cacheProduct);
        setTotalData();
    }


    public void ShowDialog(View view) {
        insideRelative = (RelativeLayout) view.getParent();
        insideTextView = (TextView) insideRelative.findViewById(R.id.quantity);
        final TextView discountText = (TextView) insideRelative.findViewById(R.id.discountPriceCart);
        final TextView actualText = (TextView) insideRelative.findViewById(R.id.actualPriceCart);

        final int positionDetail = cartDetails.getPositionForView(insideRelative);

        LayoutInflater factory = LayoutInflater.from(this);
        final View loginDialogView = factory.inflate(R.layout.quantity_dialog, null);
        final AlertDialog loginDialog = new AlertDialog.Builder(this).create();
        loginDialog.setView(loginDialogView);

        TextView changeQuantity = (TextView) loginDialogView.findViewById(R.id.changeQuantity);
        ListView listQuantity = (ListView) loginDialogView.findViewById(R.id.listQuantity);
        List<Integer> integer = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            integer.add(i);
        }
        int quantityValue = Integer.parseInt(insideTextView.getText().toString());
        final QuantityAdapter quantityAdapter = new QuantityAdapter(integer, CartListActivity.this, quantityValue);//add selected quantity
        listQuantity.setAdapter(quantityAdapter);
        listQuantity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    insideTextView.setText(String.valueOf(position + 1));
                    double value = cacheProduct.get(positionDetail).getOfferPrice();
                    double actualValue = cacheProduct.get(positionDetail).getPrice();
                    int quantityValue = (int) value * (position + 1);
                    int actualValueQuantity = (int) actualValue * (position + 1);
                    discountText.setText(String.valueOf(formatter.format(quantityValue)));
                    actualText.setText(String.valueOf(formatter.format(actualValueQuantity)));
                    loginDialog.dismiss();
                    int productId = cacheProduct.get(positionDetail).getId();
                    int cartId = cacheProduct.get(positionDetail).getCartId();

                    if (userInfo == null) {
                        BaseClass.editCartItem(productId, position + 1, CartListActivity.this);
                        updateTotalFromBoth(position + 1, positionDetail);
                    } else {
                        //edit cart with quantity
                        editProductToCart(cartId, position + 1, quantityValue, actualValueQuantity);
                    }
                }

                quantityAdapter.setSelectedPosition(position);
                quantityAdapter.notifyDataSetChanged();
            }
        });
        loginDialog.show();
        loginDialog.getWindow().setLayout(550, 1000);
    }


    public void RemoveItem(View view) {
        RelativeLayout insideRelative = (RelativeLayout) view.getParent();
        final int positionDetail = cartDetails.getPositionForView(insideRelative);
        int cardId = cacheProduct.get(positionDetail).getCartId();
        BaseClass.updateUserCount(CartListActivity.this, true);
        if (userInfo == null) {
            int productId = cacheProduct.get(positionDetail).getId();
            BaseClass.removeProductFromCart(CartListActivity.this, productId);
            updateCacheAndAdapter(positionDetail);

            setTotalData();
        } else {
            removeProductFromCart(cardId, positionDetail);
        }

        Toast.makeText(this, ((Cache) getApplicationContext()).getCount() + "", Toast.LENGTH_SHORT).show();
    }

    public void loadFragment() {
        if (cartAdapter.getCount() == 0) {
            cardView.setVisibility(View.INVISIBLE);
            cartDetails.removeFooterView(view);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.cart_activity, new PlaceHolderFragment()).commit();
        }
    }

    public void updateCacheAndAdapter(int positionDetail) {
        cartItemsList.remove(positionDetail);
        cartAdapter.notifyDataSetChanged();

        cacheProduct = cartItemsList;
        ((Cache) getApplicationContext()).setCartProduct(cacheProduct);
        loadFragment();
    }

    public void removeProductFromCart(int cartId, final int positionDetail) {
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(CartListActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Cart/RemoveProductInCart?id=" + cartId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            updateCacheAndAdapter(positionDetail);
                            setTotalData();
                            //Toast.makeText(CartListActivity.this, "Product Deleted from cart successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CartListActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(CartListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartListActivity.this,
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

    public void setTotalData() {
        totalActualPrice = 0;
        totalDiscount = 0;
        for (Product.Cart cart : cacheProduct) {

            int offerPrice = (int) Math.ceil(cart.getOfferPrice()) * cart.getQuantity();
            int actualPrice = (int) Math.ceil(cart.getPrice()) * cart.getQuantity();
            totalActualPrice += actualPrice;

            int discount = actualPrice - offerPrice;
            totalDiscount += discount;
        }

        _totalMrp = String.format("%s%s", getResources().getString(R.string.Rs), formatter.format(totalActualPrice));
        totalMrp.setText(_totalMrp);

        _bagDiscount = String.format("-%s%s", getResources().getString(R.string.Rs), formatter.format(totalDiscount));
        bagDiscount.setText(_bagDiscount);

        int calTax = totalActualPrice - totalDiscount;
        double taxDisplay = calTax * 0.18;

        tax.setText(String.format("%s%s", getResources().getString(R.string.Rs), formatter.format(taxDisplay)));
        totalAmountDisplay = (int) Math.ceil(calTax + taxDisplay);

        orderTotal.setText(String.format("%s%s", getResources().getString(R.string.Rs), formatter.format(totalAmountDisplay)));

        deliveryCharges.setText("100");

        totalAmountDisplay = totalAmountDisplay + 100;

        totalAmmount.setText(String.format("%s%s", getResources().getString(R.string.Rs),
                formatter.format(totalAmountDisplay)));

        totalFooterAmount.setText(String.format("%s%s", getResources().getString(R.string.Rs),
                formatter.format(totalAmountDisplay)));

        discountFooterAmount.setText(_totalMrp);
        discountFooterAmount.setPaintFlags(discountFooterAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
