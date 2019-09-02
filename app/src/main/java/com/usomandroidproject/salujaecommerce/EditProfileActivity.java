package com.usomandroidproject.salujaecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    TextView passwordEditText;
    EditText nameEditText, emailEditText, phoneEdittext;
    User user;
    Button saveButton;
    String emailtext, passwordText, mobileNumberText, nameText;
    boolean isCreateAccount = true;
    static boolean proceed = true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("EDIT PROFILE");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);
        nameEditText = (EditText) findViewById(R.id.nameEditTextEdit);
        emailEditText = (EditText) findViewById(R.id.emailEditTextEdit);
        phoneEdittext = (EditText) findViewById(R.id.phoneEditTextEdit);
        passwordEditText = (TextView) findViewById(R.id.passwordEditTextEdit);
        saveButton = (Button) findViewById(R.id.buttonCreateEdit);

        String userInfo = BaseClass.getStringFromPreferences(EditProfileActivity.this, null, Config.USERINFO);

        if (userInfo != null) {
            user = BaseClass.convertStringToUser(userInfo);
            nameEditText.setText(user.getName());
            emailEditText.setText(user.getEmail());
            phoneEdittext.setText(user.getPhone());
            passwordEditText.setText(user.getPassword());
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailtext = emailEditText.getText().toString();
                passwordText = passwordEditText.getText().toString();
                mobileNumberText = phoneEdittext.getText().toString();
                nameText = nameEditText.getText().toString();

                if (!BaseClass.isValidMail(emailtext) || TextUtils.isEmpty(emailtext)) {
                    emailEditText.setError("Please enter a valid email address");
                    isCreateAccount = false;
                } else if (!BaseClass.isValidMobile(mobileNumberText) || TextUtils.isEmpty(mobileNumberText)) {
                    phoneEdittext.setError("Please enter a valid mobile number");
                    isCreateAccount = false;
                } else if (TextUtils.isEmpty(passwordText)) {
                    passwordEditText.setError("Please enter a password");
                    isCreateAccount = false;
                } else {
                    isCreateAccount = true;
                }

                if (isCreateAccount) {
//                    if (!mobileNumberText.equals(user.getPhone())) {
//                        String otp = BaseClass.GenerateOtpAndSendMsg();
//                        Intent verify = new Intent(EditProfileActivity.this, VerifyOtpActivity.class);
//                        verify.putExtra("MobileNumber", mobileNumberText);
//                        verify.putExtra("EmailAddress", emailtext);
//                        verify.putExtra("FullName", nameText);
//                        verify.putExtra("Password", passwordText);
//                        verify.putExtra("otp", otp);
//                        //verify.putExtra("Gender",);
//                        startActivity(verify);
//                    } else {
                    EditProfile();
                    //}
                }
            }
        });
    }

    public void EditProfile() {

        progressDialog.show();
        final String URL = "http://salujacart.usom.co.in/User/Edit";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("FullName", nameText);
        params.put("Email", emailtext);
        params.put("PhoneNumber", mobileNumberText);
        params.put("Password", passwordText);
        params.put("Id", String.valueOf(user.getId()));

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();

                            boolean isUpdated = response.getBoolean("success");
                            if (isUpdated) {
                                String userInfo = String.format("%s-;%s-;%s-;%s-;%s", user.getId(),
                                        nameText, emailtext, mobileNumberText, passwordText);
                                BaseClass.putStringInPreferences(EditProfileActivity.this, userInfo, Config.USERINFO);
                                Toast.makeText(EditProfileActivity.this, "Saved Successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(EditProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(EditProfileActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this,
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
        //save user in db
        //move to updateprofileactivity
    }

    public void ConfirmPasswordDialog(View view) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View loginDialogView = factory.inflate(R.layout.confirm_password, null);
        final android.support.v7.app.AlertDialog loginDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        loginDialog.setView(loginDialogView);

        final EditText currentPassword = (EditText) loginDialogView.findViewById(R.id.currentPasswordEdit);
        final EditText newPassword = (EditText) loginDialogView.findViewById(R.id.newPasswordEdit);
        final EditText confirmPassword = (EditText) loginDialogView.findViewById(R.id.confirmPasswordEdit);
        Button saveChanges = (Button) loginDialogView.findViewById(R.id.okSaveConfirm);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPasstext, newPassText, confirmPassText;
                currentPasstext = currentPassword.getText().toString();
                newPassText = newPassword.getText().toString();
                confirmPassText = confirmPassword.getText().toString();

                String password = user.getPassword();
                Toast.makeText(EditProfileActivity.this, currentPasstext, Toast.LENGTH_SHORT).show();
                if (!currentPasstext.equals(password)) {
                    currentPassword.setError("Wrong Password");
                    proceed = false;
                } else if (newPassText.isEmpty() || newPassText.equals("")) {
                    newPassword.setError("Password cannot be blank");
                    proceed = false;
                } else if (newPassText.isEmpty() || confirmPassText.equals("")) {
                    newPassword.setError("Confirm Password cannot be blank");
                    proceed = false;
                } else if (!newPassText.equals(confirmPassText)) {
                    confirmPassword.setError("Confirm password mismatch with new password");
                    proceed = false;
                } else {
                    proceed = true;
                }
                if (proceed) {
                    passwordText = confirmPassText;
                    Toast.makeText(EditProfileActivity.this,
                            "Press Save to change password", Toast.LENGTH_SHORT).show();
                    passwordEditText.setText(passwordText);
                    loginDialog.dismiss();
                }
            }
        });

        loginDialog.show();
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
