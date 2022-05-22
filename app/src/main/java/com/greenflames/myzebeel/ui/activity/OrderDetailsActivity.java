package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.OrderDetailItemAdapter;
import com.greenflames.myzebeel.adapters.OrderStatusAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.order.BillingAddress;
import com.greenflames.myzebeel.models.order.OrderDetail;
import com.greenflames.myzebeel.models.order.OrderDetailItem;
import com.greenflames.myzebeel.models.order.OrderPayment;
import com.greenflames.myzebeel.models.order.StatusHistory;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.OrderId;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;

public class OrderDetailsActivity extends AppCompatActivity implements NetworkCallbacks {

    private RecyclerView recyclerView, statusRecyclerView;
    private TextView paymentMode, orderId, userName, userAddress, userPhone,
            priceTxt, deliveryTxt, grandTotalTxt;
    private Button orderagain;
    private ImageButton back;

    private final int REQUEST_ORDER_DETAIL = 1030;
    private final int REQUEST_REORDER = 1045;

    private Pref pref;
    private Gson gson = new Gson();
    private static final String TAG = OrderDetailsActivity.class.getName();

    private ArrayList<OrderDetailItem> homeOrderItemList = new ArrayList<>();
    private ArrayList<StatusHistory> homeOrderStatusList = new ArrayList<>();
    private OrderDetailItemAdapter adapter;
    private OrderStatusAdapter statusAdapter;
    private String incrementId;
    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cart;
    private String loginStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails_activity);

        pref = new Pref(this);

        recyclerView = findViewById(R.id.recyclerview_orderdetails);
        statusRecyclerView = findViewById(R.id.order_detail_status_recyclerView);
        orderId = findViewById(R.id.order_detail_id_txt);
        userName = findViewById(R.id.address_name_textView);
        userAddress = findViewById(R.id.address_details_textView);
        userPhone = findViewById(R.id.address_phonenumber_textView);
        priceTxt = findViewById(R.id.order_detail_price_txt);
        deliveryTxt = findViewById(R.id.order_detail_delivery_txt);
        grandTotalTxt = findViewById(R.id.order_detail_grand_total_txt);

        back = findViewById(R.id.imageButton_back_orderdetails);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cart = findViewById(R.id.imageView_cart_orderdetails);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });


        orderagain = findViewById(R.id.orderagain_button);
        orderagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReOrder();
                /*Intent intent = new Intent(OrderDetailsActivity.this, HomeActivity.class);
                startActivity(intent);*/
            }
        });

        paymentMode = findViewById(R.id.paymentmode_textView);
        paymentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(OrderDetailsActivity.this, PaymentActivity.class);
                startActivity(intent);*/
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_order);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OrderDetailItemAdapter();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        statusRecyclerView.setLayoutManager(linearLayoutManager2);
        statusAdapter = new OrderStatusAdapter();
        statusRecyclerView.setAdapter(statusAdapter);

        fetchOrderDetail();

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            cart.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }

    }

    private void setUpBillingAddress(BillingAddress billingAddress) {
        String name = billingAddress.getFirstname() + " " + billingAddress.getLastname();
        userName.setText(name);

        StringBuilder address = new StringBuilder();
        if (billingAddress.getStreet() != null) {
            for (int i =0; i < billingAddress.getStreet().size(); i++) {
                address.append(billingAddress.getStreet().get(i)).append(", ");
            }
        }
        address.append(billingAddress.getCity()).append(", ")
                //.append(billingAddress.getRegion()).append(", ")
                .append(billingAddress.getPostcode());
        userAddress.setText(address);

        userPhone.setText(billingAddress.getTelephone());
    }

    private void setUpPayment(OrderPayment payment) {
        priceTxt.setText(payment.getBase_amount_ordered());
        deliveryTxt.setText(payment.getShipping_amount());
        String total = "KD " + payment.getAmount_ordered();
        grandTotalTxt.setText(total);
        StringBuilder paymentType = new StringBuilder();
        if (payment.getAdditional_information() != null) {
            for (int i =0; i < payment.getAdditional_information().size(); i++) {
                paymentType.append(payment.getAdditional_information().get(i)).append(" ");
            }
        }
        paymentMode.setText(paymentType);
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

    private void processJsonOrderDetail(String response) {
        try {
            if (response != null && !response.equals("null")) {
                Type type = new TypeToken<OrderDetail>() {
                }.getType();
                OrderDetail orderDetail = gson.fromJson(response, type);
                Log.e(TAG, gson.toJson(orderDetail));
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
                    homeOrderItemList = itemList;
                }
                adapter.submitList(homeOrderItemList);
                String order = "Order #" + orderDetail.getIncrement_id();
                incrementId = orderDetail.getIncrement_id();
                orderId.setText(order);
                homeOrderStatusList.clear();
                if (orderDetail.getStatus_histories() != null && orderDetail.getStatus_histories().size() > 0) {
                    homeOrderStatusList = orderDetail.getStatus_histories();
                }
                statusAdapter.submitList(homeOrderStatusList);
                if (orderDetail.getBilling_address() != null) {
                    setUpBillingAddress(orderDetail.getBilling_address());
                }
                if (orderDetail.getPayment() != null) {
                    setUpPayment(orderDetail.getPayment());
                }
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

    private void processJsonReOrder(String response) {
        Log.d("ReOrder : ", response);
        try {
            boolean reorderBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (reorderBool) {
                Toast.makeText(this, "Products added to cart successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderDetailsActivity.this, CartActivity.class);
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
                if (requestId == REQUEST_ORDER_DETAIL) {
                    processJsonOrderDetail(response);
                } else if (requestId == REQUEST_REORDER) {
                    processJsonReOrder(response);
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
    protected void onResume() {
        super.onResume();
        if (loginStatus.equals("true")) {
            fetchCartList();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
