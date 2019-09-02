package com.usomandroidproject.salujaecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.payu.custombrowser.upiintent.Payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class BaseClass {

    public static List<SearchCriteria> getFavoriteList(Activity activity) {
        String favoriteList = getStringFromPreferences(activity, null, Config.PREFERRED_SEARCH);
        return convertStringToArray(favoriteList);
    }


    public static boolean addFavoriteItem(Activity activity, String favoriteItem) {
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(activity, null, Config.PREFERRED_SEARCH);
        // Append new Favorite item
        if (favoriteList != null) {
            favoriteList = favoriteList + "," + favoriteItem;
        } else {
            favoriteList = favoriteItem;
        }
        // Save in Shared Preferences
        return putStringInPreferences(activity, favoriteList, Config.PREFERRED_SEARCH);
    }

    public static boolean putStringInPreferences(Activity activity, String nick, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Config.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, nick);
        editor.commit();
        return true;
    }

    public static String getStringFromPreferences(Activity activity, String defaultValue, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Config.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }

    private static List<SearchCriteria> convertStringToArray(String str) {
        List<String> myStringList = new ArrayList<String>();
        List<SearchCriteria> city = new ArrayList<>();

        if (str != null) {
            String[] arr = str.split(",");
            for (String item : arr
            ) {
                String[] cityDetail = item.split("-");
                int cityId = Integer.parseInt(cityDetail[0]);
                String cityTitle = cityDetail[1];
                String type = cityDetail[2];
                city.add(new SearchCriteria(cityId, cityTitle, type));
            }
        }
        return city;
    }

    public static List<Product> updateQuantityInProduct(List<Product> cartProducts, Activity context) {
        Map<Integer, Integer> productQuantityList = getIntegerIntegerMap(context);

        for (Product item : cartProducts
        ) {

            int quantity = productQuantityList.get(item.getId());
            item.setQuantity(quantity);
        }
        // check if quantity is updated in cart cache
        return cartProducts;
    }

    public static void removeProductFromCart(Activity activity, int productId)
    {
        Map<Integer, Integer> productQuantityList = getIntegerIntegerMap(activity);
        productQuantityList.remove(productId);

        Set<Integer> set = productQuantityList.keySet();
        String cartItens = "";

        for (int value : set
        ) {

            Integer quantityGet = productQuantityList.get(value);
            cartItens = cartItens + "," + value + ";" + quantityGet;
        }

        cartItens = cartItens.replaceAll("^,+", "");

        putStringInPreferences(activity, cartItens, Config.CARTITEMS);
    }

    public static int updateUserCount(Context context, boolean isRemove) {
        int count = ((Cache) context.getApplicationContext()).getCount();
        if(isRemove)
        {
            count = count - 1;
        }
        else {
            count = count + 1;
        }
        ((Cache) context.getApplicationContext()).setCount(count);
        return count;
    }

    public static Map<Integer, Integer> getIntegerIntegerMap(Activity context) {
        String productQuantity = getStringFromPreferences(context, null, Config.CARTITEMS);

        Map<Integer, Integer> productQuantityList = new HashMap<>();
        if(productQuantity != null) {
            String arr[] = productQuantity.split(",");

            for (String item : arr
            ) {
                String[] productId = item.split(";");
                productQuantityList.put(Integer.parseInt(productId[0]), Integer.parseInt(productId[1]));
            }
        }
        return productQuantityList;
    }

    public static boolean addCartItem(Activity activity, String favoriteItem) {
        //Get previous favorite items
        String cartList = getStringFromPreferences(activity, null, Config.CARTITEMS);
        // Append new Favorite item
        if (cartList != null) {
            cartList = cartList + "," + favoriteItem;
        } else {
            cartList = favoriteItem + "";
        }
        // Save in Shared Preferences
        return putStringInPreferences(activity, cartList, Config.CARTITEMS);
    }

    public static void editCartItem(int productId, int quantity, Activity activity) {
        Map<Integer, Integer> productQuantityList = getIntegerIntegerMap(activity);
        productQuantityList.put(productId, quantity);

        Set<Integer> set = productQuantityList.keySet();
        String cartItens = "";

        for (int value : set
        ) {

            Integer quantityGet = productQuantityList.get(value);
            cartItens = cartItens + "," + value + ";" + quantityGet;
        }

        cartItens = cartItens.replaceAll("^,+", "");

        putStringInPreferences(activity, cartItens, Config.CARTITEMS);
    }

    public static String getProductFromString(String str) {
        String product = "";

        if (str != null) {
            String[] arr = str.split(",");
            for (String item : arr
            ) {
                String[] productId = item.split(";");
                product = product + "," + productId[0];
            }

            product = product.replaceFirst("^,", "");
        }
        return product;
    }

    public static PaymentModel getPaymentData(String response, Context context) {

        PaymentModel paymentModel = null;
        try {
            JSONObject job = new JSONObject(response);
            JSONObject result = job.getJSONObject("result");
            String paymentType = result.getString("mode");
            Double amount = Double.parseDouble(result.getString("amount"));
            String status = result.getString("status");
            String name = String.format("%s &s", result.getString("firstname"), result.getString("lastname"));
            String txnId = result.getString("txnid");
            String hash = result.getString("hash");
            String prodInfo = result.getString("productinfo");
            String mobile = result.getString("phone");
            String email = result.getString("email");
            String paymentId = result.getString("paymentId");

            paymentModel = new PaymentModel(paymentType, amount, status, name, txnId, hash, prodInfo, mobile, email, paymentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paymentModel;
    }

    public static List<Product.Cart> getProductForCart(String response, Context context) {
        JSONObject job1;
        JSONArray job;
        List<Product.Cart> products = new ArrayList<>();

        try {
            job1 = new JSONObject(response);

            JSONArray productArray = job1.getJSONArray("Data");
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject jobProduct = productArray.getJSONObject(i);
                int quantity = jobProduct.getInt("Quantity");
                int id = jobProduct.getInt("Id");
                Product product = null;
                JSONObject productDetail = jobProduct.getJSONObject("Product");
                product = getProductFromJson1(productDetail);


                product.setQuantity(quantity);
                Product.Cart cart = new Product.Cart(id, product.getName(), product.getPrice(), product.getId(), product.getOfferPrice(),
                        product.getPerOff(), quantity, product.getProductCategoryId(), product.getProductCategoryName(),
                        product.getIconImageFullPath(), product.getNumberOfProductInStock(), product.getDescription());

                products.add(cart);
            }

            ((Cache) context.getApplicationContext()).setCartProduct(products);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static void getOrders(String response, Context context) {
        JSONObject job1;
        List<OrderItems> orderItems = new ArrayList<>();
        List<Order> order = new ArrayList<>();

        try {
            job1 = new JSONObject(response);

            JSONArray productArray = job1.getJSONArray("data");
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject jobOrder = productArray.getJSONObject(i);
                String area = jobOrder.getString("Area");
                String fullName = jobOrder.getString("FullName");
                String houseNo = jobOrder.getString("HouseNo");
                String pinCode = jobOrder.getString("PinCode");
                String townCity = jobOrder.getString("TownCity");
                String landMark = jobOrder.getString("Landmark");
                int id = jobOrder.getInt("Id");
                String state = jobOrder.getString("State");
                int totalAmount = jobOrder.getInt("TotalAmount");
                String creationDate = jobOrder.getString("CreationDateFormatedString");
                boolean isDelivered = jobOrder.getBoolean("IsDelivered");
                JSONArray orderItem = jobOrder.getJSONArray("OrderItems");

                for(int j = 0; j < orderItem.length(); j++)
                {
                    JSONObject orderItemObject = orderItem.getJSONObject(j);
                    int orderId = orderItemObject.getInt("OrderId");
                    int orderQuantity = orderItemObject.getInt("Quantity");
                    JSONObject productObject = orderItemObject.getJSONObject("Product");
                    String color = productObject.getString("Color");
                    Product product = getProductFromJson1(productObject);
                    OrderItems orderItems1 = new OrderItems(product.getName(),orderId,product.getPrice(),product.getOfferPrice(),
                            orderQuantity,color,"",product.getIconImageFullPath(), isDelivered);

                    orderItems.add(orderItems1);

                }

                Order orderDetail = new Order(id,area,creationDate,"",fullName,houseNo,landMark,pinCode,townCity,
                        state,"",totalAmount);

                order.add(orderDetail);
            }
            ((Cache)context.getApplicationContext()).setOrders(order);
            ((Cache)context.getApplicationContext()).setOrderItems(orderItems);
        }catch (Exception ex)
        {

        }
    }

    public static Bitmap getCompressedBitmap(File file) {
        Bitmap bitmap = null;
        Bitmap bMap = BitmapFactory.decodeFile(file.getAbsolutePath());
        bitmap = getResizedBitmap(bMap, 200);
        return bitmap;
    }

    public static User convertStringToUser(String str) {
        User user = null;
        if (str != null) {

            String[] cityDetail = str.split("-;");
            int cityId = Integer.parseInt(cityDetail[0]);
            String cityTitle = cityDetail[1];
            String email = cityDetail[2];
            String mobile = cityDetail[3];
            String password = cityDetail[4];
            user = new User(cityId, cityTitle, email, mobile, password);

        }
        return user;
    }

    public static void ResolveData(String response, Context context) {
        JSONObject job = null, job1 = null;
        try {
            HashMap<Integer, List<Product>> hashMap = new HashMap<Integer, List<Product>>();
            job1 = new JSONObject(response);
            job = job1.getJSONObject("result");
            JSONArray colorArray = job1.getJSONArray("colorList");
            JSONArray productArray = job.getJSONArray("Product");
            String banner = job.getString("TopBanner");
            JSONArray sliderBanner = job.getJSONArray("SliderBanner");
            JSONArray brands = job.getJSONArray("Brands");
            JSONArray prodCategory = job.getJSONArray("ProductCategory");
            int count = job.getInt("UserCartDetailCount");

            ((Cache)context.getApplicationContext()).setCount(count);
            List<String> bannerUrls = new ArrayList<>();
            List<Brand> brandList = new ArrayList<>();
            List<Brand> categoryList = new ArrayList<>();
            List<Brand> colorList = new ArrayList<>();

            for (int i = 0; i < sliderBanner.length(); i++) {
                bannerUrls.add(sliderBanner.getString(i));
            }

            for (int i = 0; i < colorArray.length(); i++) {
               JSONObject colorObject = colorArray.getJSONObject(i);
               int colorId = colorObject.getInt("Id");
               String colorName = colorObject.getString("Name");
               Brand colorBrand = new Brand(colorId, colorName,"");
               colorList.add(colorBrand);
            }

            for (int i = 0; i < brands.length(); i++) {
                Brand brand = null;
                JSONObject brandObject = brands.getJSONObject(i);
                brand = getBrandsFromJson(brandObject);
                brandList.add(brand);
            }

            for (int i = 0; i < prodCategory.length(); i++) {
                Brand category = null;
                JSONObject brandObject = prodCategory.getJSONObject(i);
                category = getBrandsFromJson(brandObject);
                categoryList.add(category);
            }

            for (int i = 0; i < productArray.length(); i++) {
                JSONObject jObject = productArray.getJSONObject(i);
                int categoryId = jObject.getInt("ProductCategoryId");
                Product product = getProductFromJson(jObject);
                if (!hashMap.containsKey(categoryId)) {
                    List<Product> list = new ArrayList<Product>();
                    list.add(product);

                    hashMap.put(categoryId, list);
                } else {
                    hashMap.get(categoryId).add(product);
                }
            }
            ((Cache) context.getApplicationContext()).setHashMap(hashMap);
            ((Cache) context.getApplicationContext()).setBanner(banner);
            ((Cache) context.getApplicationContext()).setBannerSlider(bannerUrls);
            ((Cache) context.getApplicationContext()).setBrandList(brandList);
            ((Cache) context.getApplicationContext()).setCategoryLit(categoryList);
            ((Cache) context.getApplicationContext()).setColorList(colorList);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getStringFromList(List<Integer> listInt)
    {

        StringBuilder namesStr = new StringBuilder();
        for(Integer name : listInt)
        {
            namesStr = namesStr.length() > 0 ? namesStr.append(",").append(name) : namesStr.append(name);
        }

        return namesStr.toString();

    }

    public static Map<Integer, List<Product>> getGridProduct(String response, Context context, boolean getMore) {
        JSONObject job1 = null;
        int totalNumber = 0;
        List<Product> products = null;
        Map<Integer, List<Product>> mapReturn = new HashMap<>();

        try {
            HashMap<Integer, List<Product>> hashMap = new HashMap<Integer, List<Product>>();
            job1 = new JSONObject(response);
            JSONArray job = job1.getJSONArray("Data");
            totalNumber = job1.getInt("TotalNoOfRecords");

            products = new ArrayList<>();

            for (int i = 0; i < job.length(); i++) {
                JSONObject jObject = job.getJSONObject(i);
                Product product = getProductFromJson1(jObject);
                products.add(product);
            }

            List<Product> cacheProductList = null;
            if (getMore) {
                cacheProductList = ((Cache) context.getApplicationContext()).getProductList();
                cacheProductList.addAll(products);
            } else {
                cacheProductList = new ArrayList<>();
                cacheProductList.addAll(products);
            }
            ((Cache) context.getApplicationContext()).setProductList(cacheProductList);

            mapReturn.put(totalNumber, products);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return mapReturn;
    }

    public static Brand getBrandsFromJson(JSONObject jObject) {
        Brand brand = null;
        try {
            int id = jObject.getInt("Id");
            String title = jObject.getString("Title");
            String ImgUrl = jObject.getString("ImageAppFullPath");
            brand = new Brand(id, title, ImgUrl);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return brand;
    }

    public static Product getProductFromJson(JSONObject jObject) {
        Product product = null;
        try {
            product = getProductFromJson1(jObject);
            String productCategoryName = jObject.getString("ProductCategoryName");
            product.setProductCategoryName(productCategoryName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return product;
    }

    public static Product getProductFromJson1(JSONObject jObject) {
        Product product = null;
        try {
            int id = jObject.getInt("Id");
            String title = jObject.getString("Title");
            double price = jObject.getDouble("ActualPrice");
            double perOff = jObject.getDouble("PercentageOff");
            double offerPrice = price * (1 - perOff / 100);
            String imageUrl = jObject.getString("IconImageFullPath");
            int productCategoryId = jObject.getInt("ProductCategoryId");
            String description = jObject.getString("Description");
            int numberofProductInStock = jObject.getInt("NumberOfProductInStock");

            product = new Product(title, price, id, offerPrice, perOff, 0, productCategoryId, "", imageUrl
                    , numberofProductInStock, description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return product;
    }


    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        int length = phone.length();
        if (phone.contains("+91")) {
            length = phone.length() - 3;
        }
        if (length == 10) {
            return true;
        } else {
            return false;
        }
    }

    public static String GenerateOtpAndSendMsg() {
        String otp = "9999";
        Random rand = new Random();
        int random = (int) (Math.random() * 9000) + 1000;
        otp = String.valueOf(random);
        return otp;
        //send otp
    }
}

