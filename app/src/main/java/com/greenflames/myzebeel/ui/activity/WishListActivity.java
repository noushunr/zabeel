package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.WishListAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.helpers.MySwipeHelper;
import com.greenflames.myzebeel.interfaces.MyButtonClickListener;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.wishlist.WishListItem;
import com.greenflames.myzebeel.models.wishlist.WishListResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.AboutUsUrl;
import static com.greenflames.myzebeel.helpers.Global.KNetUrl;
import static com.greenflames.myzebeel.helpers.Global.PlayStoreBaseUrl;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.network.Apis.BASE_URL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_SKU;

//import Helper.MyButtonClickListener;
//import Helper.MySwipeHelper;
//import androidx.annotation.NonNull;

public class WishListActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, NetworkCallbacks, WishListAdapter.OnWishListClickListener {
    ImageView image_wishlist, image_home, image_order, image_address, about_us;
    TextView add_cart;

    ArrayList product_image = new ArrayList(Arrays.asList(R.drawable.apple, R.drawable.avocado1, R.drawable.orange, R.drawable.pineapple, R.drawable.strawberry, R.drawable.blackberry, R.drawable.orange, R.drawable.pineapple));
    ArrayList product_name = new ArrayList(Arrays.asList("Apple ", "Avocado", "Orange", "Pinapple", "Strawberry", "Blackberry", "Orange", "Pinapple"));
    ArrayList product_prcie = new ArrayList(Arrays.asList("KD 230.00", "KD 520.00", "KD 300.00", "KD 350.00", "KD 230.00", "KD 520.00", "KD 300.00", "KD 350.00"));
    //wishlist_product_adapter customAdapter;
    MySwipeHelper swipeHelper;

    private final int REQUEST_WISH_LIST = 1017;
    private final int REQUEST_DELETE_WISH_LIST = 1018;

    private Pref pref;
    private Gson gson = new Gson();
    private static final String TAG = WishListActivity.class.getName();

    private ArrayList<WishListItem> homeWishList = new ArrayList<>();
    private WishListAdapter adapter;
    private LinearLayout homeNav, wishNav, helpNav, aboutNav, shareNav, profileNav, myOrderNav, signOutLayout,llLanguage;
    private String loginStatus;
    private TextView userNameTxt;
    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        pref = new Pref(this);

//        cart=findViewById(R.id.cart_wishlist);
//        cart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Wishlist_activity.this,Cart_activity.class);
//                startActivity(intent);
//                finish();
//            }
//        });


        cart = findViewById(R.id.imageView_wishlist_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishListActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*image_home = findViewById(R.id.imageView_wish_home);
        image_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishListActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image_order = findViewById(R.id.imageView_wish_order);
        image_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishListActivity.this, OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image_address = findViewById(R.id.imageView_wish_address);
        image_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishListActivity.this, AddressListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image_wishlist = findViewById(R.id.navigation_wishlist_wish);
        image_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navwishlist = new Intent(WishListActivity.this, WishListActivity.class);
                startActivity(navwishlist);
                finish();
            }
        });

        about_us = findViewById(R.id.aboutUs);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishListActivity.this, ContactUsActivity.class);
                startActivity(intent);
                finish();
            }
        });*/


//        image_wishlist=findViewById(R.id.navigation_wishlist);
//        image_wishlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent navwishlist=new Intent(Wishlist_activity.this,Wishlist_activity.class);
//                startActivity(navwishlist);
//                finish();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);//your icon here


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initRecyclerview();

        //image_wishlist = findViewById(R.id.navigation_wishlist);
        wishNav = findViewById(R.id.nav_wish_layout);
        wishNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent navwishlist = new Intent(WishListActivity.this, WishListActivity.class);
                startActivity(navwishlist);
            }
        });

        //profile = findViewById(R.id.imageView_profile);
        profileNav = findViewById(R.id.nav_profile_layout);
        profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent navwishlist = new Intent(WishListActivity.this, ProfileActivity.class);
                startActivity(navwishlist);
            }
        });

        //image_help = findViewById(R.id.help_imageView);
        helpNav = findViewById(R.id.nav_help_layout);
        helpNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent navhelp = new Intent(WishListActivity.this, ContactUsActivity.class);
                startActivity(navhelp);
                finish();
            }
        });

        //image_home = findViewById(R.id.imageView_home_home);
        homeNav = findViewById(R.id.nav_home_layout);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(WishListActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //about_us = findViewById(R.id.aboutUs);
        aboutNav = findViewById(R.id.nav_about_layout);
        aboutNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                KNetUrl = AboutUsUrl;
                //Intent intent = new Intent(WishListActivity.this, WebViewActivity.class);
                Intent intent = new Intent(WishListActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        //share = findViewById(R.id.shareApp);
        shareNav = findViewById(R.id.nav_share_layout);
        shareNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                /*Intent intent = new Intent(WishListActivity.this, ShareAppActivity.class);
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
            cart.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }
        signOutLayout = findViewById(R.id.nav_sign_out_layout);
        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(WishListActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        llLanguage = findViewById(R.id.nav_language_layout);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(WishListActivity.this, LanguageActivity.class);
                startActivity(intent);
            }
        });
        myOrderNav = findViewById(R.id.nav_order_layout);
        myOrderNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent intent = new Intent(WishListActivity.this, OrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(WishListActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });

        if (loginStatus.equals("true")) {
            signOutLayout.setVisibility(View.VISIBLE);
        } else {
            signOutLayout.setVisibility(View.GONE);
        }

    }

    private void goToLogin() {
        Intent intent = new Intent(WishListActivity.this, LoginActivity.class);
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

    private void initRecyclerview() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WishListActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new WishListAdapter(this);
        recyclerView.setAdapter(adapter);

        swipeHelper = new MySwipeHelper(this, recyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer, int pos) {

                buffer.add(new MyButton(WishListActivity.this,
                        "Delete",
                        30,
                        0,
                        Color.parseColor("#C63627"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                if (homeWishList.get(pos).getProduct().getId() != null) {
                                    deleteWishList(homeWishList.get(pos).getProduct().getId());
                                }
                            }
                        }));
            }
        };
        fetchWishList();
    }

    private void fetchWishList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGet(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_WISH_LIST,
                accessToken,
                "TAG_WISH_LIST",
                REQUEST_WISH_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void deleteWishList(String productId) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_DELETE_WISH_LIST + productId;

        new NetworkManager(this).doPostCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_DELETE_WISH_LIST",
                REQUEST_DELETE_WISH_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchCartList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGetCustom(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CART_LIST_ITEMS,
                Object.class,
                null,
                accessToken,
                "TAG_CART_LIST",
                REQUEST_CART_LIST,
                this
        );
        //LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void processJsonCartList(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get CartList");
            LoadingDialog.cancelLoading();
            cart.clearBadge();
            pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<CartItem>>() {
                }.getType();
                ArrayList<CartItem> cartList = gson.fromJson(response, type);
                if (cartList != null && cartList.size() > 0) {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, cartList.size());
                    cart.setBadgeValue(cartList.size());
                } else {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                    cart.clearBadge();
                }
            } catch (Exception e) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {
                    }.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonCartList", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonWishList(String response) {
        if (response == null || response.equals("null")) {
            Log.d(TAG, "processJson: Cant get WishList");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<WishListResponse>() {
                }.getType();
                WishListResponse wishListResponse = gson.fromJson(response, type);
                homeWishList.clear();
                if (wishListResponse.getItems() != null && wishListResponse.getItems().size() > 0) {
                    homeWishList = wishListResponse.getItems();
                }
                adapter.submitList(homeWishList);
                if (wishListResponse.getMessage() != null) {
                    dialogWarning(this, wishListResponse.getMessage());
                }
                LoadingDialog.cancelLoading();
            } catch (Exception e) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {
                    }.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonWishList", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonDeleteWishList(String response) {
        Log.d("WishList Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (wishBool) {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                fetchWishList();
            } else {
                Toast.makeText(this, "Oops something went wrong.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>() {
                }.getType();
                ErrorMessageResponse cartData = gson.fromJson(response, type);
                if (cartData != null) {
                    String message = cartData.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                serverErrorDialog();
                Log.e("processDeleteWishList", e1.getMessage());
                //e1.printStackTrace();
            }
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(this, "Sorry ! Can't connect to server, try later");
    }

    private void goToProductDetail() {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_DELETE_WISH_LIST) {
                    processJsonDeleteWishList(response);
                } else if (requestId == REQUEST_WISH_LIST) {
                    processJsonWishList(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
                }
            } else {
                LoadingDialog.cancelLoading();
                Toast.makeText(this, "Couldn't load data. The network got interrupted", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("onResponse", e.getMessage());
            //e.printStackTrace();
        }
    }

    @Override
    public void onJsonResponse(int status, String response, int requestId) {

    }

    @Override
    public void onItemClick(int position, @NotNull WishListItem item) {
        pref.putString(PREF_ZABEEL_PRODUCT_SKU, item.getProduct().getSku());
        goToProductDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginStatus.equals("true")) {
            fetchCartList();
        }
    }
}

