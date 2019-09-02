package com.usomandroidproject.salujaecommerce;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

public class AddressActivity extends AppCompatActivity {
    EditText fullname, mobileNumber, pincode, town, state, flat, sector, landmark;
    String _fullname, _mobileNumber, _pincode, _town, _state, _flat, _sector, _landmark;
    Button deliverToAddress;
    ShapeDrawable shape;
    boolean procedToPay = true;
    double totalAmount;
    List<Product.Cart> productCartList;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    PaymentModel paymentModel = null;
    ProgressDialog progressDialog;
    User user = null;
    boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Add Address");
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        fullname = (EditText) findViewById(R.id.addressName);
        mobileNumber = (EditText) findViewById(R.id.addressMobileNumber);
        pincode = (EditText) findViewById(R.id.addressPincode);
        town = (EditText) findViewById(R.id.addressCity);
        state = (EditText) findViewById(R.id.stateAddress);
        sector = (EditText) findViewById(R.id.areaAddress);
        flat = (EditText) findViewById(R.id.mainAddress);
        landmark = (EditText) findViewById(R.id.landMark);
        deliverToAddress = (Button) findViewById(R.id.deliverToAddress);
        settings = getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE);
        progressDialog = new ProgressDialog(AddressActivity.this);
        progressDialog.setMessage("Saving Data...");
        progressDialog.setCancelable(false);
        totalAmount = getIntent().getIntExtra("Amount", 0);
        shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(5);
        ((Cache) getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
        deliverToAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procedToPay = true;
                _fullname = fullname.getText().toString();
                _flat = flat.getText().toString();
                _mobileNumber = mobileNumber.getText().toString();
                _pincode = pincode.getText().toString();
                _town = town.getText().toString();
                _state = state.getText().toString();
                _sector = sector.getText().toString();

                if (_fullname.equals("")) {
                    setErrorColor(fullname);
                    procedToPay = false;
                }
                if (_mobileNumber.equals("")) {
                    setErrorColor(mobileNumber);
                    procedToPay = false;
                }
                if (_pincode.equals("")) {
                    setErrorColor(pincode);
                    procedToPay = false;
                }
                if (_town.equals("")) {
                    setErrorColor(town);
                    procedToPay = false;
                }
                if (_flat.equals("")) {
                    setErrorColor(flat);
                    procedToPay = false;
                }

                if (procedToPay) {
                    //deliverToAddress.setEnabled(false);
                    launchPayUMoneyFlow();
                }
            }
        });

    }


    public void setErrorColor(EditText editText) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        int jellyBean = android.os.Build.VERSION_CODES.JELLY_BEAN;
        if (sdk < jellyBean) {
            editText.setBackgroundDrawable(shape);
        } else {
            editText.setBackground(shape);
        }
    }

    public String getProductInfo() {
        productCartList = ((Cache) getApplicationContext()).getCartProduct();

        String cartName = "";

        for (Product.Cart cart : productCartList
        ) {
            cartName = cartName + ", " + cart.getName();

        }

        cartName = cartName.replaceFirst("^,", "").trim();
        return cartName;
    }

    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText(getResources().getString(R.string.donePaymentButtonText));

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle(getResources().getString(R.string.paymentActivityTitle));

        payUmoneyConfig.disableExitConfirmation(false);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        String txnId = System.currentTimeMillis() + "";
        //String txnId = "TXNID720431525261327973";
        String userInfo = BaseClass.getStringFromPreferences(AddressActivity.this, null, Config.USERINFO);
        user = BaseClass.convertStringToUser(userInfo);
        String phone = _mobileNumber.trim();
        String productName = getProductInfo();
        String firstName = user.getName();
        String email = user.getEmail();
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        //Toast.makeText(this, phone+","+productName+","+firstName+","+email, Toast.LENGTH_SHORT).show();
        AppEnvironment appEnvironment = ((Cache) getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(totalAmount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, AddressActivity.this,
                    R.style.AppTheme_Green, true);

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((Cache) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
            String message = "";
            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    message = "Tansaction Successful";
                    deliverToAddress.setEnabled(true);
                    isSuccess = true;
                    String payuResponse = transactionResponse.getPayuResponse();
                    paymentModel = BaseClass.getPaymentData(payuResponse, AddressActivity.this);

                    savePaymentData();
                } else {
                    //Failure Transaction
                    message = "Transaction Failure";
                    deliverToAddress.setEnabled(true);
                    isSuccess = false;
                    new AlertDialog.Builder(AddressActivity.this)
                            .setCancelable(false)
                            .setMessage("Transaction Failed. Retry")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();

                }

                // Response from Payumoney

//                // Response from SURl and FURL
//                String merchantResponse = transactionResponse.getTransactionDetails();

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, resultModel.getError().getTransactionResponse() + "", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Both objects are null!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void savePaymentData() {
        progressDialog.show();
        RequestQueue rq = Volley.newRequestQueue(AddressActivity.this);
        final String URL = "http://salujacart.usom.co.in/Order/AddNewOrder";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("PaymentType", paymentModel.getPaymentType());
        params.put("Amount", String.valueOf(paymentModel.getAmount()));
        params.put("Status", paymentModel.getStatus());
        params.put("FirstName", paymentModel.getFirstName());
        params.put("TaxId", paymentModel.getTaxId());
        params.put("Hash", paymentModel.getHash());
        params.put("ProductInfo", paymentModel.getProductInfo());
        params.put("Mobile", paymentModel.getMobile());
        params.put("Email", paymentModel.getEmail());
        params.put("PayuMoneyId", paymentModel.getPayuMoneyId());

        params.put("UserId", String.valueOf(user.getId()));

        params.put("FullName", _fullname);
        params.put("MobileNumber", _mobileNumber);
        params.put("PinCode", _pincode);
        params.put("TownCity", _town);
        params.put("HouseNo", _flat);
        params.put("Area", _sector);
        params.put("Landmark", _landmark);
        params.put("IsDelivered", String.valueOf(isSuccess));
        params.put("TransactionId", paymentModel.getTaxId());

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            ((Cache) getApplicationContext()).setCount(0);
                            new AlertDialog.Builder(AddressActivity.this)
                                    .setCancelable(false)
                                    .setMessage("Transaction Successfull. Shop for More Items")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(AddressActivity.this, HomePage.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.slide_out_right
                                                    , android.R.anim.slide_in_left);
                                            finish();
                                        }
                                    }).show();


                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(AddressActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AddressActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(AddressActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddressActivity.this,
                            "Oops. Timeout error!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        req.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
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
}
