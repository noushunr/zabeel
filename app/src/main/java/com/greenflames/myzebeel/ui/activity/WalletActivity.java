package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.ProductMainAdapter;
import com.greenflames.myzebeel.adapters.WalletAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.wallet.WalletResponse;
import com.greenflames.myzebeel.models.wallet.WalletValues;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_ID;

public class WalletActivity extends AppCompatActivity implements NetworkCallbacks {

    private ImageButton image_back;
    private Button addToCart;
    private RadioGroup walletRadioGroup;
    private TextView balanceTxt;
    private String productOptionId, productOptionValue, productSku;

    private Pref pref;
    private final int REQUEST_WALLET_BALANCE = 1037;
    private final int REQUEST_WALLET_LIST = 1038;
    private final int REQUEST_ADD_CART_VIRTUAL = 1040;
    private Gson gson = new Gson();
    private static final String TAG = WalletActivity.class.getName();

    private WalletAdapter adapter;
    private GridLayoutManager layoutManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        pref = new Pref(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        image_back = findViewById(R.id.imageButton_profile_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        walletRadioGroup = findViewById(R.id.wallet_radioGroup);

        balanceTxt = findViewById(R.id.wallet_current_balance_txt);

        addToCart = findViewById(R.id.wallet_add_cart_btn);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartVirtual();
            }
        });

        recyclerView = findViewById(R.id.wallet_recyclerView);
        adapter = new WalletAdapter(new Function1<WalletValues, Unit>() {
            @Override
            public Unit invoke(WalletValues walletValues) {
                productOptionValue = walletValues.getOption_type_id();
                return null;
            }
        });
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        fetchBalance();

    }

    private void setUpRadioButtons(ArrayList<WalletValues> valueList) {
        walletRadioGroup.removeAllViews();
        for(int i=0; i<valueList.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(valueList.get(i).getTitle());
            final int index = i;
            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        productOptionValue = valueList.get(index).getOption_type_id();
                    }
                }
            });
            walletRadioGroup.addView(rb);
        }
        adapter.submitList(valueList);
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

    private void fetchWalletLst() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        new NetworkManager(this).doGetCustom(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_WALLET_LIST,
                Object.class,
                null,
                accessToken,
                "TAG_WALLET_LIST",
                REQUEST_WALLET_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }



    private void addToCartVirtual() {

        JSONObject map = new JSONObject();

        try {
            JSONArray configItemArray = new JSONArray();

            if (productOptionValue != null) {
                JSONObject configItem = new JSONObject();
                try {
                    configItem.put("option_value", NumberFormat.getInstance().parse(productOptionValue).intValue());
                    configItem.put("option_id", NumberFormat.getInstance().parse(productOptionId).intValue());
                } catch (Exception e1) {
                    //
                }
                configItemArray.put(configItem);
            } else {
                LoadingDialog.cancelLoading();
                dialogWarning(this, "Please select an amount to refill your Zwallet.");
                return;
            }

            JSONObject extensionAttribute = new JSONObject();

            extensionAttribute.put("custom_options", configItemArray);

            JSONObject productOption = new JSONObject();

            productOption.put("extension_attributes", extensionAttribute);

            JSONObject cartItem = new JSONObject();
            cartItem.put("sku", productSku);
            cartItem.put("qty", "1");
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
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_ADD_CART_CONFIG,
                JsonObject.class,
                map,
                accessToken,
                "TAG_ADD_CART_VIRTUAL",
                REQUEST_ADD_CART_VIRTUAL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");

    }

    private void processJsonWalletBalance(String response) {
        Log.e("Balance : ", response);
        try {
            if (response != null && !response.equals("null")) {
                String balance = response.replaceAll("\"", "");
                balance = balance.replace("KWD","");
                balance = balance.replace("KD","");
                //balanceTxt.setText("Current credit balance: " + balance);
                balanceTxt.setText(balance);
                fetchWalletLst();
            } else {
                serverErrorDialog();
            }
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

    private void processJsonWalletList(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<WalletResponse>() {}.getType();
                WalletResponse walletResponse = gson.fromJson(response, type);
                if (walletResponse != null && walletResponse.getOptions() != null
                        && walletResponse.getOptions().size() > 0) {
                    if (walletResponse.getOptions().get(0).getProduct_sku() != null) {
                        productSku = walletResponse.getOptions().get(0).getProduct_sku();
                    }
                    if (walletResponse.getOptions().get(0).getOption_id() != null) {
                        productOptionId = walletResponse.getOptions().get(0).getOption_id();
                    }
                    if (walletResponse.getOptions().get(0).getValues() != null &&
                            walletResponse.getOptions().get(0).getValues().size() > 0) {
                        ArrayList<WalletValues> walletValuesList = walletResponse.getOptions().get(0).getValues();
                        setUpRadioButtons(walletValuesList);
                    }
                }
                LoadingDialog.cancelLoading();
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

    private void processJsonAddToCart(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<CartItem>(){}.getType();
                CartItem cartResponse = gson.fromJson(response, type);
                Log.d("cartResponse", response);
                LoadingDialog.cancelLoading();
                if (cartResponse.getName() != null) {
                    Toast.makeText(this, cartResponse.getName() + " was added to cart.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WalletActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
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
                    //e2.printStackTrace();
                }
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
                if (requestId == REQUEST_WALLET_BALANCE) {
                    processJsonWalletBalance(response);
                } else if (requestId == REQUEST_WALLET_LIST) {
                    processJsonWalletList(response);
                } else if (requestId == REQUEST_ADD_CART_VIRTUAL) {
                    processJsonAddToCart(response);
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