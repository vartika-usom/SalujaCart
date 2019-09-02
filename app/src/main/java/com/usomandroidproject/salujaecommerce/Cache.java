package com.usomandroidproject.salujaecommerce;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class Cache extends Application {

    public static Map<Integer, String> sortStatus = new HashMap<Integer, String>() {{
        put(1, "Price-high to low");
        put(2, "Price-low to high");
        put(3, "Discount");
    }};

    //Change in gridViewActivity
    public static Map<Integer, String> priceList = new HashMap<Integer, String>() {{
        put(1, "Below 2000");
        put(2, "2000 - 4000");
        put(3, "4000 - 6000");
        put(4, "6000 - 8000");
        put(5, "8000 - 10000");
        put(6, "Above 10000");
    }};

    private List<Product> productList;

    private List<Brand> colorList;

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    private List<OrderItems> orderItems;

    private List<Order> orders;

    private List<Integer> filterBrandIds;

    private List<Integer> filterCategoryIds;

    private List<Integer> colourIds;

    private List<Integer> filterPriceIds;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count = 0;

    public List<Brand> getBrandList() {
        return brandList;
    }

    public List<Brand> getColorList() {
        return colorList;
    }

    public void setColorList(List<Brand> colorList) {
        this.colorList = colorList;
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    AppEnvironment appEnvironment;

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public List<String> getBannerSlider() {
        return bannerSlider;
    }

    public void setBannerSlider(List<String> bannerSlider) {
        this.bannerSlider = bannerSlider;
    }

    public List<Brand> getCategoryLit() {
        return categoryList;
    }

    public void setCategoryLit(List<Brand> categoryLit) {
        this.categoryList = categoryLit;
    }

    public HashMap<Integer, List<Product>> getHashMap() {
        return categoryWiseProduct;
    }

    public void setHashMap(HashMap<Integer, List<Product>> hashMap) {
        this.categoryWiseProduct = hashMap;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private List<Brand> brandList;

    private String banner;

    private List<String> bannerSlider;

    private List<Brand> categoryList;

    private HashMap<Integer, List<Product>> categoryWiseProduct;

    private HashMap<Integer, List<Integer>> selectedFilterValues;


    public List<Product.Cart> getCartProduct() {
        return cartProduct;
    }

    public void setCartProduct(List<Product.Cart> cartProduct) {
        this.cartProduct = cartProduct;
    }

    private List<Product.Cart> cartProduct;

    private RequestQueue mRequestQueue;

    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static Cache sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;
        appEnvironment = AppEnvironment.SANDBOX;
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized Cache getInstance() {
        return sInstance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public HashMap<Integer, List<Integer>> getSelectedFilterValues() {
        return selectedFilterValues;
    }

    public void setSelectedFilterValues(HashMap<Integer, List<Integer>> selectedFilterValues) {
        this.selectedFilterValues = selectedFilterValues;
    }

    public List<Integer> getFilterBrandIds() {
        return filterBrandIds;
    }

    public void setFilterBrandIds(List<Integer> filterBrandIds) {
        this.filterBrandIds = filterBrandIds;
    }

    public List<Integer> getFilterCategoryIds() {
        return filterCategoryIds;
    }

    public void setFilterCategoryIds(List<Integer> filterCategoryIds) {
        this.filterCategoryIds = filterCategoryIds;
    }

    public List<Integer> getColourIds() {
        return colourIds;
    }

    public void setColourIds(List<Integer> colourIds) {
        this.colourIds = colourIds;
    }

    public List<Integer> getFilterPriceIds() {
        return filterPriceIds;
    }

    public void setFilterPriceIds(List<Integer> filterPriceIds) {
        this.filterPriceIds = filterPriceIds;
    }
}
