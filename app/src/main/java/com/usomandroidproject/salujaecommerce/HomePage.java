package com.usomandroidproject.salujaecommerce;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView MyRecyclerView, verticalCategoryRecycler;
    ViewPager sliderBannerRecycler;
    List<Brand> brandSliderList, categoryList;
    ImageView bannerImage;
    String bannerUrl;
    List<String> imageSlider;
    HashMap<Integer, List<Product>> categoryWiseProduct;
    List<Integer> categoryIds;
    ImageView profileImage, shoppingCart;
    TextView navTextView, cartCount;
    User user;
    String userInfo = null, imgLocalPath = "", imgurl;
    SharedPreferences sharedPrefrences;
    Bitmap bitmap;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        bannerImage = (ImageView) findViewById(R.id.bannerImage);
        sliderBannerRecycler = (ViewPager) findViewById(R.id.sliderBannerRecycler);
        verticalCategoryRecycler = (RecyclerView) findViewById(R.id.verticalCategorySlider);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        navTextView = (TextView) headerView.findViewById(R.id.loginTextView);
        shoppingCart = (ImageView) findViewById(R.id.shoppingCart);
        cartCount = (TextView) findViewById(R.id.cartCount);
        profileImage = (ImageView) headerView.findViewById(R.id.profileImage);
        sharedPrefrences = HomePage.this
                .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        imgLocalPath = sharedPrefrences.getString(Config.LOGGEDINLOCALIMAGE, "");
//        setSupportActionBar(toolbar);

        userInfo = BaseClass.getStringFromPreferences(HomePage.this, null, Config.USERINFO);

        if (userInfo != null) {
            user = BaseClass.convertStringToUser(userInfo);
            addMenuItemInNavMenuDrawer();
            navTextView.setText("Hello " + user.getName());
            updateImage();
        } else {
            //String cartItems = BaseClass.getStringFromPreferences(HomePage.this, null, Config.CARTITEMS);
            Map<Integer, Integer> cartProducts = BaseClass.getIntegerIntegerMap(HomePage.this);
            int i = cartProducts.size();

            ((Cache) getApplicationContext()).setCount(i);
        }

        navTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo == null) {
                    Intent intent = new Intent(HomePage.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        cartCount.setText(String.valueOf(((Cache) getApplicationContext()).getCount()));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        MyRecyclerView = (RecyclerView) findViewById(R.id.cardViewRecycle);
        MyRecyclerView.setHasFixedSize(true);
        verticalCategoryRecycler.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(HomePage.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager SliderLayoutManager = new LinearLayoutManager(HomePage.this);
        SliderLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager ListLayoutManager = new LinearLayoutManager(HomePage.this);
        ListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        brandSliderList = new ArrayList<>();
        imageSlider = new ArrayList<>();
        categoryWiseProduct = new HashMap<>();
        categoryIds = new ArrayList<>();
        brandSliderList = ((Cache) getApplicationContext()).getBrandList();
        bannerUrl = ((Cache) getApplicationContext()).getBanner();
        imageSlider = ((Cache) getApplicationContext()).getBannerSlider();
        categoryWiseProduct = ((Cache) getApplicationContext()).getHashMap();

        shoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, CartListActivity.class);
                startActivity(intent);
            }
        });

        categoryIds = new ArrayList<Integer>(categoryWiseProduct.keySet());

        Glide.with(HomePage.this).load(bannerUrl)
                .placeholder(R.drawable.ic_default)
                .error(R.drawable.ic_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(bannerImage);

        if (brandSliderList.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyRecyclerAdapter(brandSliderList, HomePage.this));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        MyRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        if (imageSlider.size() > 0 & imageSlider != null) {
//            sliderBannerRecycler.setAdapter(new SliderRecyclerAdapter(imageSlider, HomePage.this));
//        }
//
//        sliderBannerRecycler.setLayoutManager(SliderLayoutManager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageSlider);

        sliderBannerRecycler.setAdapter(viewPagerAdapter);

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == imageSlider.size()) {
                    currentPage = 0;
                }
                sliderBannerRecycler.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        if (categoryWiseProduct.size() > 0 & categoryWiseProduct != null) {
            verticalCategoryRecycler.setAdapter(new CategoryRecyclerAdapter(HomePage.this,
                    categoryWiseProduct, categoryIds));
        }

        verticalCategoryRecycler.setLayoutManager(ListLayoutManager);
        verticalCategoryRecycler.setNestedScrollingEnabled(false);

    }

    public void updateImage() {
        imgurl = sharedPrefrences.getString(Config.LOGGEDINIMGURL, "");
        File file = new File(imgLocalPath);

        if (file.exists()) {
            if (bitmap == null) {
                bitmap = BaseClass.getCompressedBitmap(file);
            }
            profileImage.setImageBitmap(bitmap);
        } else {
            Glide.with(getBaseContext()).load(imgurl)
                    .placeholder(R.drawable.ic_profile_set).into(profileImage);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           showDialogYesNo();
        }
    }


    // show yes no dialog
    private void showDialogYesNo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
        builder.setMessage("Are you sure want to exit?");
        builder.setCancelable(true);
        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.finishAffinity(HomePage.this);
            }
        });
        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMenuItemInNavMenuDrawer() {

        Menu menu = navigationView.getMenu();
        MenuItem submenu = menu.findItem(R.id.nav_logout);
        submenu.setVisible(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            Intent intent = new Intent(HomePage.this, CategoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_order) {
            if (userInfo != null) {
                Intent intent = new Intent(HomePage.this, OrderActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_account) {
            if (userInfo != null) {
                Intent intentUpdate = new Intent(HomePage.this, UpdateProfileActivity.class);
                startActivity(intentUpdate);
            } else {
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Saluja Cart");
                String shareMessage = "\nInstall Saluja Cart from the link given\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +
                        BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_policy) {

        } else if (id == R.id.nav_logout) {
            if (userInfo != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomePage.this)
                        .setTitle("Logout")
                        .setCancelable(true)
                        .setMessage(HomePage.this.getResources().getString(R.string.logout_msg))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPrefrences = HomePage.this
                                        .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPrefrences.edit();
                                editor.clear();
                                editor.commit();
                                //editor.putString(Config.LOGGEDINLOCALIMAGE, imgLocalPath);
                                //editor.commit();
                                ((Cache) getApplicationContext()).setCount(0);
                                Toast.makeText(HomePage.this, "You have successfully been logged out", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent main = new Intent(HomePage.this, LoadingActivity.class);
                                startActivity(main);
                                overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                            }
                        })
                        .setNegativeButton("No", null).show();
            }}else if(id == R.id.nav_rate)
            {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            else if(id == R.id.nav_aboutus)
            {
                Intent about = new Intent(HomePage.this, AboutUs.class);
                startActivity(about);
            }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartCount.setText(String.valueOf(((Cache) getApplicationContext()).getCount()));
        String userInfo = BaseClass.getStringFromPreferences(HomePage.this, null, Config.USERINFO);
        if (userInfo != null) {
            user = BaseClass.convertStringToUser(userInfo);
            navTextView.setText("Hello " + user.getName());
            addMenuItemInNavMenuDrawer();
            bitmap = null;
            updateImage();
        }
    }
}
