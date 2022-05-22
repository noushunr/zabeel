package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.invoice.InvoiceResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.greenflames.myzebeel.helpers.Global.ORDER_DELIVERY;
import static com.greenflames.myzebeel.helpers.Global.ORDER_DISCOUNT;
import static com.greenflames.myzebeel.helpers.Global.ORDER_SUB_TOTAL;
import static com.greenflames.myzebeel.helpers.Global.ORDER_TAX;
import static com.greenflames.myzebeel.helpers.Global.ORDER_TOTAL;
import static com.greenflames.myzebeel.helpers.Global.OrderId;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;

public class OrderPlacedActivity extends AppCompatActivity implements NetworkCallbacks {

    LinearLayout linearLayout;

    private final int REQUEST_ORDER_DETAIL = 1025;
    private final int REQUEST_FETCH_INVOICE = 1028;
    private final int REQUEST_FETCH_INVOICE_DETAIL = 1029;

    private Pref pref;
    private Gson gson = new Gson();
    private static final String TAG = OrderPlacedActivity.class.getName();

    private TextView countPriceTxt, userAddress, subTotal, deliveryAmt, discountAmt, taxAmt, grandTotal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderplaced_activity);

        pref = new Pref(this);

        countPriceTxt = findViewById(R.id.order_count_price_txt);
        userAddress = findViewById(R.id.order_summary_user_address);
        subTotal = findViewById(R.id.order_summary_subtotal);
        deliveryAmt = findViewById(R.id.order_summary_delivery_amt);
        discountAmt = findViewById(R.id.order_summary_discount_amt);
        taxAmt = findViewById(R.id.order_summary_tax);
        grandTotal = findViewById(R.id.order_summary_grand_total);

        linearLayout = findViewById(R.id.linear_continue);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.putString(PREF_ZABEEL_GUEST_TOKEN,"");
                Intent intent = new Intent(OrderPlacedActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        //fetchCartList();
        countPriceTxt.setText("Total Price " + ORDER_TOTAL + " " + "KD");
        subTotal.setText(ORDER_SUB_TOTAL);
        deliveryAmt.setText(ORDER_DELIVERY);
        discountAmt.setText(ORDER_DISCOUNT);
        taxAmt.setText(ORDER_TAX);
        grandTotal.setText("KD" + " " + ORDER_TOTAL);



        //fetchInvoiceNumber(OrderId);
    }

    private void setUpOrderInvoice(InvoiceResponse invoice) {
        countPriceTxt.setText("Total Price " + invoice.getGrand_total() + " " + invoice.getBase_currency_code());
        subTotal.setText(invoice.getSubtotal());
        deliveryAmt.setText(invoice.getShipping_amount());
        discountAmt.setText(invoice.getDiscount_amount());
        taxAmt.setText(invoice.getTax_amount());
        grandTotal.setText(invoice.getBase_currency_code() + " " + invoice.getGrand_total());
    }

    private void fetchOrderDetail() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_ORDER_DETAIL + OrderId;
        new NetworkManager(this).doGetCustom(
                null,
                Apis.API_GET_ORDER_DETAIL,
                JsonObject.class,
                null,
                accessToken,
                "TAG_ORDER_DETAIL",
                REQUEST_ORDER_DETAIL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchInvoiceNumber(String orderId) {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String postUrl = Apis.API_POST_INVOICE + orderId + "/invoice";

        new NetworkManager(this).doPostCustom(
                postUrl,
                String.class,
                null,
                accessToken,
                "TAG_FETCH_INVOICE",
                REQUEST_FETCH_INVOICE,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchInvoiceDetail(String invoiceId) {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String postUrl = Apis.API_POST_INVOICE_DETAIL + invoiceId;

        new NetworkManager(this).doGetCustom(
                null,
                postUrl,
                String.class,
                null,
                accessToken,
                "TAG_FETCH_INVOICE_DETAIL",
                REQUEST_FETCH_INVOICE_DETAIL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void processJsonInvoiceNumber(String response) {
        Log.d("Invoice id : ", response);
        try {
            if (response != null && !response.equals("null")) {
                String sInvoiceId = response.replace("\"", "");
                int invoiceId = Integer.parseInt(sInvoiceId);
                LoadingDialog.cancelLoading();
                fetchInvoiceDetail("" + invoiceId);
            } else {
                checkErrorMessage(response);
            }
        } catch (Exception e) {
            Log.e("processInvoiceNumber", e.getMessage());
            //e.printStackTrace();
            checkErrorMessage(response);
        }
    }

    private void processJsonInvoiceDetail(String response) {
        try {
            if (response != null && !response.equals("null")) {
                Type type = new TypeToken<InvoiceResponse>() {
                }.getType();
                InvoiceResponse invoiceResponse = gson.fromJson(response, type);
                if (invoiceResponse.getGrand_total() != null) {
                    setUpOrderInvoice(invoiceResponse);
                }
                if (invoiceResponse.getMessage() != null) {
                    dialogWarning(this, invoiceResponse.getMessage());
                }
                LoadingDialog.cancelLoading();
            } else {
                checkErrorMessage(response);
            }
        } catch (Exception e) {
            Log.e("processInvoiceDetail", e.getMessage());
            //e.printStackTrace();
            checkErrorMessage(response);
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
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_ORDER_DETAIL) {
                    //processJsonOrderDetail(response);
                } else if (requestId == REQUEST_FETCH_INVOICE) {
                    processJsonInvoiceNumber(response);
                } else if (requestId == REQUEST_FETCH_INVOICE_DETAIL) {
                    processJsonInvoiceDetail(response);
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
    public void onBackPressed() {
        Intent intent = new Intent(OrderPlacedActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
