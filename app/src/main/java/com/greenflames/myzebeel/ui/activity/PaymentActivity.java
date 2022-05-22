package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.cart.CartListResponse;
import com.greenflames.myzebeel.models.cart.CartTotalSegments;
import com.greenflames.myzebeel.models.shipping.ShippingInfoResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Locale;

import static com.greenflames.myzebeel.helpers.Global.KNetUrl;
import static com.greenflames.myzebeel.helpers.Global.ORDER_DELIVERY;
import static com.greenflames.myzebeel.helpers.Global.ORDER_DISCOUNT;
import static com.greenflames.myzebeel.helpers.Global.ORDER_SUB_TOTAL;
import static com.greenflames.myzebeel.helpers.Global.ORDER_TAX;
import static com.greenflames.myzebeel.helpers.Global.ORDER_TOTAL;
import static com.greenflames.myzebeel.helpers.Global.OrderId;
import static com.greenflames.myzebeel.helpers.Global.ShippingAddressJsonObj;
import static com.greenflames.myzebeel.helpers.Global.ShippingJsonMapObj;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_IS_VIRTUAL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class PaymentActivity extends AppCompatActivity implements NetworkCallbacks {
    private ImageButton back,cart;
    private Button payment;

    private final int REQUEST_PAYMENT_COUPON = 1021;
    private final int REQUEST_SHIPPING_INFO = 1022;
    private final int REQUEST_CART_TOTAL = 1023;
    private final int REQUEST_CREATE_ORDER = 1024;
    private final int REQUEST_KNET_URL = 1026;
    private final int REQUEST_WALLET_BALANCE = 1041;
    private final int REQUEST_WALLET_CREDIT_APPLY = 1042;
    private final int REQUEST_WALLET_CREDIT_CANCEL = 1043;

    private Pref pref;
    private Gson gson = new Gson();
    private static final String TAG = PaymentActivity.class.getName();

    private RadioButton onlineRadioBtn, codRadioBtn, creditRadioBtn;
    private EditText couponEditText;
    private Button couponSubmit;
    private TextView priceTitleTxt, priceAmountTxt, deliveryTitleTxt, deliveryAmountTxt,
            discountTitleTxt, discountAmountTxt, payableTitleTxt, payableAmountTxt,
            balanceTxt, useAmountTxt, applyTxt;
    private String paymentMethodCode = "knet", total = "0", walletBalance = "0.000", appliedBalance = "0.000", grandTotalAmount = "0.00";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_payment_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        pref = new Pref(this);

        onlineRadioBtn = findViewById(R.id.radioButton1_payment);
        codRadioBtn = findViewById(R.id.radioButton2_payment);
        creditRadioBtn = findViewById(R.id.radioButton3_payment);
        couponEditText = findViewById(R.id.couponEditText_payment);
        couponSubmit = findViewById(R.id.payment_coupon_btn);
        priceTitleTxt = findViewById(R.id.price_title_txt);
        priceAmountTxt = findViewById(R.id.price_amount_txt);
        deliveryTitleTxt = findViewById(R.id.delivery_title_txt);
        deliveryAmountTxt = findViewById(R.id.delivery_amount_txt);
        discountTitleTxt = findViewById(R.id.discount_title_txt);
        discountAmountTxt = findViewById(R.id.discount_amount_txt);
        payableTitleTxt = findViewById(R.id.total_title_txt);
        payableAmountTxt = findViewById(R.id.total_amount_txt);
        balanceTxt = findViewById(R.id.payment_zwallet_balance_txt);
        useAmountTxt = findViewById(R.id.payment_zwallet_use_amount_txt);
        applyTxt = findViewById(R.id.payment_zwallet_apply_txt);

        ORDER_TOTAL = "0";
        ORDER_SUB_TOTAL = "0";
        ORDER_DELIVERY = "0";
        ORDER_DISCOUNT = "0";
        ORDER_TAX = "0";

        onlineRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    paymentMethodCode = "knet";
                }
            }
        });

        codRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    paymentMethodCode = "cashondelivery";
                    //paymentMethodCode = "checkmo";
                }
            }
        });

        creditRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    paymentMethodCode = "checkmo";
                }
            }
        });

        if (pref.getString(PREF_IS_VIRTUAL).equals("true")) {
            codRadioBtn.setVisibility(View.GONE);
            creditRadioBtn.setVisibility(View.GONE);
        }

        couponSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponEditText.getText() != null
                        && !couponEditText.getText().toString().equals("")
                        && !couponEditText.getText().toString().equals("null")) {
                    submitCouponCode(couponEditText.getText().toString());
                } else {
                    Toast.makeText(PaymentActivity.this, "Please enter a valid coupon code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back = findViewById(R.id.imageButton_back_payment);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cart = findViewById(R.id.imageView_payment_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        payment = findViewById(R.id.payButton);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float amt = Float.parseFloat(total);
                if (amt == 0) {
                    paymentMethodCode = "free";
                }
                createNewOrder();
            }
        });

        applyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applyTxt.getText().equals("Apply")) {
                    if (Float.parseFloat(walletBalance) == 0f) {
                        Toast.makeText(PaymentActivity.this, "Insufficient balance. Please refill your Zwallet.", Toast.LENGTH_SHORT).show();
                    } else {
                        submitCreditApply(walletBalance);
                    }
                } else {
                    submitCreditCancel(appliedBalance);
                }
            }
        });

        if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true"))
            submitShippingInfo();
        else
            guestSubmitShippingInfo();
    }

    private void setUpTotals(ArrayList<CartTotalSegments> totalSegmentsArrayList) {
        ORDER_TOTAL = "0";
        ORDER_SUB_TOTAL = "0";
        ORDER_DELIVERY = "0";
        ORDER_DISCOUNT = "0";
        ORDER_TAX = "0";
        for (int i = 0; i < totalSegmentsArrayList.size(); i++) {
            String code = totalSegmentsArrayList.get(i).getCode();
            switch (code) {
                case "subtotal":
                    priceTitleTxt.setText(totalSegmentsArrayList.get(i).getTitle());
                    priceAmountTxt.setText(totalSegmentsArrayList.get(i).getValue());
                    if (totalSegmentsArrayList.get(i).getValue() != null) {
                        ORDER_SUB_TOTAL = totalSegmentsArrayList.get(i).getValue();
                    }
                    break;
                case "shipping":
                    deliveryTitleTxt.setText(totalSegmentsArrayList.get(i).getTitle());
                    deliveryAmountTxt.setText(totalSegmentsArrayList.get(i).getValue());
                    if (totalSegmentsArrayList.get(i).getValue() != null) {
                        ORDER_DELIVERY = totalSegmentsArrayList.get(i).getValue();
                    }
                    break;
                case "discount":
                    discountTitleTxt.setText(totalSegmentsArrayList.get(i).getTitle());
                    discountAmountTxt.setText(totalSegmentsArrayList.get(i).getValue());
                    if (totalSegmentsArrayList.get(i).getValue() != null) {
                        ORDER_DISCOUNT = totalSegmentsArrayList.get(i).getValue();
                    }
                    break;
                case "grand_total":
                    total = totalSegmentsArrayList.get(i).getValue();
                    payableTitleTxt.setText(totalSegmentsArrayList.get(i).getTitle());
                    payableAmountTxt.setText("KD " + total);
                    try {
                        Float totalFloat = Float.parseFloat(total);
                        String totalAmt = String.format(Locale.ENGLISH, "%.3f", totalFloat);
                        payableAmountTxt.setText("KD " + totalAmt);
                    } catch (Exception e) {
                        payableAmountTxt.setText("KD " + total);
                        e.printStackTrace();
                    }
                    if (totalSegmentsArrayList.get(i).getValue() != null) {
                        ORDER_TOTAL = totalSegmentsArrayList.get(i).getValue();
                        grandTotalAmount = total;
                    }
                    break;
            }
        }
    }

    private void submitShippingInfo() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        JSONObject map = new JSONObject();
        try {
            map = ShippingJsonMapObj;
            Log.d("doJSON Request", map.toString());
        } catch (Exception e) {
            Log.e("submitShippingInfo", e.getMessage());
            //e.printStackTrace();
        }
        new NetworkManager(this).doPostCustom(
                Apis.API_POST_SHIPPING_INFO,
                JsonObject.class,
                map,
                accessToken,
                "TAG_SHIPPING_INFO",
                REQUEST_SHIPPING_INFO,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }
    private void guestSubmitShippingInfo() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        JSONObject map = new JSONObject();
        try {
            map = ShippingJsonMapObj;
            Log.d("doJSON Request", map.toString());
        } catch (Exception e) {
            Log.e("submitShippingInfo", e.getMessage());
            //e.printStackTrace();
        }
        String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
        String url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/shipping-information";
        new NetworkManager(this).doPostCustom(
                url,
                JsonObject.class,
                map,
                accessToken,
                "TAG_SHIPPING_INFO",
                REQUEST_SHIPPING_INFO,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }
    private void fetchCartTotal() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String url ="";
        if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
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

    private void submitCouponCode(String coupon) {
        String url = "";
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
            url = Apis.API_PUT_CART_COUPON + coupon;
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/coupons/"+coupon;
        }
        new NetworkManager(this).doPutCustom(
                url,
                Object.class,
                null,
                accessToken,
                "TAG_PAYMENT_COUPON",
                REQUEST_PAYMENT_COUPON,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void createNewOrder() {

        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String url ="";

        JSONObject map = new JSONObject();
        try {
            JSONObject payMethod = new JSONObject();

            payMethod.put("method", paymentMethodCode);

            map.put("paymentMethod", payMethod);
            map.put("billing_address", ShippingAddressJsonObj);

            Log.d("doJSON Request", map.toString());
        } catch (Exception e) {
            Log.e("createNewOrder", e.getMessage());
            //e.printStackTrace();
        }
        if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
            url = Apis.API_POST_CREATE_NEW_ORDER;
            new NetworkManager(this).doPostCustom(
                    url,
                    Object.class,
                    map,
                    accessToken,
                    "TAG_CREATE_ORDER",
                    REQUEST_CREATE_ORDER,
                    this
            );
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/order";
            new NetworkManager(this).doPutCustom(
                    url,
                    Object.class,
                    map,
                    accessToken,
                    "TAG_CREATE_ORDER",
                    REQUEST_CREATE_ORDER,
                    this
            );
        }

        LoadingDialog.showLoadingDialog(this,"Loading...");
    }

    private void fetchKNetUrl(String orderId) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_KNET_URL + orderId + "/url";

        new NetworkManager(this).doPostCustom(
                postUrl,
                String.class,
                null,
                accessToken,
                "TAG_KNET_URL",
                REQUEST_KNET_URL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchBalance() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGetCustom(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_WALLET_BALANCE,
                Object.class,
                null,
                accessToken,
                "TAG_WALLET_BALANCE",
                REQUEST_WALLET_BALANCE,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }

    private void submitCreditApply(String amount) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_WALLET_CREDIT_APPLY + amount;

        appliedBalance = amount;

        new NetworkManager(this).doPostCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_WALLET_CREDIT_APPLY",
                REQUEST_WALLET_CREDIT_APPLY,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void submitCreditCancel(String amount) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_WALLET_CREDIT_CANCEL + amount;

        new NetworkManager(this).doPostCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_WALLET_CREDIT_CANCEL",
                REQUEST_WALLET_CREDIT_CANCEL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void processJsonShippingInfo(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get Shipping Info");
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ShippingInfoResponse>() {
                }.getType();
                ShippingInfoResponse shippingInfoResponse = gson.fromJson(response, type);

                if (shippingInfoResponse.getTotals() != null
                        && shippingInfoResponse.getTotals().getTotal_segments() != null
                        && shippingInfoResponse.getTotals().getTotal_segments().size() > 0) {
                    setUpTotals(shippingInfoResponse.getTotals().getTotal_segments());
                }

                if (shippingInfoResponse.getPayment_methods() != null
                        && shippingInfoResponse.getPayment_methods().size() > 0) {
                    //setUpPaymentMethod(shippingInfoResponse.getPayment_methods());
                }

                if (shippingInfoResponse.getMessage() != null) {
                    dialogWarning(this, shippingInfoResponse.getMessage());
                }
                LoadingDialog.cancelLoading();
                if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
                    fetchBalance();
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
                    Log.e("processShippingInfo", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonCartTotal(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get CartList");
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
                if (cartListResponse.getMessage() != null) {
                    dialogWarning(this, cartListResponse.getMessage());
                }
                LoadingDialog.cancelLoading();
                //fetchBalance();
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
                    e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonPaymentCoupon(String response) {
        Log.d("Coupon Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (wishBool) {
                Toast.makeText(this, "Coupon applied successfully", Toast.LENGTH_SHORT).show();
                fetchCartTotal();
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

    private void processJsonCreateNewOrder(String response) {
        Log.d("Order id : ", response);
        try {
            if (response != null && !response.equals("null")) {
                String sOrderId = response.replace("\"", "");
                int orderId = Integer.parseInt(sOrderId);
                OrderId = "" + orderId;
                LoadingDialog.cancelLoading();
                if (paymentMethodCode.equals("knet")) {
                    fetchKNetUrl(OrderId);
                } else {
                    Intent intent = new Intent(PaymentActivity.this, OrderPlacedActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                checkErrorMessage(response);
            }
        } catch (Exception e) {
            Log.e("processCreateNewOrder", e.getMessage());
            //e.printStackTrace();
            checkErrorMessage(response);
        }
    }

    private void processJsonKNetUrl(String response) {
        //Log.d("Knet Url : ", response);
        try {
            if (response != null && !response.equals("null")) {
                String sKnetUrl = response.replaceAll("\"", "");
                sKnetUrl = URLDecoder.decode(sKnetUrl, "utf-8");
                boolean bool = sKnetUrl.contains("\\u003d");
                Log.e("bool : ", bool + "");
                sKnetUrl = sKnetUrl.replace("\\u003d", "=");
                sKnetUrl = sKnetUrl.replace("\\u0026", "&");
                sKnetUrl = Html.fromHtml((String) sKnetUrl).toString();
                Log.e("KNetUrl : ", sKnetUrl);
                KNetUrl = "" + sKnetUrl;
                Intent intent = new Intent(PaymentActivity.this, WebViewActivity.class);
                startActivity(intent);
                finish();
            } else {
                checkErrorMessage(response);
            }
            LoadingDialog.cancelLoading();
        } catch (Exception e) {
            Log.e("processJsonKNetUrl", e.getMessage());
            //e.printStackTrace();
            checkErrorMessage(response);
        }
    }

    private void processJsonWalletBalance(String response) {
        Log.e("Balance : ", response);
        try {
            if (response != null && !response.equals("null")) {
                String balance = response.replaceAll("\"", "");
                String wBalance = balance.replace("KWD", "");
                wBalance = wBalance.replace("KD", "");
                balanceTxt.setText("Your current balance is : KD " + wBalance);
                float fBalance = Float.parseFloat(wBalance.trim());
                float fTotal = Float.parseFloat(grandTotalAmount);
                if (fTotal >= fBalance) {
                    walletBalance = String.format(Locale.ENGLISH,"%.3f", fBalance);
                } else {
                    walletBalance = String.valueOf(fTotal);
                }
                useAmountTxt.setText(walletBalance);
            } else {
                serverErrorDialog();
            }
            LoadingDialog.cancelLoading();
        } catch (Exception e) {
            //e.printStackTrace();
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

    private void processJsonCreditApplyCancel(String response) {
        Log.d("Wallet Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            fetchCartTotal();
            if (wishBool) {
                if (applyTxt.getText().equals("Apply")) {
                    Toast.makeText(this, "Zwallet was successfully applied", Toast.LENGTH_SHORT).show();
                    applyTxt.setText("Cancel");
                } else {
                    Toast.makeText(this, "Zwallet was successfully cancelled", Toast.LENGTH_SHORT).show();
                    applyTxt.setText("Apply");
                }
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
            Log.e("checkErrorMessage", e1.getMessage());
            //e1.printStackTrace();
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(this, "Sorry ! Can't connect to server, try later");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_PAYMENT_COUPON) {
                    processJsonPaymentCoupon(response);
                } else if (requestId == REQUEST_SHIPPING_INFO) {
                    processJsonShippingInfo(response);
                } else if (requestId == REQUEST_CART_TOTAL) {
                    processJsonCartTotal(response);
                } else if (requestId == REQUEST_CREATE_ORDER) {
                    processJsonCreateNewOrder(response);
                } else if (requestId == REQUEST_KNET_URL) {
                    processJsonKNetUrl(response);
                } else if (requestId == REQUEST_WALLET_BALANCE) {
                    processJsonWalletBalance(response);
                } else if (requestId == REQUEST_WALLET_CREDIT_APPLY) {
                    processJsonCreditApplyCancel(response);
                } else if (requestId == REQUEST_WALLET_CREDIT_CANCEL) {
                    processJsonCreditApplyCancel(response);
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
}
