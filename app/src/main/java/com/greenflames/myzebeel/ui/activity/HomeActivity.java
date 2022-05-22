package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.category.CategoryModel;
import com.greenflames.myzebeel.models.category.CategoryResponse;
import com.greenflames.myzebeel.models.customer.CustomerModel;
import com.greenflames.myzebeel.models.products.ProductResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.AboutUsUrl;
import static com.greenflames.myzebeel.helpers.Global.CatId;
import static com.greenflames.myzebeel.helpers.Global.DefaultPageSize;
import static com.greenflames.myzebeel.helpers.Global.HomeCategoryList;
import static com.greenflames.myzebeel.helpers.Global.KNetUrl;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.helpers.Global.PlayStoreBaseUrl;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;
import static com.greenflames.myzebeel.network.Apis.BASE_URL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_EMAIL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_PASSWORD;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_EMAIl;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_ID;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_LANGUAGE;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_ID;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NetworkCallbacks {

    ImageView image_wishlist, image_address, image_order, image_help,
            image_home, cart_fruits, share, signout, about_us, profile;
    EditText searchView;
    Pref pref;


    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;
    ViewPager viewPager;

    int product_image[] = {R.drawable.logo, R.drawable.logo, R.drawable.logo};
    String product_name[] = {"Eat Organic", "text 2", "text 3"};
    String product_prcie[] = {"Eat Organic", "text 2", "text 3"};
    RecyclerView recyclerView;
    RecyclerView recyclerViewsearch;
    String file, category_id;
    ArrayList product_id = new ArrayList();
    ArrayList product_name1 = new ArrayList();
    ArrayList product_available1 = new ArrayList();
    ArrayList product_image1 = new ArrayList();
    ArrayList product_price1 = new ArrayList();
    ArrayList description = new ArrayList();
    ArrayList product_sku = new ArrayList();
    ArrayList skurelatedarray = new ArrayList();
    ArrayList link_typearray = new ArrayList();
    ArrayList linkedproductskuarray = new ArrayList();
    ArrayList linkedproducttypearray = new ArrayList();
    ArrayList positionarray = new ArrayList();
    ImageView search;
    String searchstring, value1, skurelated, link_type, linked_product_sku, linked_product_type, position;
//private  SectionPagerAdapter sectionPagerAdapter;

    private final int REQUEST_ADMIN_TOKEN = 1004;
    private final int REQUEST_CATEGORY = 1005;
    private final int REQUEST_PRODUCTS = 1006;
    private final int REQUEST_CREATE_CART_ID = 1009;
    private final int REQUEST_CREATE_GUEST_TOKEN = 1013;
    private final int REQUEST_CUSTOMER_DETAIL = 1020;
    private final int REQUEST_CART_MERGE = 1010;
    private Gson gson = new Gson();
    private static final String TAG = HomeActivity.class.getName();

    private List<CategoryModel> homeCategoryList = new ArrayList<>();
    private MaterialCardView fruitsCard, kuwaitGccCard, vegCard, eggCard, greenyVegCard, gasCard;
    private String mCategoryId;
    private LinearLayout searchMainLayout;
    private LinearLayout homeNav, wishNav, helpNav, aboutNav, shareNav, profileNav, myOrderNav, signOutLayout,llLanguage;
    private String loginStatus;
    private TextView userNameTxt;
    //private LinearLayout signOutLayout;

    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cart_btn;
    private final int REQUEST_LOGIN = 1001;
    private boolean firstLoad = true;

    private final Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable;
    private final int delay = 599*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);

        pref = new Pref(this);

        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        fruitsCard = (MaterialCardView) findViewById(R.id.home_cat_fruits_card);
        kuwaitGccCard = (MaterialCardView) findViewById(R.id.home_cat_ku_gcc_card);
        vegCard = (MaterialCardView) findViewById(R.id.home_cat_veg_card);
        eggCard = (MaterialCardView) findViewById(R.id.home_cat_eggs_card);
        greenyVegCard = (MaterialCardView) findViewById(R.id.home_cat_greeny_veg_card);
        gasCard = (MaterialCardView) findViewById(R.id.home_cat_gas_card);
        searchMainLayout = (LinearLayout) findViewById(R.id.home_search_main_layout);
        String languageCode = pref.getString(PREF_LANGUAGE);
        if (languageCode.isEmpty()){
            languageCode = "en";
        }
        Global.STORE_LANGUAGE = languageCode+"/";
        fetchCategory();

        searchView =  findViewById(R.id.searchView_home);
        /*searchView.clearFocus();

        //admintoken();
        //customertoken();

        //simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_home);
        viewPager=findViewById(R.id.viewpager_home);

        search=findViewById(R.id.imageview_search);
        recyclerViewsearch=findViewById(R.id.recycler_home);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerViewsearch.setLayoutManager(gridLayoutManager);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                recyclerViewsearch.setVisibility(View.VISIBLE);

            }
        });

        searchView.setOnEditorActionListener(new  TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, final int actionId, KeyEvent event) {
                boolean handled = false;

                        if (actionId == EditorInfo.IME_ACTION_SEND) {

                            searchstring=searchView.getText().toString();
                            search_product(searchstring);
                            Log.d("serd","drttt");

                        }
                return handled;
                    }

                });*/

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setVerticalScrollBarEnabled(true);

        cart_btn = findViewById(R.id.imageView_cart);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                final Intent mainIntent = new Intent(HomeActivity.this, CartActivity.class);
                HomeActivity.this.startActivity(mainIntent);
//                if (loginStatus.equals("true")) {
//                    final Intent mainIntent = new Intent(HomeActivity.this, CartActivity.class);
//                    HomeActivity.this.startActivity(mainIntent);
//                } else {
//                    Toast.makeText(HomeActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
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
                    Intent navwishlist = new Intent(HomeActivity.this, WishListActivity.class);
                    startActivity(navwishlist);
                } else {
                    Toast.makeText(HomeActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });

        llLanguage = findViewById(R.id.nav_language_layout);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent navwishlist = new Intent(HomeActivity.this, LanguageActivity.class);
                startActivity(navwishlist);
            }
        });

        profile = findViewById(R.id.imageView_profile);
        profileNav = findViewById(R.id.nav_profile_layout);
        profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent navwishlist = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(navwishlist);
                } else {
                    Toast.makeText(HomeActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
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
                Intent navhelp = new Intent(HomeActivity.this, ContactUsActivity.class);
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
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
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
                //Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        share = findViewById(R.id.shareApp);
        shareNav = findViewById(R.id.nav_share_layout);
        shareNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                /*Intent intent = new Intent(HomeActivity.this, ShareAppActivity.class);
                startActivity(intent);
                finish();*/
                shareUrl();
            }
        });

        //signout = findViewById(R.id.sign_out);
        signOutLayout = findViewById(R.id.nav_sign_out_layout);
        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        myOrderNav = findViewById(R.id.nav_order_layout);
        myOrderNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
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
                drawer.closeDrawers();
                Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        image_address = findViewById(R.id.address_imageView);
        image_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(HomeActivity.this, AddressListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signout = findViewById(R.id.sign_out);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

        searchMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //getData();

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


    }

    private void goToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
        boolean flag = false;
        String catId = "";
        for (int index=0; index<homeCategoryList.size(); index ++) {
            if (homeCategoryList.get(index).getName().equalsIgnoreCase(catName)) {
                flag = true;
                catId = homeCategoryList.get(index).getId();
                break;
            }
        }
        if (flag) {
            mCategoryId = catId;
            CatId = mCategoryId;
            //fetchProductList(catId);
            goToMainActivity();

        } else {
            Toast.makeText(this, "Oops, This category is currently unavailable.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void fetchAdminToken() {
        JSONObject map = new JSONObject();
        try {
            map.put("username", "mobileapp");
            map.put("password", "Zeb@$7127");
            Log.d("doJSON Request", map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkManager(this).doPostCustom(
                Apis.API_POST_ADMIN_TOKEN,
                Object.class,
                map,
                ACCESS_TOKEN,
                "TAG_ADMIN_TOKEN",
                REQUEST_ADMIN_TOKEN,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchCategory() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        new NetworkManager(this).doGet(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_HOME_CATEGORY,
                accessToken,
                "TAG_CATEGORY",
                REQUEST_CATEGORY,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchProductList(String catId) {
        CatId = catId;
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_PRODUCTS +
                "?searchCriteria[currentPage]=1" +
                "&searchCriteria[page_size]=" + DefaultPageSize +
                "&searchCriteria[filter_groups][0][filters][0][field]=category_id" +
                "&searchCriteria[filter_groups][0][filters][0][value]=" + catId +
                "&searchCriteria[filter_groups][0][filters][1][field]=visibility" +
                "&searchCriteria[filter_groups][0][filters][1][value]=2,3,4" +
                "&searchCriteria[filter_groups][0][filters][1][condition_type]=in" +
                "&searchCriteria[sortOrders][0][field]=position" +
                "&searchCriteria[sortOrders][0][direction]=ASC";

        new NetworkManager(this).doGetCustom(
                null,
                getUrl,
                ProductResponse.class,
                null,
                accessToken,
                "TAG_PRODUCTS",
                REQUEST_PRODUCTS,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }

    private void mergeCart(String customerId, String storeId) {
        JSONObject map = new JSONObject();
        try {
            map.put("customerId", customerId);
            map.put("storeId", storeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doPutCustom(
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS+cartId,
                Object.class,
                map,
                accessToken,
                "TAG_MERGE",
                REQUEST_CART_MERGE,
                this
        );
    }
    private void postCreateCartId() {
        try {
            String token = pref.getString(PREF_ZABEEL_USER_TOKEN);
            new NetworkManager(this).doPostCustom(
                    Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_CREATE_CART_ID,
                    Object.class,
                    null,
                    token,
                    "TAG_CREATE_CART_ID",
                    REQUEST_CREATE_CART_ID,
                    this
            );

            //LoadingDialog.showLoadingDialog(this,"Loading...");

        } catch (Exception e) {
            Log.e("postCreateCartId", e.getMessage());
            //e.printStackTrace();
        }
    }

    private void postCreateGuestToken() {
        try {
            String token = pref.getString(PREF_ZABEEL_USER_TOKEN);

            new NetworkManager(this).doPostCustom(
                    Apis.API_POST_CREATE_GUEST_CART_ID,
                    Object.class,
                    null,
                    null,
                    "TAG_CREATE_GUEST_CART_ID",
                    REQUEST_CREATE_GUEST_TOKEN,
                    this
            );

            //LoadingDialog.showLoadingDialog(this,"Loading...");

        } catch (Exception e) {
            Log.e("postCreateCartId", e.getMessage());
            //e.printStackTrace();
        }
    }

    private void fetchCustomer() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGet(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CUSTOMER,
                accessToken,
                "TAG_CUSTOMER",
                REQUEST_CUSTOMER_DETAIL,
                this
        );
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

    private void fetchGuestCartList() {
        String accessToken = pref.getString(PREF_ZABEEL_GUEST_TOKEN);

        String query = accessToken.replaceAll("\"","");
        new NetworkManager(this).doGetCustom(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + query+"/items",
                Object.class,
                null,
                null,
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
            cart_btn.clearBadge();
            pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<CartItem>>() {
                }.getType();
                ArrayList<CartItem> cartList = gson.fromJson(response, type);
                if (cartList != null && cartList.size() > 0) {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, cartList.size());
                    cart_btn.setBadgeValue(cartList.size());
                } else {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                    cart_btn.clearBadge();
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

    private void processJsonAdmin(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<String>() {}.getType();
                String adminToken = gson.fromJson(response, type);
                Log.e("adminToken", adminToken);
                pref.putString(PREF_ZABEEL_ADMIN_TOKEN, adminToken);
                LoadingDialog.cancelLoading();

            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(HomeActivity.this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonLogin", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonCategory(String response) {
        if (response == null || response.equals("null")) {
            Log.d(TAG, "processJson: Cant get Category");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<CategoryResponse>() {
                }.getType();
                CategoryResponse categoryResponse = gson.fromJson(response, type);
                if (categoryResponse.getItems() != null && categoryResponse.getItems().size() > 0) {
                    homeCategoryList = categoryResponse.getItems();

                    Collections.sort(homeCategoryList, new Comparator<CategoryModel>() {
                        @Override
                        public int compare(CategoryModel p0, CategoryModel p1) {
                            return Integer.valueOf(p0.getPosition()).compareTo(Integer.valueOf(p1.getPosition()));
                        }
                    });

                    HomeCategoryList = homeCategoryList;
                    fruitsCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkHomeCategory(getString(R.string.fruits_title_text));
                        }
                    });
                    kuwaitGccCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkHomeCategory(getString(R.string.kuwait_food_title));
                        }
                    });
                    vegCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkHomeCategory(getString(R.string.Vegetables_title_text));
                        }
                    });
                    eggCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkHomeCategory(getString(R.string.eggs_title_text));
                        }
                    });
                    greenyVegCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkHomeCategory(getString(R.string.leafy_vegetables_title_text));
                        }
                    });
                    gasCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkHomeCategory(getString(R.string.gas_delivery_title_text));
                        }
                    });
                }
                LoadingDialog.cancelLoading();
                //postCreateCartId();
            } catch (Exception e) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {
                    }.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(HomeActivity.this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonLogin", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonProducts(String response) {
        if (response != null && !response.equals("null")) {
            Log.d(TAG, "processJson: " + response);
            LoadingDialog.cancelLoading();
        } else {
            serverErrorDialog();
        }
    }
    private void processJsonCreateCartId(String response) {
        Log.e("cart id : ", response);
        try {
            float quoteId = Float.parseFloat(response);
            pref.putString(PREF_ZABEEL_CART_ID, "" + quoteId);
            //LoadingDialog.cancelLoading();
            //getCartCount();
            fetchCartList();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                ErrorMessageResponse messageResponse = gson.fromJson(response, type);
                if (messageResponse != null) {
                    String message = messageResponse.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                LoadingDialog.cancelLoading();
                //e1.printStackTrace();
            }
        }
    }
    private void processJsonCreateGuestToken(String response) {
        Log.e("cart id : ", response);
        try {

            pref.putString(PREF_ZABEEL_GUEST_TOKEN, response.replaceAll("\"",""));
            //LoadingDialog.cancelLoading();
            //getCartCount();
            fetchGuestCartList();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                ErrorMessageResponse messageResponse = gson.fromJson(response, type);
                if (messageResponse != null) {
                    String message = messageResponse.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                LoadingDialog.cancelLoading();
                //e1.printStackTrace();
            }
        }
    }
    private void processJsonMergeCart(String response) {
        Log.e("cart id : ", response);
        try {
            pref.putString(PREF_ZABEEL_GUEST_TOKEN, "");
            postCreateCartId();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                ErrorMessageResponse messageResponse = gson.fromJson(response, type);
                if (messageResponse != null) {
                    String message = messageResponse.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                LoadingDialog.cancelLoading();
                //e1.printStackTrace();
            }
        }
    }
    private void processJsonCustomer(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<CustomerModel>() {}.getType();
                CustomerModel customerModel = gson.fromJson(response, type);
                String customerId = customerModel.getId();
                String customerEmail = customerModel.getEmail();
                String customerFirstName = customerModel.getFirstname();
                String customerLastName = customerModel.getLastname();
                pref.putString(PREF_CUSTOMER_ID, customerId);
                pref.putString(PREF_CUSTOMER_EMAIl, customerEmail);
                pref.putString(PREF_CUSTOMER_FIRST_NAME, customerFirstName);
                pref.putString(PREF_CUSTOMER_LAST_NAME, customerLastName);
                if (pref.getString(PREF_ZABEEL_GUEST_TOKEN).isEmpty()){
                    postCreateCartId();
                }else {
                    mergeCart(customerId, customerModel.getStoreId());
                }

            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                    Log.e("processJsonCustomer", e1.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonCustomer", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(HomeActivity.this, "Sorry ! Can't connect to server, try later");
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
                if (firstLoad) {
                    firstLoad = false;
                    fetchCustomer();
                } else {
                    postCreateCartId();
                }

            } catch (Exception e1) {
                Log.e("processJsonLogin", e1.getMessage());
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loginStatus.equals("true")) {
            //fetchLogin();
            //postCreateCartId();
            handler.postDelayed( runnable = new Runnable() {
                public void run() {
                    fetchLogin();
                    handler.postDelayed(runnable, delay);
                }
            }, 200);
        }else {
            String token = pref.getString(PREF_ZABEEL_GUEST_TOKEN);
            if (token!=null && !token.isEmpty()){
                fetchGuestCartList();
            }else {
                postCreateGuestToken();
            }

        }
        //searchView.clearFocus();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
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

        } else if (id == R.id.navigation_wishlist) {
            Intent navwishlist = new Intent(HomeActivity.this, WishListActivity.class);
            startActivity(navwishlist);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_ADMIN_TOKEN) {
                    processJsonAdmin(response);
                } else if (requestId == REQUEST_CATEGORY) {
                    processJsonCategory(response);
                } else if (requestId == REQUEST_PRODUCTS) {
                    processJsonProducts(response);
                } else if (requestId == REQUEST_CREATE_CART_ID) {
                    processJsonCreateCartId(response);
                } else if (requestId == REQUEST_CUSTOMER_DETAIL) {
                    processJsonCustomer(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
                } else if (requestId == REQUEST_LOGIN) {
                    processJsonLogin(response);
                } else if (requestId == REQUEST_CREATE_GUEST_TOKEN) {
                    processJsonCreateGuestToken(response);
                }else if (requestId == REQUEST_CART_MERGE) {
                    processJsonMergeCart(response);
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

    }

//    public class SectionPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return new Fruits();
//                case 1:
//                    return new Nuts();
//                case 2:
//                    return new Vegetables();
//                case 3:
//                    return new Juice();
//                case 4:
//                default:
//                    return new Seeds();
//            }
//
//
//        }
//
//        @Override
//        public int getCount() {
//            return 5;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Fruits";
//                case 1:
//                    return "Nuts&Seeds";
//                case 2:
//                    return "Vegetables";
//                case 3:
//                    return "Juice";
//                case 4:
//                default:
//                    return "Seeds";
//            }
//        }
//
//
//
//    }

    /*private void customertoken() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", "abhinakj123@gmail.com");
            jsonObject.put("password", "Abhinakj123");

            //jsonObject2 is the payload to server here you can use JsonObjectRequest

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, customertoken, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            System.out.print(response);

                        }
                    }, new com.android.volley.Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();

                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void admintoken() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", "mobileapp");
            jsonObject.put("password", "Zeb@$7127");

            //jsonObject2 is the payload to server here you can use JsonObjectRequest


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, admintoken, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            System.out.print(response);

                        }
                    }, new com.android.volley.Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();

                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void search_product(String toString) {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET,"http://demo.myzebeel.com/rest/en/V1/search?searchCriteria" +
                "[requestName]=advanced_search_container&searchCriteria[filter_groups][0][filters][0][field]" +
                "=name&searchCriteria[filter_groups][0][filters][0][value]=" +
                toString+
                "&searchCriteria[filter_groups][0][filters][0][condition_type]=like" ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("searchresponsee",response);
                        String error="";
                        if (response!=null){
                            try {
                                JSONObject jsonObject=new JSONObject(response);
//                        JSONArray items=jsonObject.getJSONArray("items");
//                        for (int i=0;i<items.length();i++){
//                            JSONObject jsonObject1=items.getJSONObject(i);
//                            String id=jsonObject1.getString("id");
//                            products(id);
//                        }
                                JSONObject search_criteria=jsonObject.getJSONObject("search_criteria");
                                JSONArray filter_groups=search_criteria.getJSONArray("filter_groups");
                                for (int i=0;i<filter_groups.length();i++){
                                    JSONObject jsonObject1=filter_groups.getJSONObject(i);
                                    JSONArray filters=jsonObject1.getJSONArray("filters");
                                    for (int j=0;j<filters.length();j++){
                                        JSONObject jsonObject2=filters.getJSONObject(j);
                                        String value=jsonObject2.getString("value");
                                        products(value);
                                        Log.d("vakihd",value);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("exception",e.toString());
                                Toast.makeText(HomeActivity.this, "error in connection"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyerror",error.toString());
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void products(final String value) {
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show();
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, Config_urls.categoryProducts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    Log.d("responsecategoryprd",response);

                    try {
                        progressDialog.dismiss();
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray items=jsonObject.getJSONArray("items");
                        for (int i=0;i<items.length();i++){
                            JSONObject jsonObject1=items.getJSONObject(i);
                            String sku=jsonObject1.getString("sku");
                            String id1=jsonObject1.getString("id");
                            String name=jsonObject1.getString("name");
                            String price=jsonObject1.getString("price");
                            String status=jsonObject1.getString("status");
                            JSONArray media_gallery_entries=jsonObject1.getJSONArray("media_gallery_entries");
                            for (int j=0;j<media_gallery_entries.length();j++){
                                JSONObject jsonObject2=media_gallery_entries.getJSONObject(j);
                                file=jsonObject2.getString("file");
                            }
                            JSONArray custom_attributes=jsonObject1.getJSONArray("custom_attributes");
                            for (int i1=0;i1<custom_attributes.length();i1++){
                                JSONObject jsonObject2=custom_attributes.getJSONObject(i1);
                                String attributecode=jsonObject2.getString("attribute_code");
                                if (attributecode.equals("description")){
                                    value1=jsonObject2.getString("value");

                                }
                            }
                            JSONArray product_links=jsonObject1.getJSONArray("product_links");
                            if (product_links.equals("")){

                                skurelatedarray.add("");
                                link_typearray.add("");
                                linkedproductskuarray.add("");
                                linkedproducttypearray.add("");
                                positionarray.add("");
                            }else {
                                for (int i1 = 0; i1 < product_links.length(); i1++) {
                                    JSONObject jsonObject2 = product_links.getJSONObject(i1);
                                    skurelated = jsonObject2.getString("sku");
                                    link_type = jsonObject2.getString("link_type");
                                    linked_product_sku = jsonObject2.getString("linked_product_sku");
                                    linked_product_type = jsonObject2.getString("linked_product_type");
                                    position = jsonObject2.getString("position");
                                }
                            }
                            JSONObject extension_attributes=jsonObject1.getJSONObject("extension_attributes");
                            JSONArray category_links=extension_attributes.getJSONArray("category_links");
                            for (int j=0;j<category_links.length();j++) {
                                JSONObject jsonObject2 = category_links.getJSONObject(j);
                                category_id = jsonObject2.getString("category_id");
                                Log.d("nnjhj",name);
                                Log.d("kkjjjj",value);

                                if (name.equals(value)) {
                                    product_name1.add(name);
                                    Log.d("ptok", product_name1.toString());
                                    product_price1.add(price);
                                    product_image1.add(file);
                                    product_available1.add(status);
                                    product_id.add(id1);
                                    description.add(value1);
                                    product_sku.add(sku);
                                    skurelatedarray.add(skurelated);
                                    link_typearray.add(link_type);
                                    linkedproductskuarray.add(linked_product_sku);
                                    linkedproducttypearray.add(linked_product_type);
                                    positionarray.add(position);
                                    searchlistAdapter customAdapter = new searchlistAdapter(getApplicationContext(),
                                            product_id, product_name1, product_price1, product_image1, product_available1,description,
                                            product_sku,skurelatedarray,link_typearray,linkedproductskuarray,linkedproducttypearray,positionarray);
                                    recyclerViewsearch.setAdapter(customAdapter);
                                    customAdapter.notifyDataSetChanged();
                                }
//                                else {
//                                    Toast.makeText(Home_activity.this, "No products available", Toast.LENGTH_SHORT).show();
//                                }
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyerror",error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String>params=new HashMap<String, String>();

                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer dnmcysxdvkc26neptgm325r8iqmzfqv4");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void searchbysku() {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, "http://demo.myzebeel.com/rest/en/V1/search", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("skuresponse",response);
                String error="";
                if (response!=null){
                    try {
                        JSONObject jsonObject=new JSONObject(response);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("exception",e.toString());
                        Toast.makeText(HomeActivity.this, "error in connection"+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyerror",error.toString());
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }


    private void getData(){
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please Wait"); // set message
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest postrequest = new StringRequest(Request.Method.GET, Config_urls.getCategory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("test_response", response);
                if (response!=null) {


                    try {
                        JSONObject jsonObject=new JSONObject(String.valueOf(response));
                        progressDialog.dismiss();
                        JSONArray items=jsonObject.getJSONArray("items");
                        for (int i=0;i<items.length();i++) {
                            JSONObject jsonObject1 = items.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String parent_id=jsonObject1.getString("parent_id");

                            String position= jsonObject1.getString("position");
                            String children=jsonObject1.getString("children");
                            String s="2";
                            pref.putString("cat_id",id);

                            if (id.equals("1")){

                            }else if (id.equals("2")){

                            }else {
                                String name=jsonObject1.getString("name");
                                tabLayout.addTab(tabLayout.newTab().setText(name));

                                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), items);
                                viewPager.setAdapter(adapter);

                                tabLayout.setupWithViewPager(viewPager);

//                                viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
//                                tabLayout.setupWithViewPager(viewPager);

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error"+error.toString(), Toast.LENGTH_LONG).show();
                Log.e("errorvolley",error.toString());
                progressDialog.dismiss();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String>params=new HashMap<String, String>();

                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer dnmcysxdvkc26neptgm325r8iqmzfqv4");
                return headers;
            }
        };
        requestQueue.add(postrequest);
        postrequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }*/


}


