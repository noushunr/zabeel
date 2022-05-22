package com.greenflames.myzebeel.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.MainFragmentAdapter;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.category.CategoryModel;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.AboutUsUrl;
import static com.greenflames.myzebeel.helpers.Global.CatId;
import static com.greenflames.myzebeel.helpers.Global.HomeCategoryList;
import static com.greenflames.myzebeel.helpers.Global.KNetUrl;
import static com.greenflames.myzebeel.helpers.Global.PlayStoreBaseUrl;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;
import static com.greenflames.myzebeel.network.Apis.BASE_URL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_EMAIL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_PASSWORD;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        NetworkCallbacks {

    private ImageView  image_wishlist, image_address, image_order,
            image_help, image_home, cart_fruits, share, signout, about_us, profile;

    TabLayout tabLayout;
    ViewPager2 viewPager;
    private List<CategoryModel> homeCategoryList = new ArrayList<>();
    private String mCategoryId;
    private LinearLayout searchMainLayout;
    private EditText searchView;
    private Pref pref;
    private LinearLayout homeNav, wishNav, helpNav, aboutNav, shareNav, profileNav, myOrderNav, signOutLayout,llLanguage;
    private String loginStatus;
    private TextView userNameTxt;

    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cart_btn;

    private Gson gson = new Gson();
    private final int REQUEST_LOGIN = 1001;

    private final Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable;
    private final int delay = 599*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = new Pref(this);

        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        viewPager = (ViewPager2) findViewById(R.id.main_viewPager);

        homeCategoryList = HomeCategoryList;
        mCategoryId = CatId;

        searchMainLayout = (LinearLayout) findViewById(R.id.home_search_main_layout);
        searchView =  findViewById(R.id.searchView_home);

        searchMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        MainFragmentAdapter fragAdapter = new MainFragmentAdapter(
                MainActivity.this, homeCategoryList
        );
        viewPager.setAdapter(fragAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(homeCategoryList.get(position).getName());
                    }
                }).attach();

        checkHomeCategory(mCategoryId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);//your icon here

        cart_btn = findViewById(R.id.imageView_cart);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setVerticalScrollBarEnabled(true);


        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mainIntent = new Intent(MainActivity.this, CartActivity.class);
                MainActivity.this.startActivity(mainIntent);
//                if (loginStatus.equals("true")) {
//                    final Intent mainIntent = new Intent(MainActivity.this, CartActivity.class);
//                    MainActivity.this.startActivity(mainIntent);
//                } else {
//                    Toast.makeText(MainActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
//                    goToLogin();
//                }
            }
        });

        image_wishlist = findViewById(R.id.navigation_wishlist);
        wishNav = findViewById(R.id.nav_wish_layout);
        wishNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent navwishlist = new Intent(MainActivity.this, WishListActivity.class);
                    startActivity(navwishlist);
                } else {
                    Toast.makeText(MainActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });

        profile = findViewById(R.id.imageView_profile);
        profileNav = findViewById(R.id.nav_profile_layout);
        profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent navwishlist = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(navwishlist);
                } else {
                    Toast.makeText(MainActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });

        image_help = findViewById(R.id.help_imageView);
        helpNav = findViewById(R.id.nav_help_layout);
        helpNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent navhelp = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(navhelp);
                finish();
            }
        });

        image_home = findViewById(R.id.imageView_home_home);
        homeNav = findViewById(R.id.nav_home_layout);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        about_us = findViewById(R.id.aboutUs);
        aboutNav = findViewById(R.id.nav_about_layout);
        aboutNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                KNetUrl = AboutUsUrl;
                //Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        share = findViewById(R.id.shareApp);
        shareNav = findViewById(R.id.nav_share_layout);
        shareNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                /*Intent intent = new Intent(MainActivity.this, ShareAppActivity.class);
                startActivity(intent);
                finish();*/
                shareUrl();
            }
        });

        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        userNameTxt = navigationView.getHeaderView(0).findViewById(R.id.nav_header_user_txt);
        if (loginStatus.equals("true")) {
            String userName = pref.getString(PREF_CUSTOMER_FIRST_NAME) + " " + pref.getString(PREF_CUSTOMER_LAST_NAME);
            userNameTxt.setText(userName);
        } else {
            userNameTxt.setText("");
        }

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            cart_btn.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }

        signOutLayout = findViewById(R.id.nav_sign_out_layout);
        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        llLanguage = findViewById(R.id.nav_language_layout);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
                startActivity(intent);
            }
        });

        myOrderNav = findViewById(R.id.nav_order_layout);
        myOrderNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });

        if (loginStatus.equals("true")) {
            signOutLayout.setVisibility(View.VISIBLE);
        } else {
            signOutLayout.setVisibility(View.GONE);
        }


        /*image_order = findViewById(R.id.imageView_order);
        image_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image_address = findViewById(R.id.address_imageView);
        image_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddressListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image_wishlist = findViewById(R.id.navigation_wishlist);
        image_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navwishlist = new Intent(MainActivity.this, WishListActivity.class);
                startActivity(navwishlist);
                finish();
            }
        });

        profile = findViewById(R.id.imageView_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navwishlist = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(navwishlist);
                finish();
            }
        });

        image_help = findViewById(R.id.help_imageView);
        image_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navhelp = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(navhelp);
                finish();
            }
        });

        image_home = findViewById(R.id.imageView_home_home);
        image_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        about_us = findViewById(R.id.aboutUs);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        share = findViewById(R.id.shareApp);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShareAppActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signout = findViewById(R.id.sign_out);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });*/
    }

    private void fetchLogin() {
        JSONObject map = new JSONObject();
        try {
            map.put("username", pref.getString(PREF_CUSTOMER_CRED_EMAIL));
            map.put("password", pref.getString(PREF_CUSTOMER_CRED_PASSWORD));
            //Log.d("doJSON Request", map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkManager(this).doPostCustom(
                Apis.API_POST_USER_LOGIN,
                Object.class,
                map,
                ACCESS_TOKEN,
                "TAG_LOGIN",
                REQUEST_LOGIN,
                this
        );
    }

    private void processJsonLogin(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<String>(){}.getType();
                String userToken = gson.fromJson(response, type);
                Log.d("userToken", userToken);
                LoadingDialog.cancelLoading();
                pref.putString(PREF_ZABEEL_USER_TOKEN, userToken);
                pref.putString(PREF_USER_LOGIN_STATUS, "true");

            } catch (Exception e1) {
                Log.e("processJsonLogin", e1.getMessage());
            }
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void shareUrl() {
        try {
            Intent sendIntent = new Intent();
            String message = "Hey checkout this cool app \n\n" + PlayStoreBaseUrl + getApplicationContext().getPackageName() + "\n\n" + BASE_URL;
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Oops, Share is currently unavailable.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void checkHomeCategory(String catName) {
        for (int index=0; index<homeCategoryList.size(); index ++) {
            if (homeCategoryList.get(index).getId().equals(catName)) {
                viewPager.setCurrentItem(index);
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_LOGIN) {
                    processJsonLogin(response);
                }
            } else {
                LoadingDialog.cancelLoading();
                Toast.makeText(this, "Couldn't load data. The network got interrupted", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("onResponse", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onJsonResponse(int status, String response, int requestId) {
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginStatus.equals("true")) {
            cart_btn.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
            //fetchLogin();
            handler.postDelayed( runnable = new Runnable() {
                public void run() {
                    fetchLogin();
                    handler.postDelayed(runnable, delay);
                }
            }, 200);
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}