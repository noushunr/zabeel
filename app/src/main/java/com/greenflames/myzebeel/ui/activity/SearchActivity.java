package com.greenflames.myzebeel.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.ProductMainAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.products.ProductModel;
import com.greenflames.myzebeel.models.products.ProductResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.DefaultPageSize;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_SKU;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_TYPE;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class SearchActivity extends AppCompatActivity implements NetworkCallbacks,
        ProductMainAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductMainAdapter adapter;
    private List<ProductModel> mCatProductList = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private EditText searchView;
    private ImageButton backImage;
    private TextView noProductTxt;
    private int page_number = 1;
    private boolean isLoading = true;
    private int pastVisibleItems = 0, visibleItemCount = 0, totalItemCount = 0, previousTotal = 0;
    private int view_threshold = 10;
    private int productTotalCount = 0;
    private String searchText = "";
    private static final String TAG = SearchActivity.class.getName();
    private Pref pref;
    private final int REQUEST_SEARCH_PRODUCTS = 1035;
    private Gson gson = new Gson();
    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cartImage;
    private String loginStatus;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        pref = new Pref(this);
        backImage =findViewById(R.id.imageButton_search_back);
        cartImage =findViewById(R.id.imageView_search_cart);
        recyclerView = findViewById(R.id.search_recyclerView);
        searchView = findViewById(R.id.searchview);
        noProductTxt = findViewById(R.id.search_no_result_txt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ProductMainAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                        if (productTotalCount > totalItemCount) {
                            page_number++;
                            isLoading = true;
                            fetchProductList();
                        }
                    }
                }
            }
        });

        searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_hidden_close, 0);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noProductTxt.setVisibility(View.GONE);
                //mCatProductList.clear();
                //adapter.submitList(mCatProductList);
                if (searchView.getText().toString().trim().length() > 0) {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
                } else {
                    searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_hidden_close, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (getCurrentFocus() != null) {
                    hideKeyboard(getCurrentFocus());
                } else {
                    hideKeyboard(new View(SearchActivity.this));
                }
                String enterText = v.getText().toString().trim();
                if (!enterText.equals("null") && !enterText.isEmpty()) {
                    isLoading = true;
                    page_number = 1;
                    searchText = v.getText().toString().trim();
                    fetchProductList();
                }
                return false;
            }
        });

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (searchView.getText().toString().trim().length() > 0) {
                        if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            searchView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_hidden_close, 0);
                            searchView.setText("");
                            mCatProductList.clear();
                            adapter.initalList(mCatProductList);
                            noProductTxt.setVisibility(View.GONE);
                            searchView.requestFocus();
                            showSoftKeyboard(searchView);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            cartImage.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void goToProductDetail() {
        //Intent intent = new Intent(this, ProductDetailActivity.class);
        Intent intent = new Intent(this, SelectTypeActivity.class);
        startActivity(intent);
    }

    private void fetchProductList() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);

        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_SEARCH_PRODUCTS +
                "?searchCriteria[filter_groups][0][filters][0][field]=name" +
                "&searchCriteria[filter_groups][0][filters][0][value]=%25" + searchText + "%25" +
                "&searchCriteria[filter_groups][0][filters][0][condition_type]=like" +
                //"&fields=total_count,items[sku,name,price,status,visibility,type_id,media_gallery_entries]" +
                "&searchCriteria[pageSize]=" + DefaultPageSize +"&searchCriteria[currentPage]=" + page_number;

        new NetworkManager(this).doGetCustom(
                null,
                getUrl,
                ProductResponse.class,
                null,
                accessToken,
                "TAG_SEARCH_PRODUCTS",
                REQUEST_SEARCH_PRODUCTS,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
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
            cartImage.clearBadge();
            pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<CartItem>>() {
                }.getType();
                ArrayList<CartItem> cartList = gson.fromJson(response, type);
                if (cartList != null && cartList.size() > 0) {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, cartList.size());
                    cartImage.setBadgeValue(cartList.size());
                } else {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                    cartImage.clearBadge();
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

    private void processJsonProducts(String response) {
        if (response != null && !response.equals("null")) {
            Log.d(TAG, "processJson: " + response);
            LoadingDialog.cancelLoading();
            try {
                Type type = new TypeToken<ProductResponse>() {
                }.getType();
                mCatProductList.clear();
                ProductResponse productResponse = gson.fromJson(response, type);
                if (productResponse.getItems() != null && productResponse.getItems().size() > 0) {
                    mCatProductList = productResponse.getItems();
                    //adapter.submitList(productResponse.getItems());
                } else {
                    adapter.initalList(mCatProductList);
                }

                if (productResponse.getTotal_count() != null) {
                    try {
                        //CatProductCount = Integer.parseInt(productResponse.getTotal_count());
                        productTotalCount = Integer.parseInt(productResponse.getTotal_count());;
                    } catch (Exception e) {
                        //CatProductCount = 0;
                        productTotalCount = 0;
                    }
                } else {
                    //CatProductCount = 0;
                    productTotalCount = 0;
                }

                if (!mCatProductList.isEmpty()) {
                    noProductTxt.setVisibility(View.GONE);
                    if (page_number == 1) {
                        adapter.initalList(mCatProductList);
                    } else {
                        adapter.updateList(mCatProductList);
                    }
                } else {
                    noProductTxt.setVisibility(View.VISIBLE);
                    Toast.makeText(this,
                            "Oops no products are available",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "error: " + e.getMessage());
            }
        } else {
            serverErrorDialog();
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(this, "Sorry ! Can't connect to server, try later");
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_SEARCH_PRODUCTS) {
                    processJsonProducts(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
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
    public void onProductClick(int position, @NotNull ProductModel item) {
        pref.putString(PREF_ZABEEL_PRODUCT_SKU, item.getSku());
        pref.putString(PREF_ZABEEL_PRODUCT_TYPE, item.getType_id());
        goToProductDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.requestFocus();
        showSoftKeyboard(searchView);
        if (loginStatus.equals("true")) {
            fetchCartList();
        }
    }
}