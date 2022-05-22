package com.greenflames.myzebeel.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.helpers.Global.isDataValid;
import static com.greenflames.myzebeel.helpers.Global.isValidPass;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_EMAIl;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class ProfileActivity extends AppCompatActivity implements NetworkCallbacks {

    private ImageButton image_back, image_close;
    private TextView nameTxt, emailTxt;
    private BottomSheetBehavior behavior;
    private LinearLayout goToOrders, goToWallet, goToAddress, goToChangePassword, signOutUser,llLanguage;
    private FrameLayout overLay;
    private EditText currentPassTxt, newPassTxt;
    private Button submitBtn;
    private String currentPassword, newPassword;

    private Pref pref;
    private final int REQUEST_PUT_PASSWORD = 1036;
    private Gson gson = new Gson();
    private static final String TAG = ProfileActivity.class.getName();
    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView image_cart;
    private String loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        image_cart = findViewById(R.id.imageView_profile_cart);
        image_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nameTxt = findViewById(R.id.profile_username_txt);
        String userName = pref.getString(PREF_CUSTOMER_FIRST_NAME) + " " + pref.getString(PREF_CUSTOMER_LAST_NAME);
        nameTxt.setText(userName);
        emailTxt = findViewById(R.id.profile_user_email_txt);
        emailTxt.setText(pref.getString(PREF_CUSTOMER_EMAIl));

        overLay = findViewById(R.id.profile_overlay);
        overLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        View bottomSheet = findViewById(R.id.profile_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    overLay.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    overLay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        image_close = findViewById(R.id.profile_close_bottom_sheet);
        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        goToOrders = findViewById(R.id.profile_go_to_orders);
        goToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, OrderActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        goToAddress = findViewById(R.id.profile_go_to_address);
        goToAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AddressListActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        llLanguage = findViewById(R.id.profile_change_language);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LanguageActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        goToWallet = findViewById(R.id.profile_go_to_zwallet);
        goToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });

        goToChangePassword = findViewById(R.id.profile_go_to_change_password);
        goToChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        signOutUser = findViewById(R.id.profile_sign_out_user);
        signOutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        currentPassTxt = findViewById(R.id.profile_current_password);
        newPassTxt = findViewById(R.id.profile_new_password);

        submitBtn = findViewById(R.id.profile_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getCurrentFocus() != null) {
                    hideKeyboard(getCurrentFocus());
                } else {
                    hideKeyboard(new View(ProfileActivity.this));
                }
                if (isInputValid()) {
                    submitPassword();
                }
            }
        });

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            image_cart.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isInputValid() {
        currentPassword = currentPassTxt.getText().toString().trim();
        newPassword = newPassTxt.getText().toString().trim();
        Log.d("currentPassword", currentPassword);
        Log.d("newPassword", newPassword);

        currentPassTxt.setError(null);
        newPassTxt.setError(null);

        if (!isDataValid(currentPassword)) {
            currentPassTxt.setError("Current Password is required");
            currentPassTxt.requestFocus();
            return false;
        } else if (!isDataValid(newPassword)) {
            newPassTxt.setError("New Password is required");
            newPassTxt.requestFocus();
            return false;
        } else if (!isValidPass(newPassword)) {
            newPassTxt.setError("Password must contain Lower Case, Upper Case, Digits & Length must be equal or greater than 8");
            newPassTxt.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void submitPassword() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        JSONObject map = new JSONObject();
        try {
            map.put("currentPassword", currentPassword);
            map.put("newPassword", newPassword);
            Log.d("doJSON Request", map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkManager(this).doPutCustom(
                Apis.API_PUT_UPDATE_PASSWORD,
                Object.class,
                map,
                accessToken,
                "TAG_LOGIN",
                REQUEST_PUT_PASSWORD,
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
            image_cart.clearBadge();
            pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<CartItem>>() {
                }.getType();
                ArrayList<CartItem> cartList = gson.fromJson(response, type);
                if (cartList != null && cartList.size() > 0) {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, cartList.size());
                    image_cart.setBadgeValue(cartList.size());
                } else {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                    image_cart.clearBadge();
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

    private void processJsonChangePassword(String response) {
        Log.d("ChangePassword Response", response);
        try {
            boolean passBool = Boolean.parseBoolean(response);
            if (passBool) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                newPassTxt.setText(null);
                currentPassTxt.setText(null);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                ErrorMessageResponse cartData = gson.fromJson(response, type);
                if (cartData != null) {
                    String message = cartData.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            }
            LoadingDialog.cancelLoading();
        } catch (Exception e) {
            //e.printStackTrace();
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
                //e1.printStackTrace();
            }
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
                if (requestId == REQUEST_PUT_PASSWORD) {
                    processJsonChangePassword(response);
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
}