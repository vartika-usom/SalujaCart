package com.usomandroidproject.salujaecommerce;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class UploadImageService extends IntentService {
    String imageString, id;
    SharedPreferences sharedPrefrences;

    public UploadImageService() {
        // Used to name the worker thread, important only for debugging.
        super("upload-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        id = intent.getExtras().getString("id");
        imageString = intent.getExtras().getString("image_string");
        uploadImageToServer();
    }

    public void uploadImageToServer() {
        //update status to vacant.
        sharedPrefrences = getApplicationContext()
                .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        RequestQueue rq = Volley.newRequestQueue(UploadImageService.this);
        // make a post request to the server
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST,
                "http://salujacart.usom.co.in/User/UploadUserImage", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreferences.Editor editor = sharedPrefrences.edit();
                editor.putString(Config.LOGGEDINIMGURL, response);
                editor.commit();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("base64Image", imageString);
                params.put("userId", id);
                params.put("extension","png");
                return params;
            }
        };
        stringPostRequest.setRetryPolicy(new RetryPolicy() {
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
        rq.add(stringPostRequest);
    }
}

