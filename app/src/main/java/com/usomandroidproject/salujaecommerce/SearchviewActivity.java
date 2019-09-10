package com.usomandroidproject.salujaecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchviewActivity extends AppCompatActivity {
    ListView list;
    EditText et;
    ArrayList<SearchCriteria>  cityList;
    List<SearchCriteria> preferredCityList;
    SearchAdapter adapter;
    ImageButton imageButton, searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_searchview);
        list = (ListView) findViewById(R.id.listView);
        et = (EditText) findViewById(R.id.searchText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imageButton = (ImageButton) findViewById(R.id.clearEditText);
        searchButton = (ImageButton) findViewById(R.id.searchEditText);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });

        et.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            return false;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        preferredCityList = BaseClass.getFavoriteList(this);

        if (preferredCityList != null) {
            adapter = new SearchAdapter(preferredCityList, this);
            list.setAdapter(adapter);
            cityList = new ArrayList<>();
            cityList.addAll(preferredCityList);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(cityList.size() > 0) {
                    SearchCriteria city = cityList.get(position);
                    storeCityPrefrences(city);
                    performSelectionAction(city);
                }
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.equals("")) {
                    imageButton.setVisibility(View.VISIBLE);
                }
                searchInCity(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void searchInCity(CharSequence city) {

        if (city != null && !city.toString().equals("")) {
            RequestQueue rq = Volley.newRequestQueue(SearchviewActivity.this);
            StringRequest sr = new StringRequest(Request.Method.GET,
                    "http://salujacart.usom.co.in/Product/GetGlobalSearchResult?searchText=" + city,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //progressBar.setVisibility(View.INVISIBLE);
                                JSONObject job = new JSONObject(response);
                                JSONArray cityArray = job.getJSONArray("data");
                                cityList = new ArrayList<>();
                                for (int i = 0; i < cityArray.length(); i++) {
                                    JSONObject job1 = cityArray.getJSONObject(i);
                                    int type = job1.getInt("ResultType");
                                    String title = job1.getString("SearchResult");
                                    int id = job1.getInt("Id");

                                    cityList.add(new SearchCriteria(id, title, type));
                                }

                                if(cityList.size() == 0)
                                {
                                    List<String> textNoRecord = new ArrayList<>();
                                    textNoRecord.add("No Record Found");
                                    ArrayAdapter adapter = new ArrayAdapter(SearchviewActivity.this,
                                            android.R.layout.simple_list_item_1, textNoRecord);
                                    list.setAdapter(adapter);
                                }
                                else {
                                    adapter = new SearchAdapter(cityList, SearchviewActivity.this);
                                    list.setAdapter(adapter);
                                }

                            } catch (Exception e) {

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //progressBar.setVisibility(View.GONE);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError
                            || error instanceof NetworkError) {
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(SearchviewActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(SearchviewActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SearchviewActivity.this,
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
        if (city.toString().equals("") && preferredCityList != null) {
            cityList = new ArrayList<>();
            cityList.addAll(preferredCityList);
            adapter = new SearchAdapter(cityList, this);
            list.setAdapter(adapter);
        }

    }

    public void storeCityPrefrences(SearchCriteria selectedCity) {
        boolean isPresent = false;
        for (SearchCriteria item : preferredCityList
        ) {
            if (item.getId() == selectedCity.getId() && item.getType() == selectedCity.getType()) {
                isPresent = true;
            }
        }

        if (!isPresent) {
            String value = String.format("%s-%s-%s",selectedCity.getId(),selectedCity.getTitle(),selectedCity.getType());
            //Toast.makeText(this, value+"", Toast.LENGTH_SHORT).show();
            BaseClass.addFavoriteItem(this, value);
        }

    }

    public void performSelectionAction(SearchCriteria city) {
        hideSoftWindow();
        Intent intentCity = new Intent(SearchviewActivity.this, GridViewActivity.class);
        //intentCity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch(city.getType())
        {
            case 1 : intentCity.putExtra("CategoryId", city.getId());
            break;

            case 2 :
                intentCity.putExtra("BrandId", city.getId());
            break;

            case 3 : intentCity.putExtra("ProductId", city.getId());
            break;
        }

        startActivity(intentCity);
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_out_right);
        finish();
    }

    public void hideSoftWindow()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
