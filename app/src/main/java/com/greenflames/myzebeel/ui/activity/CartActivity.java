package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.CartListAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.MyButtonClickListener;
import com.greenflames.myzebeel.helpers.MySwipeHelper;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartConfigItemOption;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.cart.CartListResponse;
import com.greenflames.myzebeel.models.cart.CartTotalSegments;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.greenflames.myzebeel.helpers.Global.AboutUsUrl;
import static com.greenflames.myzebeel.helpers.Global.KNetUrl;
import static com.greenflames.myzebeel.helpers.Global.PlayStoreBaseUrl;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.network.Apis.BASE_URL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.KEY_EXTRAS_ADDRESS_OPERATION;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.KEY_EXTRAS_CART;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_IS_VIRTUAL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class CartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NetworkCallbacks, CartListAdapter.OnCartListClickListener {

    private ImageView image_wishlist, imgeView_address, imageView_home, imageView_order, about_us;

    private LinearLayout linearLayout;
    private MySwipeHelper swipeHelper;

    private final int REQUEST_CART_TOTAL = 1013;
    private final int REQUEST_DELETE_CART_ITEM = 1014;
    private final int REQUEST_CART_LIST = 1015;
    private final int REQUEST_CART_COUPON = 1019;
    private final int REQUEST_UPDATE_ITEM_QTY = 1034;

    private Pref pref;
    private Gson gson = new Gson();
    private static final String TAG = CartActivity.class.getName();

    private ArrayList<CartItem> homeCartList = new ArrayList<>();
    private CartListAdapter adapter;
    private TextView cartSubTotal, shippingRate, cartTotal, couponSubmit;
    private EditText couponEditText;
    private LinearLayout homeNav, wishNav, helpNav, aboutNav, shareNav, profileNav, myOrderNav, signOutLayout,llLanguage;
    private String loginStatus;
    private TextView userNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_activity);

        pref = new Pref(this);

        pref.putString(PREF_IS_VIRTUAL, "false");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        cartSubTotal = findViewById(R.id.cart_sub_total);
        shippingRate = findViewById(R.id.cart_shipping_rate);
        cartTotal = findViewById(R.id.cart_total_amount);
        couponSubmit = findViewById(R.id.cart_coupon_submit);
        couponEditText = findViewById(R.id.cart_coupon_editText);
        linearLayout = findViewById(R.id.cart_last_linear);

        /*imageView_home = findViewById(R.id.imageView_cart_home);
        imageView_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView_order = findViewById(R.id.imageView_cart_order);
        imageView_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgeView_address = findViewById(R.id.imageView_cart_address);
        imgeView_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AddressListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        about_us = findViewById(R.id.aboutUs);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ContactUsActivity.class);
                startActivity(intent);
            }
        });


        image_wishlist = findViewById(R.id.navigation_wishlist);
        image_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navwishlist = new Intent(CartActivity.this, WishListActivity.class);
                startActivity(navwishlist);
                finish();
            }
        });*/

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
                Intent navwishlist = new Intent(CartActivity.this, WishListActivity.class);
                startActivity(navwishlist);
            }
        });

        //profile = findViewById(R.id.imageView_profile);
        profileNav = findViewById(R.id.nav_profile_layout);
        profileNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent navwishlist = new Intent(CartActivity.this, ProfileActivity.class);
                    startActivity(navwishlist);
                } else {
                    Toast.makeText(CartActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });

        //image_help = findViewById(R.id.help_imageView);
        helpNav = findViewById(R.id.nav_help_layout);
        helpNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent navhelp = new Intent(CartActivity.this, ContactUsActivity.class);
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
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
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
                //Intent intent = new Intent(CartActivity.this, WebViewActivity.class);
                Intent intent = new Intent(CartActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        //share = findViewById(R.id.shareApp);
        shareNav = findViewById(R.id.nav_share_layout);
        shareNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                /*Intent intent = new Intent(CartActivity.this, ShareAppActivity.class);
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
        signOutLayout = findViewById(R.id.nav_sign_out_layout);
        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        llLanguage = findViewById(R.id.nav_language_layout);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(CartActivity.this, LanguageActivity.class);
                startActivity(intent);
            }
        });
        myOrderNav = findViewById(R.id.nav_order_layout);
        myOrderNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (loginStatus.equals("true")) {
                    Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(CartActivity.this, LoginActivity.class);
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(this);
        recyclerView.setAdapter(adapter);

        swipeHelper = new MySwipeHelper(this, recyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MySwipeHelper.MyButton> buffer, int pos) {
                /*buffer.add(new MyButton(CartActivity.this,
                        "Edit",
                        30,
                        0,
                        Color.parseColor("#C0C0C0"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Toast.makeText(CartActivity.this, "Edit", Toast.LENGTH_SHORT).show();
                            }
                        }));*/
                buffer.add(new MyButton(CartActivity.this,
                        "Delete",
                        30,
                        0,
                        Color.parseColor("#C63627"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                if (homeCartList.get(pos).getItem_id() != null) {
                                    deleteCartItem(homeCartList.get(pos).getItem_id());
                                }
                                //Toast.makeText(CartActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        };

        fetchCartList();
    }

    private void setUpTotals(ArrayList<CartTotalSegments> totalSegmentsArrayList) {
        for (int i = 0; i < totalSegmentsArrayList.size(); i++) {
            String code = totalSegmentsArrayList.get(i).getCode();
            if (code.equals("subtotal")) {
                try {
                    String sub = totalSegmentsArrayList.get(i).getValue();
                    Float subFloat = Float.parseFloat(sub);
                    String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                    cartSubTotal.setText("KD " + subAmt);
                } catch (Exception e) {
                    cartSubTotal.setText("KD " + totalSegmentsArrayList.get(i).getValue());
                }
            } else if (code.equals("shipping")) {
                shippingRate.setText(totalSegmentsArrayList.get(i).getValue());
            } else if (code.equals("grand_total")) {
                String total = totalSegmentsArrayList.get(i).getValue();
                cartTotal.setText("KD " + total);
                try {
                    Float totalFloat = Float.parseFloat(total);
                    String totalAmt = String.format(Locale.ENGLISH, "%.3f", totalFloat);
                    cartTotal.setText("KD " + totalAmt);
                } catch (Exception e) {
                    cartTotal.setText("KD " + total);
                    e.printStackTrace();
                }
            }
        }
    }

    private void fetchCartList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        if (accessToken.isEmpty()){
            fetchGuestCartList();
        }else {
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
            LoadingDialog.showLoadingDialog(this, "Loading...");
        }
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
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchCartTotal() {

        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String url ="";
        if (loginStatus.equals("true")){
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CART_TOTAL;
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/totals";
        }
        new NetworkManager(this).doGetCustom(
                null,
                url,
                Object.class,
                null,
                accessToken,
                "TAG_CART_TOTAL",
                REQUEST_CART_TOTAL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void deleteCartItem(String itemId) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = "";
        if (loginStatus.equals("true")){
            postUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_DELETE_CART_ITEM + itemId;
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            postUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/items/"+ itemId;
        }
        new NetworkManager(this).doDeleteCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_DELETE_CART_ITEM",
                REQUEST_DELETE_CART_ITEM,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void updateCartQty(@NotNull CartItem item, @NotNull String qty) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);

        String postUrl = "";
        if (loginStatus.equals("true")){
            postUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_PUT_CART_UPDATE_ITEM + item.getItem_id();
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            postUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/items/"+ item.getItem_id();
        }
        try {
            JSONObject map = new JSONObject();
            JSONObject cartItem = new JSONObject();
            JSONObject extAttribute = new JSONObject();

            cartItem.put("item_id", item.getItem_id());
            cartItem.put("sku", item.getSku());
            cartItem.put("qty", qty);
            cartItem.put("name", item.getName());
            cartItem.put("price", item.getPrice());
            cartItem.put("product_type", item.getProduct_type());
            cartItem.put("quote_id", item.getQuote_id());
            if (item.getExtension_attributes() != null && item.getExtension_attributes().getImage() != null) {
                extAttribute.put("image", item.getExtension_attributes().getImage());
            }
            cartItem.put("extension_attributes", extAttribute);
            if (item.getProduct_type().equals("configurable")) {
                JSONObject configItem = new JSONObject();
                JSONObject configItemOption = new JSONObject();
                JSONObject productExtAttr = new JSONObject();
                JSONObject productOption = new JSONObject();
                JSONArray optionArray = new JSONArray();
                if (item.getProduct_option() != null) {
                    if (item.getProduct_option().getExtension_attributes() != null) {
                        if (item.getProduct_option().getExtension_attributes().getCartConfigItemOption() != null
                                && item.getProduct_option().getExtension_attributes().getCartConfigItemOption().size() > 0) {
                            ArrayList<CartConfigItemOption> configItemOptionArrayList = item.getProduct_option().getExtension_attributes().getCartConfigItemOption();
                            for (int i = 0; i < configItemOptionArrayList.size(); i++) {
                                configItem = new JSONObject();
                                configItem.put("option_id", configItemOptionArrayList.get(i).getOption_id());
                                configItem.put("option_value", configItemOptionArrayList.get(i).getOption_value());
                                optionArray.put(configItem);
                            }
                        }
                        configItemOption.put("configurable_item_options", optionArray);
                    }
                    productExtAttr.put("extension_attributes", configItemOption);
                }
                productOption.put("product_option", productExtAttr);
            }

            map.put("cartItem", cartItem);

            new NetworkManager(this).doPutCustom(
                    postUrl,
                    JsonObject.class,
                    map,
                    accessToken,
                    "TAG_PUT_CART_UPDATE_ITEM",
                    REQUEST_UPDATE_ITEM_QTY,
                    this
            );

            Log.d("doJSON Request", gson.toJson(map));
            LoadingDialog.showLoadingDialog(this,"Loading....");

        } catch (Exception e) {
            e.printStackTrace();
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Something went wrong. Try again after some time.");
        }
    }

    private void submitCouponCode(String coupon) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_PUT_CART_COUPON + coupon;

        new NetworkManager(this).doPutCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_CART_COUPON",
                REQUEST_CART_COUPON,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void processJsonCartList(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get CartList");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<CartItem>>() {
                }.getType();
                ArrayList<CartItem> cartList = gson.fromJson(response, type);
                homeCartList.clear();
                if (cartList != null && cartList.size() > 0) {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, cartList.size());
                } else {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                }
                if (cartList != null && cartList.size() > 0) {
                    homeCartList = cartList;
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (loginStatus.equals("true")) {
                                Intent intent = new Intent(CartActivity.this, AddressListActivity.class);
                                intent.putExtra(KEY_EXTRAS_CART, "cart");
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(CartActivity.this, AddAddressActivity.class);
                                intent.putExtra(KEY_EXTRAS_ADDRESS_OPERATION, "new");
                                startActivity(intent);
                            }
                        }
                    });
                    couponSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (couponEditText.getText() != null
                                    && !couponEditText.getText().toString().equals("")
                                    && !couponEditText.getText().toString().equals("null")) {
                                submitCouponCode(couponEditText.getText().toString());
                            } else {
                                Toast.makeText(CartActivity.this, "Please enter a valid coupon code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CartActivity.this, "Please add products to cart and continue", Toast.LENGTH_SHORT).show();
                        }
                    });
                    couponSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CartActivity.this, "Please add products to cart and continue", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                adapter.submitList(homeCartList);
                fetchCartTotal();
                /*Type type = new TypeToken<CartListResponse>() {
                }.getType();
                CartListResponse cartListResponse = gson.fromJson(response, type);
                homeCartList.clear();
                if (cartListResponse.getItems() != null && cartListResponse.getItems().size() > 0) {
                    homeCartList = cartListResponse.getItems();
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CartActivity.this, AddressListActivity.class);
                            intent.putExtra(KEY_EXTRAS_CART, "cart");
                            startActivity(intent);
                            finish();
                        }
                    });
                    couponSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (couponEditText.getText() != null
                                    && !couponEditText.getText().toString().equals("")
                                    && !couponEditText.getText().toString().equals("null")) {
                                submitCouponCode(couponEditText.getText().toString());
                            } else {
                                Toast.makeText(CartActivity.this, "Please enter a valid coupon code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CartActivity.this, "Please add products to cart and continue", Toast.LENGTH_SHORT).show();
                        }
                    });
                    couponSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(CartActivity.this, "Please add products to cart and continue", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                adapter.submitList(homeCartList);
                if (cartListResponse.getMessage() != null) {
                    dialogWarning(this, cartListResponse.getMessage());
                } else {
                    fetchCartTotal();
                }*/
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

    private void processJsonCartTotal(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get Cart Total");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<CartListResponse>() {
                }.getType();
                CartListResponse cartListResponse = gson.fromJson(response, type);
                if (cartListResponse.getTotal_segments() != null && cartListResponse.getTotal_segments().size() > 0) {
                    setUpTotals(cartListResponse.getTotal_segments());
                }
                pref.putString(PREF_IS_VIRTUAL, "false");
                if (cartListResponse.getItems() != null) {
                    ArrayList<CartItem> cartList = cartListResponse.getItems();
                    for (int index = 0; index < cartList.size(); index++) {
                        for (int i = 0; i < homeCartList.size(); i++) {
                            if (homeCartList.get(i).getItem_id().equals(cartList.get(index).getItem_id())) {
                                cartList.get(index).setSku(homeCartList.get(i).getSku());
                                cartList.get(index).setProduct_type(homeCartList.get(i).getProduct_type());
                                cartList.get(index).setQuote_id(homeCartList.get(i).getQuote_id());
                                cartList.get(index).setExtension_attributes(homeCartList.get(i).getExtension_attributes());
                                cartList.get(index).setProduct_option(homeCartList.get(i).getProduct_option());
                                if (homeCartList.get(i).getProduct_type().equals("virtual")) {
                                    pref.putString(PREF_IS_VIRTUAL, "true");
                                }
                                if (cartList.get(index).getOptions() != null) {
                                    Type type2 = new TypeToken<ArrayList<CartTotalSegments>>() {
                                    }.getType();
                                    String finalJSON = cartList.get(index).getOptions().replaceAll("\\\\", "").replace("\"[", "[").replace("]\"", "]");
                                    Log.e("json :", finalJSON);
                                    ArrayList<CartTotalSegments> cartTotalSegmentsArrayList = gson.fromJson(finalJSON, type2);
                                    //String sOrigin = "";
                                    //String sPacking = "";
                                    if (cartTotalSegmentsArrayList != null && cartTotalSegmentsArrayList.size() > 0) {
                                        StringBuilder configString = new StringBuilder();
                                        for (int j = 0; j < cartTotalSegmentsArrayList.size(); j++) {
                                            /*if (cartTotalSegmentsArrayList.get(j).getLabel().equals("Origin")) {
                                                sOrigin = cartTotalSegmentsArrayList.get(j).getValue();
                                            }
                                            if (cartTotalSegmentsArrayList.get(j).getLabel().equals("Packing")) {
                                                sPacking = cartTotalSegmentsArrayList.get(j).getValue();
                                            }*/
                                            String name = cartTotalSegmentsArrayList.get(j).getLabel();
                                            String value = cartTotalSegmentsArrayList.get(j).getValue();
                                            if (j == 0) {
                                                configString = new StringBuilder(name + " : " + value);
                                            } else {
                                                configString.append("\n").append(name).append(" : ").append(value);
                                            }
                                        }
                                        //String configString = "Origin : " + sOrigin + "\nPacking : " + sPacking;
                                        cartList.get(index).setOptions(configString.toString());
                                    }
                                }
                                //homeCartList.get(i).setOptions(cartList.get(index).getOptions());
                                break;
                            }
                        }
                    }
                    adapter.submitList(cartList);
                    homeCartList = cartList;
                }
                if (cartListResponse.getMessage() != null) {
                    dialogWarning(this, cartListResponse.getMessage());
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
                    Log.e("processCartTotal", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonDeleteCart(String response) {
        Log.d("CartItem Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (wishBool) {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                fetchCartList();
            } else {
                Toast.makeText(this, "Oops something went wrong.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //e.printStackTrace();
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
                Log.e("processJsonDeleteCart", e1.getMessage());
                //e1.printStackTrace();
            }
        }
    }

    private void processJsonCartCoupon(String response) {
        Log.d("CartCoupon Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (wishBool) {
                Toast.makeText(this, "Coupon applied successfully", Toast.LENGTH_SHORT).show();
                fetchCartList();
            } else {
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
            }
        } catch (Exception e) {
            //e.printStackTrace();
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
                Log.e("processJsonCartCoupon", e1.getMessage());
                //e1.printStackTrace();
            }
        }
    }

    private void processJsonCartUpdate(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get CartList");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<CartItem>() {
                }.getType();
                CartItem cartItem = gson.fromJson(response, type);
                LoadingDialog.cancelLoading();
                if (cartItem.getMessage() != null) {
                    dialogWarning(this, cartItem.getMessage());
                } else {
                    Toast.makeText(this, cartItem.getName() + " quantity updated to " + cartItem.getQty(), Toast.LENGTH_LONG).show();
                    fetchCartList();
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

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(this, "Sorry ! Can't connect to server, try later");
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
                if (requestId == REQUEST_CART_TOTAL) {
                    processJsonCartTotal(response);
                } else if (requestId == REQUEST_DELETE_CART_ITEM) {
                    processJsonDeleteCart(response);
                } else if (requestId == REQUEST_CART_COUPON) {
                    processJsonCartCoupon(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
                } else if (requestId == REQUEST_UPDATE_ITEM_QTY) {
                    processJsonCartUpdate(response);
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
    public void onCartClick(int position, @NotNull CartItem item) {

    }

    @Override
    public void onIncDecClick(int position, @NotNull CartItem item, @NotNull String qty) {
        updateCartQty(item, qty);
    }

    @Override
    public void onErrorClick(int position, @NotNull CartItem item, @NotNull String message) {
        dialogWarning(this, message);
        LoadingDialog.cancelLoading();
    }
}
