package com.usomandroidproject.salujaecommerce;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

public class VerifyOtpActivity extends AppCompatActivity implements TextWatcher  {
    EditText editText_one, editText_two, editText_three, editText_four;
    TextView msgDisplay;
    String msg = "", mobileNumber, email, password, fullname, gender, otp;
    Button submitOtp;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("VERIFY");

        editText_one = (EditText) findViewById(R.id.edittextOne);
        editText_two = (EditText) findViewById(R.id.editTextTwo);
        editText_three = (EditText) findViewById(R.id.editTextThree);
        editText_four = (EditText) findViewById(R.id.editTextFour);
        msgDisplay = (TextView)findViewById(R.id.otptext);
        submitOtp = (Button)findViewById(R.id.submitotp);

        editText_one.addTextChangedListener(this);
        editText_two.addTextChangedListener(this);
        editText_three.addTextChangedListener(this);
        editText_four.addTextChangedListener(this);

        mobileNumber = getIntent().getStringExtra("MobileNumber");
        email = getIntent().getStringExtra("EmailAddress");
        fullname = getIntent().getStringExtra("FullName");
        password = getIntent().getStringExtra("Password");
        otp = getIntent().getStringExtra("otp");

        progressDialog = new ProgressDialog(VerifyOtpActivity.this);

        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);

        msg = String.format("We have sent a six digit OTP on \n%s", mobileNumber);
        msgDisplay.setText(msg);

        submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentOtp = String.format("%s%s%s%s",editText_one.getText(), editText_two.getText(),
                      editText_three.getText(), editText_four.getText());

                if(currentOtp.equals(otp))
                {
                    //save user in db
                    getData();

                }
            }
        });

    }


    public void getData() {
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(VerifyOtpActivity.this);
        final String URL = "http://salujacart.usom.co.in/User/AddUser";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("FullName", fullname);
        params.put("Email", email);
        params.put("PhoneNumber", mobileNumber);
        params.put("Gender", gender);
        params.put("Password", password);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();

                            if(response.getBoolean("Success") == true) {
                                JSONObject jsonObject = response.getJSONObject("Data");

                                int id = jsonObject.getInt("Id");

                                String userInfo = String.format("%s-;%s-;%s-;%s-;%s", id, fullname, email, mobileNumber, password);
                                BaseClass.putStringInPreferences(VerifyOtpActivity.this, userInfo, Config.USERINFO);
                                Toast.makeText(VerifyOtpActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(VerifyOtpActivity.this, HomePage.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_out_right
                                        , android.R.anim.slide_in_left);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(VerifyOtpActivity.this, "Sign up fail", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(VerifyOtpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(VerifyOtpActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(VerifyOtpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyOtpActivity.this,
                            "Oops. Timeout error!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        req.setRetryPolicy(new RetryPolicy() {
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

        Cache.getInstance().addToRequestQueue(req);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus();
            }

            if (editText_two.length() == 1) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 1) {
                editText_four.requestFocus();
            }
        } else if (editable.length() == 0) {
            if (editText_four.length() == 0) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 0) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 0) {
                editText_one.requestFocus();
            }
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
