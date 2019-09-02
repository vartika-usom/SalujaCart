package com.usomandroidproject.salujaecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText email,password, mobileNumber, name;
    Button createAccount;
    String emailtext, passwordText, mobileNumberText, nameText;
    boolean isCreateAccount = true;
    TextInputLayout emailAddressInput, passwordInput, mobileNumberInput, fullnameInput;
    String otp = "9999", gender = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("SIGN-UP");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        email = (TextInputEditText) findViewById(R.id.emailAddressText);
        password = (TextInputEditText) findViewById(R.id.passwordText);
        mobileNumber = (TextInputEditText) findViewById(R.id.phoneText);
        name = (TextInputEditText) findViewById(R.id.nameText);
        createAccount = (Button) findViewById(R.id.buttonCreate);
        emailAddressInput = (TextInputLayout)findViewById(R.id.emailAddress);
        passwordInput = (TextInputLayout)findViewById(R.id.password);
        mobileNumberInput = (TextInputLayout)findViewById(R.id.mobileNumber);
        fullnameInput = (TextInputLayout)findViewById(R.id.FullName);

        progressDialog = new ProgressDialog(SignUpActivity.this);

        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailtext = email.getText().toString();
                passwordText = password.getText().toString();
                mobileNumberText = mobileNumber.getText().toString();
                nameText = name.getText().toString();

                if (!BaseClass.isValidMail(emailtext) || TextUtils.isEmpty(emailtext)) {
                    emailAddressInput.setError("Please enter a valid email address");
                    isCreateAccount = false;
                }
                else if (!BaseClass.isValidMobile(mobileNumberText) || TextUtils.isEmpty(mobileNumberText)) {
                    mobileNumberInput.setError("Please enter a valid mobile number");
                    isCreateAccount = false;
                }
                else if(TextUtils.isEmpty(passwordText) && passwordText.length() == 6)
                {
                    passwordInput.setError("Please enter a password");
                    isCreateAccount = false;
                }
                else
                {
                    isCreateAccount = true;
                }

                if(isCreateAccount) {
                    //otp = BaseClass.GenerateOtpAndSendMsg();
                    setData();
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if(emailAddressInput.getError() != null && emailAddressInput.getError().length() > 0)
               {
                   emailAddressInput.setError(null);
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordInput.getError() != null && passwordInput.getError().length() > 0)
                {
                    passwordInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mobileNumberInput.getError() != null && mobileNumberInput.getError().length() > 0)
                {
                    mobileNumberInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setData() {
        String cartItems = BaseClass.getStringFromPreferences(SignUpActivity.this, null, Config.CARTITEMS);
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(SignUpActivity.this);
        final String URL = "http://salujacart.usom.co.in/User/AddUser";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("FullName", nameText);
        params.put("Email", emailtext);
        params.put("PhoneNumber", mobileNumberText);
        params.put("Gender", gender);
        params.put("Password", passwordText);
        params.put("productQuantity",cartItems);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();

                            if(response.getBoolean("success") == true) {
                                JSONObject jsonObject = response.getJSONObject("Data");

                                int id = jsonObject.getInt("Id");

                                String userInfo = String.format("%s-;%s-;%s-;%s-;%s", id, nameText, emailtext, mobileNumberText, passwordText);
                                BaseClass.putStringInPreferences(SignUpActivity.this, userInfo, Config.USERINFO);
                                Toast.makeText(SignUpActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, HomePage.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_out_right
                                        , android.R.anim.slide_in_left);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this, "Sign up fail", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(SignUpActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this,
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



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.male:
                if (checked)
                    gender = "male";
                    break;
            case R.id.female:
                if (checked)
                    gender = "female";
                    break;
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
