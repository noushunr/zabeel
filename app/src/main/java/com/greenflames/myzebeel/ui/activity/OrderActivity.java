package com.greenflames.myzebeel.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.OrderAdapter;
import com.greenflames.myzebeel.adapters.OrderDetailItemAdapter;
import com.greenflames.myzebeel.adapters.ReorderCartListAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.order.OrderDetail;
import com.greenflames.myzebeel.models.order.OrderDetailItem;
import com.greenflames.myzebeel.models.order.OrderItem;
import com.greenflames.myzebeel.models.order.OrderListResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.OrderId;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.network.Apis.API_GET_ORDER_LIST;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_EMAIl;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class OrderActivity extends AppCompatActivity implements NetworkCallbacks,
        OrderAdapter.OnOrderListClickListener,ReorderCartListAdapter.OnCartListClickListener {

    ImageButton back;
    RecyclerView recyclerView;

    private final int REQUEST_ORDER_LIST = 1027;
    private final int REQUEST_ORDER_DETAIL = 1030;
    private final int REQUEST_REORDER = 1045;
    private String incrementId;
    private Pref pref;
    private Gson gson = new Gson();
    private static final String TAG = OrderActivity.class.getName();

    private ArrayList<OrderItem> homeOrderList = new ArrayList<>();
    private OrderAdapter adapter;
    private ReorderCartListAdapter reorderCartListAdapter;
    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cart;
    private String loginStatus;
    private ArrayList<OrderDetailItem> homeOrderItemList = new ArrayList<>();
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_activity);

        pref = new Pref(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_order_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        initRecyclerView();

        back = findViewById(R.id.imageButton_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();*/
                onBackPressed();
            }
        });

        cart = findViewById(R.id.imageView_order_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            cart.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.relative_orders_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);

        fetchOrderList();

    }

    private void fetchOrderList() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + API_GET_ORDER_LIST + pref.getString(PREF_CUSTOMER_EMAIl) +
                "&searchCriteria[filter_groups][0][filters][0][condition_type]=like" +
                "&fields=total_count,items[increment_id,entity_id,grand_total,status,created_at,billing_address]" +
                "&searchCriteria[sortOrders][0][field]=created_at&searchCriteria[sortOrders][0][direction]=DESC";
        //increment_id,entity_id,grand_total,status,extension_attributes,created_at

        new NetworkManager(this).doGetCustom(
                null,
                getUrl,
                JsonObject.class,
                null,
                accessToken,
                "TAG_ORDER_LIST",
                REQUEST_ORDER_LIST,
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
                Log.e("processJsonCart", e.getMessage());
            }
        }
    }

    private void processJsonOrderList(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get OrderList");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            //Log.d(TAG, response);
            try {
                Type type = new TypeToken<OrderListResponse>() {
                }.getType();
                OrderListResponse orderListResponse = gson.fromJson(response, type);
                homeOrderList.clear();
                if (orderListResponse.getItems() != null && orderListResponse.getItems().size() > 0) {
                    homeOrderList = orderListResponse.getItems();
                }
                adapter.submitList(homeOrderList);
                if (orderListResponse.getMessage() != null) {
                    dialogWarning(this, orderListResponse.getMessage());
                }
                LoadingDialog.cancelLoading();
                if (loginStatus.equals("true")) {
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
                    Log.e("processJsonOrderList", e2.getMessage());
                    //e2.printStackTrace();
                }
                Log.e("processJsonOrder", e.getMessage());
            }
        }
    }
    private void processJsonReOrder(String response) {
        Log.d("ReOrder : ", response);
        try {
            boolean reorderBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (reorderBool) {
                Toast.makeText(this, "Products added to cart successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
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
                Log.e("processPaymentCoupon", e1.getMessage());
                //e1.printStackTrace();
            }
        }
    }
    private void processJsonOrderDetail(String response) {
        try {
            if (response != null && !response.equals("null")) {
                Type type = new TypeToken<OrderDetail>() {
                }.getType();
                OrderDetail orderDetail = gson.fromJson(response, type);
                Log.e(TAG, gson.toJson(orderDetail));
                incrementId = orderDetail.getIncrement_id();
                if (orderDetail.getItems() != null && orderDetail.getItems().size() > 0) {
                    ArrayList<OrderDetailItem> itemList = new ArrayList<>();
                    //homeOrderItemList = orderDetail.getItems();
                    for (int i = 0; i < orderDetail.getItems().size(); i++) {
                        OrderDetailItem item = orderDetail.getItems().get(i);
                        if (item.getBase_original_price() != null && !item.getBase_original_price().isEmpty() &&
                                item.getQty_ordered() != null && !item.getQty_ordered().isEmpty() &&
                                item.getName() != null && !item.getName().isEmpty() &&
                                item.getStore_id() != null && !item.getStore_id().isEmpty()
                        ) {
                            itemList.add(item);
                        }
                    }
                    homeOrderItemList.clear();
                    homeOrderItemList = itemList;
                }
                showPopUp();

                if (orderDetail.getMessage() != null) {
                    dialogWarning(this, orderDetail.getMessage());
                }
                LoadingDialog.cancelLoading();
            } else {
                checkErrorMessage(response);
            }
        } catch (Exception e) {
            Log.e("processOrderDetail", e.getMessage());
            //e.printStackTrace();
            checkErrorMessage(response);
        }
    }

    private void checkErrorMessage(String response) {
        try {
            Type type = new TypeToken<ErrorMessageResponse>() {
            }.getType();
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
            Log.e("checkErrorMessage", e1.getMessage());
            //e1.printStackTrace();
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
                if (requestId == REQUEST_ORDER_LIST) {
                    processJsonOrderList(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
                }else if (requestId == REQUEST_ORDER_DETAIL) {
                    processJsonOrderDetail(response);
                }else if (requestId == REQUEST_REORDER) {
                    processJsonReOrder(response);
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
    public void onOrderClick(int position, @NotNull OrderItem item) {
        OrderId = item.getEntity_id();
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();*/
    }

    @Override
    public void onReOrderClick(int position, @NonNull OrderItem item) {
        OrderId = item.getEntity_id();
        fetchOrderDetail();


    }

    private void fetchOrderDetail() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_ORDER_DETAIL + OrderId;
        new NetworkManager(this).doGetCustom(
                null,
                getUrl,
                JsonObject.class,
                null,
                accessToken,
                "TAG_ORDER_DETAIL",
                REQUEST_ORDER_DETAIL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void showPopUp(){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_order_item,
                null);
        RecyclerView rvOrderItem = popupView.findViewById(R.id.rv_order_items);
        TextView tvReOrder = popupView.findViewById(R.id.tv_confirm_order);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvOrderItem.setLayoutManager(linearLayoutManager);
        reorderCartListAdapter = new ReorderCartListAdapter(this);
        rvOrderItem.setAdapter(reorderCartListAdapter);
        reorderCartListAdapter.submitList(homeOrderItemList);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
        tvReOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callReOrder();
            }
        });
    }
    private void callReOrder() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_REORDER + incrementId;
        new NetworkManager(this).doPostCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_REORDER",
                REQUEST_REORDER,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    @Override
    public void onCartClick(int position, @NonNull OrderDetailItem item) {

    }

    @Override
    public void onIncDecClick(int position, @NonNull OrderDetailItem item, @NonNull String qty) {

    }

    @Override
    public void onErrorClick(int position, @NonNull OrderDetailItem item, @NonNull String message) {

    }
}

