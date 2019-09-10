package com.usomandroidproject.salujaecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    TextView textView;
    String signUpString = "", mobileNumber = "", password = "";
    EditText editText;
    ImageView imageView;
    Button loginButton;
    TextInputEditText passwordTextView;
    ProgressDialog progressDialog;
    boolean isCreateAccount = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView = (TextView) findViewById(R.id.signUpRedirect);
        editText = (EditText) findViewById(R.id.mobileLogin);
        imageView = (ImageView) findViewById(R.id.clearTextLogin);
        loginButton = (Button) findViewById(R.id.loginButton);
        passwordTextView = (TextInputEditText) findViewById(R.id.passwordActualEdit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("LOGIN");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        String signUpString = "New to Saluja? Signup now";

        progressDialog = new ProgressDialog(LoginActivity.this);

        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);

        SpannableString ss = new SpannableString(signUpString);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.stay);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };


        ss.setSpan(clickableSpan, 15, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String edit = editText.getText().toString();

                if (edit.length() > 0) {
                    if (imageView.getVisibility() == View.INVISIBLE) {
                        imageView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (imageView.getVisibility() == View.VISIBLE)
                        imageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumber = editText.getText().toString();
                password = passwordTextView.getText().toString();
                if (!BaseClass.isValidMobile(mobileNumber) || TextUtils.isEmpty(mobileNumber)) {
                    editText.setError("Please enter a valid mobile number");
                    isCreateAccount = false;
                }
                if (isCreateAccount) {
                    getData();
                }
            }
        });
    }

    public void getData() {
        String cartItems = BaseClass.getStringFromPreferences(LoginActivity.this, null, Config.CARTITEMS);
        if(cartItems == null)
        {
            cartItems = "";
        }

        progressDialog.show();

        RequestQueue rq = Volley.newRequestQueue(LoginActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET,
                "http://salujacart.usom.co.in/User/VerifyUser?username=" + mobileNumber + "&&password=" + password
                        +"&&productQuantity="+cartItems,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();

                            JSONObject job = new JSONObject(response);
                            if (job.getBoolean("success") == true) {
                                int count = job.getInt("UserCartDetailCount");
                                JSONObject job1 = job.getJSONObject("user");
                                int id = job1.getInt("Id");
                                String name = job1.getString("FullName");
                                String email = job1.getString("Email");
                                String mobile = job1.getString("PhoneNumber");
                                ((Cache)getApplicationContext()).setCount(count);
                                //job1.getString("ImagePath");

                                String userInfo = String.format("%s-;%s-;%s-;%s-;%s", id, name, email, mobile, password);
                                BaseClass.putStringInPreferences(LoginActivity.this, userInfo, Config.USERINFO);
                                Toast.makeText(LoginActivity.this, "User is successfully logged In", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomePage.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_out_right
                                        , android.R.anim.slide_out_right);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Log in failed. Incorrect Mobile number and password combination!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this,
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
        }

        return super.onOptionsItemSelected(item);
    }
}
