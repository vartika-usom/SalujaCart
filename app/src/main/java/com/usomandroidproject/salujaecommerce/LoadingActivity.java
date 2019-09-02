package com.usomandroidproject.salujaecommerce;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class LoadingActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textInternet;
    Button internetButton;
    boolean notFirstLogin;
    int userId = 0;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textInternet = (TextView) findViewById(R.id.internetMessage);

        final String userInfo = BaseClass.getStringFromPreferences(LoadingActivity.this, null, Config.USERINFO);

        if (userInfo != null) {
            user = BaseClass.convertStringToUser(userInfo);
        }

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.seeGreen), PorterDuff.Mode.MULTIPLY);
        internetButton = (Button) findViewById(R.id.internetButton);
        internetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetButton.setVisibility(View.INVISIBLE);
                textInternet.setVisibility(View.INVISIBLE);
                getData();
            }
        });

        getData();
    }

    public void getData() {
        if(user != null)
        {
            userId = user.getId();
        }

        progressBar.setVisibility(View.VISIBLE);
        RequestQueue rq = Volley.newRequestQueue(LoadingActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/Product/GetInitialLoadAppData?userId="+userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            BaseClass.ResolveData(response, LoadingActivity.this);

                            Intent intent = new Intent(LoadingActivity.this, HomePage.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);
                            finish();
                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    textInternet.setVisibility(View.VISIBLE);
                    internetButton.setVisibility(View.VISIBLE);
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoadingActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(LoadingActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoadingActivity.this,
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
