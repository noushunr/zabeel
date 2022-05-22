package com.greenflames.myzebeel.ui.activity;

import static com.greenflames.myzebeel.adapters.SelectTypeAdapter.SPAN_COUNT_ONE;
import static com.greenflames.myzebeel.adapters.SelectTypeAdapter.SPAN_COUNT_THREE;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.KEY_TYPE_CONFIG;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_EMAIL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_PASSWORD;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_ID;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_SKU;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_TYPE;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.SelectTypeAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.detailproducts.PriceChildren;
import com.greenflames.myzebeel.models.detailproducts.ProductDetail;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class SelectTypeActivity extends AppCompatActivity implements NetworkCallbacks, SelectTypeAdapter.OnSelectTypeClickListener {

    private RecyclerView recyclerView;
    private ImageButton back;
    private ImageBadgeView cart_btn;
    private TextView typeTxt;
    private Pref pref;
    private String loginStatus;
    private SelectTypeAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<PriceChildren> items = new ArrayList<PriceChildren>();
    ArrayList<PriceChildren> productChildrenList = new ArrayList<>();

    private final int REQUEST_PRODUCT_DETAIL = 1008;
    private final int REQUEST_ADD_CART_SIMPLE = 1011;
    private final int REQUEST_ADD_CART_CONFIG = 1012;
    private final int REQUEST_ADD_WISH_LIST = 1016;
    private final int REQUEST_CONFIG_DETAIL_CHILD = 1039;
    private final int REQUEST_LOGIN = 1001;
    private final int REQUEST_CART_LIST = 1015;

    private final Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable;
    private final int delay = 599*1000;

    private Gson gson = new Gson();
    private static final String TAG = SelectTypeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);

        cart_btn = findViewById(R.id.imageView_cart);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mainIntent = new Intent(SelectTypeActivity.this, CartActivity.class);
                SelectTypeActivity.this.startActivity(mainIntent);
//                if (loginStatus.equals("true")) {
//                    final Intent mainIntent = new Intent(SelectTypeActivity.this, CartActivity.class);
//                    SelectTypeActivity.this.startActivity(mainIntent);
//                } else {
//                    Toast.makeText(SelectTypeActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
//                    goToLogin();
//                }
            }
        });
        back = findViewById(R.id.imageButton_select_type_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.select_type_recyclerView);
        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_THREE);
        itemAdapter = new SelectTypeAdapter(items, gridLayoutManager, this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        typeTxt = findViewById(R.id.select_type_txt);
        typeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLayout();
            }
        });

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            cart_btn.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }

        if (pref.getString(PREF_ZABEEL_PRODUCT_TYPE).equals(KEY_TYPE_CONFIG)) {
            productChildren();
        } else {
            fetchProductDetail();
        }

    }

    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            typeTxt.setText(getString(R.string.grid_view_text));
            typeTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_grid, 0, 0, 0);
            gridLayoutManager.setSpanCount(SPAN_COUNT_THREE);
        } else {
            typeTxt.setText(getString(R.string.list_view_text));
            typeTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_bulleted, 0, 0, 0);
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);
        }
        itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
    }

    private void goToLogin() {
        Intent intent = new Intent(SelectTypeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void addToCartSimple(String sku, String qty) {

        JSONObject map = new JSONObject();

        try {
            JSONObject cartItem = new JSONObject();
            cartItem.put("sku", sku);
            cartItem.put("qty", qty);
            if (loginStatus.equals("true"))
                cartItem.put("quote_id", pref.getString(PREF_ZABEEL_CART_ID));
            else {
                String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
                cartItem.put("quote_id", cartId);
            }
//            cartItem.put("quote_id", pref.getString(PREF_ZABEEL_CART_ID));
            map.put("cartItem", cartItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String url = "";
        if (loginStatus.equals("true")){
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_ADD_CART_SIMPLE;
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/items";
        }
        new NetworkManager(this).doPostCustom(
                url,
                JsonObject.class,
                map,
                accessToken,
                "TAG_ADD_CART_SIMPLE",
                REQUEST_ADD_CART_SIMPLE,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");

    }

    /*private void addToCartConfig() {

        JSONObject map = new JSONObject();

        try {
            JSONArray configItemArray = new JSONArray();

            if (selectedOption1 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption1.getOption_value());
                configItem.put("option_id",selectedOption1.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption2 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption2.getOption_value());
                configItem.put("option_id",selectedOption2.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption3 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption3.getOption_value());
                configItem.put("option_id",selectedOption3.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption4 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption4.getOption_value());
                configItem.put("option_id",selectedOption4.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption5 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption5.getOption_value());
                configItem.put("option_id",selectedOption5.getOption_id());
                configItemArray.put(configItem);
            }

            JSONObject extensionAttribute = new JSONObject();

            extensionAttribute.put("configurable_item_options", configItemArray);

            JSONObject productOption = new JSONObject();

            productOption.put("extension_attributes", extensionAttribute);

            JSONObject cartItem = new JSONObject();
            cartItem.put("sku", pr_sku.getText().toString());
            cartItem.put("qty", count.getText().toString());
            cartItem.put("quote_id", pref.getString(PREF_ZABEEL_CART_ID));
            cartItem.put("product_option", productOption);
            map.put("cartItem", cartItem);

            Log.d("doJSON Request", map.toString());

            Log.d("configItemArray", configItemArray.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("error", "" + e.getMessage());
        }
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        new NetworkManager(this).doPostCustom(
                Apis.API_POST_ADD_CART_CONFIG,
                JsonObject.class,
                map,
                accessToken,
                "TAG_ADD_CART_CONFIG",
                REQUEST_ADD_CART_CONFIG,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");

    }*/

    private void addToWishList(String productId) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_ADD_WISH_LIST + productId;

        new NetworkManager(this).doPostCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_ADD_WISH_LIST",
                REQUEST_ADD_WISH_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void productChildren() {
        String productSKU = pref.getString(PREF_ZABEEL_PRODUCT_SKU);
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String postUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CONFIG_PRODUCT_CHILD + productSKU + "/children";

        new NetworkManager(this).doGetCustom(
                null,
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_ADD_CONFIG_DETAIL_CHILD",
                REQUEST_CONFIG_DETAIL_CHILD,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
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

    private void fetchCartList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        if (accessToken.isEmpty()){
            fetchGuestCartList();
        }else {
            new NetworkManager(this).doGetCustom(
                    null,
                    Apis.STORE_URL + Global.STORE_LANGUAGE +  Apis.API_GET_CART_LIST_ITEMS,
                    Object.class,
                    null,
                    accessToken,
                    "TAG_CART_LIST",
                    REQUEST_CART_LIST,
                    this
            );
        }
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
                accessToken,
                "TAG_CART_LIST",
                REQUEST_CART_LIST,
                this
        );
        //LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchProductDetail() {
        String productSKU = pref.getString(PREF_ZABEEL_PRODUCT_SKU);
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String query = productSKU;
        try {
            query = URLEncoder.encode(productSKU, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_PRODUCT_DETAIL + query;

        new NetworkManager(this).doGetCustom(
                null,
                getUrl,
                ProductDetail.class,
                null,
                accessToken,
                "TAG_PRODUCT_DETAIL",
                REQUEST_PRODUCT_DETAIL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
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

    private void processJsonAddToCart(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<CartItem>(){}.getType();
                CartItem cartResponse = gson.fromJson(response, type);
                Log.d("cartResponse", response);
                LoadingDialog.cancelLoading();
                if (cartResponse.getName() != null) {
                    Toast.makeText(this, cartResponse.getName() + " was added to cart.", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    //startActivity(intent);
                    fetchCartList();
                } else {
                    dialogWarning(this, cartResponse.getMessage());
                }
            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonAddToCart", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonWishList(String response) {
        Log.d("WishList Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            if (wishBool) {
                Toast.makeText(this, "Added to wishlist successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Oops something went wrong.", Toast.LENGTH_SHORT).show();
            }
            LoadingDialog.cancelLoading();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                ErrorMessageResponse cartData = gson.fromJson(response, type);
                if (cartData != null) {
                    String message = cartData.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                LoadingDialog.cancelLoading();
                e1.printStackTrace();
            }
        }
    }

    private void processJsonConfigChild(String response) {
        Log.d("ConfigChild Response : ", response);
        if (response == null || response.equals("null")) {
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<PriceChildren>>() {
                }.getType();
                ArrayList<PriceChildren> childrenList = gson.fromJson(response, type);
                productChildrenList.clear();
                if (childrenList != null && childrenList.size() > 0) {
                    productChildrenList = childrenList;
                    items.clear();
                    for (int i = 0; i < productChildrenList.size(); i++) {
                        PriceChildren priceChildren = productChildrenList.get(i);
                        if (priceChildren.getStatus().equals("1") || priceChildren.getStatus().equals("1.0")) {
                            items.add(priceChildren);
                            //Log.e("priceChildren", priceChildren.getName());
                        }
                    }
                    itemAdapter = new SelectTypeAdapter(items, gridLayoutManager, this);
                    recyclerView.setAdapter(itemAdapter);
                    itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
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
                    Log.e("processJsonConfigChild", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonProductDetail(String response) {
        Log.d("ProductDetailResponse :", response);
        if (response == null || response.equals("null")) {
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<PriceChildren>() {
                }.getType();
                PriceChildren childrenList = gson.fromJson(response, type);
                productChildrenList.clear();
                if (childrenList != null) {
                    productChildrenList.add(childrenList);
                    items.clear();
                    for (int i = 0; i < productChildrenList.size(); i++) {
                        PriceChildren priceChildren = productChildrenList.get(i);
                        if (priceChildren.getStatus().equals("1") || priceChildren.getStatus().equals("1.0")) {
                            items.add(priceChildren);
                            //Log.e("priceChildren", priceChildren.getName());
                        }
                    }
                    itemAdapter = new SelectTypeAdapter(items, gridLayoutManager, this);
                    recyclerView.setAdapter(itemAdapter);
                    itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
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
                    Log.e("processJsonConfigChild", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
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

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(SelectTypeActivity.this, "Sorry ! Can't connect to server, try later");
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            fetchCartList();
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

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_PRODUCT_DETAIL) {
                    processJsonProductDetail(response);
                } else if (requestId == REQUEST_ADD_CART_SIMPLE) {
                    processJsonAddToCart(response);
                } else if (requestId == REQUEST_ADD_CART_CONFIG) {
                    processJsonAddToCart(response);
                } else if (requestId == REQUEST_ADD_WISH_LIST) {
                    processJsonWishList(response);
                } else if (requestId == REQUEST_CONFIG_DETAIL_CHILD) {
                    processJsonConfigChild(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
                } else if (requestId == REQUEST_LOGIN) {
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

    }

    @Override
    public void onAddCartClick(PriceChildren item, String qty) {
        addToCartSimple(item.getSku(), qty);
//        if (loginStatus.equals("true")) {
//
//        } else {
//            goToLogin();
//        }
    }

    @Override
    public void onWishListClick(PriceChildren item) {
        if (loginStatus.equals("true")) {
            addToWishList(item.getId());
        } else {
            goToLogin();
        }
    }
}