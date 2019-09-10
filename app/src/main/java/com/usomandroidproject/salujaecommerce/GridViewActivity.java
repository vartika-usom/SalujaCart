package com.usomandroidproject.salujaecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GridViewActivity extends AppCompatActivity {

    MyAdapter myAdapter;
    List<Product> mItems, list, currentPage;
    ImageButton backButton, cartButton;
    RelativeLayout relativeLayout, gridRelativeLayout, layout;
    LinearLayout listSort;
    TextView buttonSort, applyButton, cancelApply, gridViewCount, cityNameText, totalItemGrid, button, clearAll;
    ListView sortItems;
    Fragment fragment;
    ProgressDialog progressDialog;
    String categoryId = "", brandId = "", color = "", productId = "";
    int totalCount = 0, minPrice = 0, maxPrice = 0;
    GridView gridView;
    int index = 0, category, brand, product;
    boolean isLoading = false;
    ImageView searchInGrid;
    String textToDisplay, orderBy = "";
    boolean isAscending = false;
    List<Integer> filteredIds;
    HashMap<Integer, List<Integer>> filterValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        gridView = (GridView) findViewById(R.id.gridview);
        backButton = (ImageButton) findViewById(R.id.backButton);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeSearch);
        cartButton = (ImageButton) findViewById(R.id.shoppingCart);
        button = (TextView) findViewById(R.id.filterGrid);
        listSort = (LinearLayout) findViewById(R.id.sortListLayout);
        buttonSort = (TextView) findViewById(R.id.sortGrid);
        layout = (RelativeLayout) findViewById(R.id.filter);
        sortItems = (ListView) findViewById(R.id.sortListItems);
        applyButton = (TextView) findViewById(R.id.ApplyButton);
        gridRelativeLayout = (RelativeLayout) findViewById(R.id.gridRelativeLayout);
        cancelApply = (TextView) findViewById(R.id.cancelApply);
        gridViewCount = (TextView) findViewById(R.id.cartCountGrid);
        cityNameText = (TextView) findViewById(R.id.cityNametext);
        totalItemGrid = (TextView) findViewById(R.id.totalItemsGrid);
        searchInGrid = (ImageView) findViewById(R.id.searchInGrid);
        clearAll = (TextView) findViewById(R.id.clearAll);

        progressDialog = new ProgressDialog(GridViewActivity.this);

        progressDialog.setMessage("Loading....Please wait");
        progressDialog.setCancelable(false);

        category = getIntent().getIntExtra("CategoryId", 0);
        brand = getIntent().getIntExtra("BrandId", 0);
        product = getIntent().getIntExtra("ProductId", 0);
        filteredIds = new ArrayList<>();
        clearFilter();
        if (category != 0) {
            categoryId = String.valueOf(category);
            filteredIds.add(category);

            ((Cache) getApplicationContext()).setFilterCategoryIds(filteredIds);
        }
        if (brand != 0) {
            brandId = String.valueOf(brand);
            filteredIds.add(brand);
            ((Cache) getApplicationContext()).setFilterBrandIds(filteredIds);

        }
        if (product != 0) {
            productId = String.valueOf(product);
        }

        fragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        textToDisplay = getIntent().getStringExtra("HeaderTitle");
        cityNameText.setText("SALUJA PRODUCT STORE");
        ResetData();
        mItems = new ArrayList<>();
//        if (categoryId != 0) {
        progressDialog.show();
        index = 0;
        //isCategory = true;
        getData(index, false);


//        } else if (brandId != 0) {
//            progressDialog.show();
//            index = 0;
//            isCategory = false;
//            getData(index, false, isCategory);
//        }

        gridViewCount.setText(String.valueOf(((Cache) getApplicationContext()).getCount()));
        fragment.getView().setBackgroundColor(getResources().getColor(R.color.grey));

        Map<Integer, String> sortedMap = new TreeMap<Integer, String>(((Cache) getApplicationContext()).sortStatus);
        final List<String> sortList = (new ArrayList<String>(sortedMap.values()));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sortList);
        sortItems.setAdapter(adapter);

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
                String[] AndroidOS = new String[]{"Category", "Brand", "Price", "Color"};
                ListView lv = (ListView) fragment.getView().findViewById(R.id.filterListView);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(GridViewActivity.this, android.R.layout.simple_list_item_1, AndroidOS);
                lv.setAdapter(adapter);

                ResetData();
                if (layout.getVisibility() == View.VISIBLE) {
                    Animation slideDowm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    layout.startAnimation(slideDowm);
                    layout.setVisibility(View.INVISIBLE);
                    mItems = new ArrayList<>();
                    progressDialog.show();
                    //isLoading = false;
                    index = 0;
                    getData(0, false);
                }
            }
        });

        sortItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int value = position + 1;
                switch (value) {
                    case 1:
                        orderBy = "ActualPrice";
                        isAscending = false;
                        break;
                    case 2:
                        orderBy = "ActualPrice";
                        isAscending = true;
                        break;
                    case 3:
                        orderBy = "PercentageOff";
                        isAscending = false;
                        break;
                }

                mItems = new ArrayList<>();
                progressDialog.show();
                index = 0;
                //isLoading = false;
                getData(0, false);

                Animation slideDowm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                if (listSort.getVisibility() == View.VISIBLE) {
                    listSort.startAnimation(slideDowm);
                    listSort.setVisibility(View.INVISIBLE);
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

                if (layout.getVisibility() == View.INVISIBLE) {
                    layout.startAnimation(slideUp);
                    layout.setVisibility(View.VISIBLE);
                    layout.requestFocus();
                }
            }
        });

        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                if (listSort.getVisibility() == View.INVISIBLE) {
                    listSort.startAnimation(slideUp);
                    listSort.setVisibility(View.VISIBLE);
                    listSort.requestFocus();
                }
            }
        });

        cancelApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slideDowm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                if (layout.getVisibility() == View.VISIBLE) {
                    layout.startAnimation(slideDowm);
                    layout.setVisibility(View.INVISIBLE);
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> brandIds = ((Cache) getApplicationContext()).getFilterBrandIds();
                List<Integer> categoryIds = ((Cache) getApplicationContext()).getFilterCategoryIds();
                List<Integer> colorIds = ((Cache) getApplicationContext()).getColourIds();
                List<Integer> priceIds = ((Cache) getApplicationContext()).getFilterPriceIds();
                if (brandIds != null && brandIds.size() > 0) {
                    brandId = BaseClass.getStringFromList(brandIds);
                } else {
                    brandId = "";
                }
                if (categoryIds != null && categoryIds.size() > 0) {
                    categoryId = BaseClass.getStringFromList(categoryIds);
                } else {
                    categoryId = "";
                }
                if (colorIds != null && colorIds.size() > 0) {
                    color = BaseClass.getStringFromList(colorIds);
                } else {
                    color = "";
                }
                if (priceIds != null && priceIds.size() > 0) {
                    getMinMaxValue(priceIds);
                } else {
                    minPrice = 0;
                    maxPrice = 0;
                }
                mItems = new ArrayList<>();
                progressDialog.show();
                index = 0;
                //isLoading = false;
                getData(0, false);
                if (layout.getVisibility() == View.VISIBLE) {
                    Animation slideDowm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                    layout.startAnimation(slideDowm);
                    layout.setVisibility(View.INVISIBLE);
                }

            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Toast.makeText(GridViewActivity.this, view.getLastVisiblePosition()+";"+totalCount+";"+isLoading, Toast.LENGTH_SHORT).show();
                if (view.getLastVisiblePosition() == totalItemCount - 3 && totalCount > gridView.getCount()
                        && view.getLastVisiblePosition() != -1) {

                    //isLoading = true;
                    loadNextDataFromApi();

                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent product = new Intent(GridViewActivity.this, DescriptionActivity.class);
                // Toast.makeText(GridViewActivity.this, position+";"+mItems.size(), Toast.LENGTH_SHORT).show();
                product.putExtra("ProductId", mItems.get(position).getId());
                startActivity(product);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GridViewActivity.this, CartListActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
                finish();
            }
        });

        searchInGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GridViewActivity.this, SearchviewActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);
                finish();
            }
        });
    }

    public void clearFilter() {
        //List<Integer> newBrand = new ArrayList<Integer>();
        ((Cache) getApplicationContext()).setFilterBrandIds(new ArrayList<Integer>());
        ((Cache) getApplicationContext()).setFilterCategoryIds(new ArrayList<Integer>());
        ((Cache) getApplicationContext()).setColourIds(new ArrayList<Integer>());
        ((Cache) getApplicationContext()).setFilterPriceIds(new ArrayList<Integer>());

        //Toast.makeText(this, ((Cache) getApplicationContext()).getFilterPriceIds().size()+"", Toast.LENGTH_SHORT).show();
        brandId = "";
        categoryId = "";
        minPrice = 0;
        maxPrice = 0;
        color = "";

    }

    public void ResetData() {
        FilterSubCategory listFragment = (FilterSubCategory) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        List<Brand> categoryList = ((Cache) getApplicationContext()).getCategoryLit();
        List<Integer> selectedIds = ((Cache) getApplicationContext()).getFilterCategoryIds();
        listFragment.change(categoryList, selectedIds, 0);
    }

    public void getMinMaxValue(List<Integer> priceIds) {
        minPrice = 0;
        maxPrice = 0;
        Map<Integer, String> map = ((Cache) getApplicationContext()).priceList;
        List<Integer> valueArray = new ArrayList<>();
        int firstValue = 0;
        for (Integer id : priceIds
        ) {

            String value = map.get(id);
            if (value.contains("-")) {
                String[] values = value.split("-");
                firstValue = Integer.parseInt(values[0].trim());
                if (!valueArray.contains(firstValue)) {
                    valueArray.add(firstValue);
                }
                firstValue = Integer.parseInt(values[1].trim());
                if (!valueArray.contains(firstValue)) {
                    valueArray.add(firstValue);
                }

            } else {
                String[] aboveBelow = value.split(" ");
                firstValue = Integer.parseInt(aboveBelow[1]);
                if (!valueArray.contains(firstValue)) {
                    valueArray.add(firstValue);
                }
            }

        }

        if (valueArray.size() == 1) {
            if (valueArray.get(0) == 2000) {
                maxPrice = 2000;
                minPrice = 0;
            } else if (valueArray.get(0) == 10000) {
                minPrice = 10000;
            }
        } else {
            Integer[] array = new Integer[valueArray.size()];
            valueArray.toArray(array);
            Arrays.sort(array);
            minPrice = array[0];
            maxPrice = array[array.length - 1];
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        index = index + 1;

        getData(index, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gridViewCount.setText(String.valueOf(((Cache) getApplicationContext()).getCount()));
    }

    @Override
    public void onBackPressed() {
        if (listSort.getVisibility() == View.VISIBLE) {
            Animation slideDowm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            listSort.startAnimation(slideDowm);
            listSort.setVisibility(View.INVISIBLE);
        } else if (layout.getVisibility() == View.VISIBLE) {
            Animation slideDowm = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            layout.startAnimation(slideDowm);
            layout.setVisibility(View.INVISIBLE);
        } else {
            clearFilter();
            super.onBackPressed();
        }
    }

    public void getData(int pageIndex, final boolean getMore) {

        String url = "";

        if(color != "" && color != null)
        {
            color = BaseClass.colorString(color, GridViewActivity.this);
        }

        url = "http://salujacart.usom.co.in/Product/GetFilteredDataForApp?categoryIds=" + categoryId + "&&productIds="
                + productId + "&&brandIds=" + brandId
                + "&&color=" + color + "&&minPrice=" + minPrice + "&&maxPrice=" + maxPrice + "&&pageIndex=" + pageIndex +
                "&&pageSize=" + 10 + "&&orderBy=" + orderBy + "&&isAscendingOrder=" + isAscending;
//        if (isCategory) {
//            url = "http://salujacart.usom.co.in/Product/GetProductListByCategoryId?categoryId=" + categoryId
//                    + "&&pageIndex=" + pageIndex +
//                    "&&pageSize=" + 10+"&&orderBy="+orderBy+"&&isAscendingOrder="+isAscending;
//        } else {
//            url = "http://salujacart.usom.co.in/Product/GetProductListByBrandId?brandId=" + brandId + "&&pageIndex=" + pageIndex +
//                    "&&pageSize=" + 10+"&&orderBy="+orderBy+"&&isAscendingOrder="+isAscending;
//        }
        RequestQueue rq = Volley.newRequestQueue(GridViewActivity.this);
        StringRequest sr = new StringRequest(Request.Method.GET, url
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            Map<Integer, List<Product>> mapReturn = BaseClass
                                    .getGridProduct(response, GridViewActivity.this, getMore);

                            List<Integer> integers = new ArrayList<Integer>(mapReturn.keySet());

                            totalCount = integers.get(0);
                            totalItemGrid.setText(String.format("%s Items", String.valueOf(totalCount)));

                            currentPage = mapReturn.get(totalCount);
                            mItems.addAll(currentPage);
                            if (getMore) {
                                //appendData(mItems);
                                myAdapter.AddListItemToAdapter(currentPage);
                            } else {
                                list = new ArrayList<>();
                                list.addAll(mItems);
                                myAdapter = new MyAdapter(GridViewActivity.this, list);
                                gridView.setAdapter(myAdapter);
                            }

                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (error instanceof TimeoutError || error instanceof NoConnectionError
                        || error instanceof NetworkError) {
                    Toast.makeText(GridViewActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(GridViewActivity.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(GridViewActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GridViewActivity.this,
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
