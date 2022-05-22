package com.greenflames.myzebeel.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.signup.SignUpResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.helpers.Global.isDataValid;
import static com.greenflames.myzebeel.helpers.Global.isValidEmail;
import static com.greenflames.myzebeel.helpers.Global.isValidPass;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;

public class SignUpActivity extends AppCompatActivity implements NetworkCallbacks {

    private Button signUpBtn;
    private EditText userNameTxt, lastNameTxt, passwordTxt, confirmPassTxt, mobilNumberTxt, emailTxt;
    private String userName, lastName, password, confirmPass, email, mobile;
    private LinearLayout goToLoginLayout;

    private Pref pref;
    private Gson gson = new Gson();
    private final int REQUEST_SIGN_UP = 1002;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);

        pref = new Pref(this);

        userNameTxt = findViewById(R.id.signup_username);
        lastNameTxt = findViewById(R.id.signup_user_last_name);
        passwordTxt = findViewById(R.id.signup_password);
        confirmPassTxt = findViewById(R.id.signup_confirmpassword);
        mobilNumberTxt = findViewById(R.id.signup_mobilenumber);
        emailTxt = findViewById(R.id.signup_emailid);
        signUpBtn = findViewById(R.id.signup_button);
        goToLoginLayout = findViewById(R.id.signup_go_to_login_layout);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getCurrentFocus() != null) {
                    hideKeyboard(getCurrentFocus());
                } else {
                    hideKeyboard(new View(SignUpActivity.this));
                }
                if (isInputValid()) {
                    submitSignUp();
                    //dialogWarning(SignUpActivity.this, "Feature Adding Soon...");
                }
            }
        });

        goToLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isInputValid() {
        userName = userNameTxt.getText().toString().trim();
        lastName = lastNameTxt.getText().toString().trim();
        password = passwordTxt.getText().toString().trim();
        confirmPass = confirmPassTxt.getText().toString().trim();
        mobile = mobilNumberTxt.getText().toString().trim();
        email = emailTxt.getText().toString().trim();
        //Log.d("password", password);
        //Log.d("confirmPass", confirmPass);

        userNameTxt.setError(null);
        lastNameTxt.setError(null);
        passwordTxt.setError(null);
        confirmPassTxt.setError(null);
        mobilNumberTxt.setError(null);
        emailTxt.setError(null);

        if (!isDataValid(userName)) {
            userNameTxt.setError("First Name is required");
            userNameTxt.requestFocus();
            return false;
        } else if (!isDataValid(lastName)) {
            lastNameTxt.setError("Last Name is required");
            lastNameTxt.requestFocus();
            return false;
        } else if (!isDataValid(password)) {
            passwordTxt.setError("Password required");
            passwordTxt.requestFocus();
            return false;
        } else if (!password.equals(confirmPass)) {
            confirmPassTxt.setError("Password mismatch");
            confirmPassTxt.requestFocus();
            return false;
        } else if (!isValidPass(password)) {
            passwordTxt.setError("Password must contain Lower Case, Upper Case, Digits & Length must be equal or greater than 8");
            passwordTxt.requestFocus();
            return false;
        } else if (!isDataValid(mobile)) {
            mobilNumberTxt.setError("Mobile Number required");
            mobilNumberTxt.requestFocus();
            return false;
        } /*else if (!isValidMobile(mobile)) {
            mobilNumberTxt.setError("Invalid Mobile Number");
            mobilNumberTxt.requestFocus();
            return false;
        }*/ else if (!isDataValid(email)) {
            emailTxt.setError("Email required");
            emailTxt.requestFocus();
            return false;
        } else if (!isValidEmail(email)) {
            emailTxt.setError("Invalid Email Id");
            emailTxt.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void submitSignUp() {
        JSONObject map = new JSONObject();
        try {
            JSONObject customer = new JSONObject();
            JSONArray addressArray = new JSONArray();
            JSONObject addressItem = new JSONObject();
            JSONObject regionItem = new JSONObject();
            JSONArray streetArray = new JSONArray();

            regionItem.put("region", "KUWAIT");
            regionItem.put("regionCode", "KW");
            regionItem.put("regionId", 43);

            streetArray.put("123 Oak Ave");

            addressItem.put("region", regionItem);
            addressItem.put("street", streetArray);
            addressItem.put("city", "Purchase");
            addressItem.put("firstname", userName);
            addressItem.put("lastname", lastName);
            addressItem.put("postcode", "10755");
            addressItem.put("default_billing", true);
            addressItem.put("countryId", "US");
            addressItem.put("default_shipping", true);
            addressItem.put("telephone", mobile);

            addressArray.put(addressItem);

            customer.put("email", email);
            customer.put("firstname", userName);
            customer.put("lastname", lastName);
            customer.put("addresses", addressArray);

            map.put("customer", customer);
            map.put("password", password);

            Log.d("doJSON Request", map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkManager(this).doPostCustom(
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_USER_SIGN_UP,
                JsonObject.class,
                map,
                ACCESS_TOKEN,
                "TAG_SIGN_UP",
                REQUEST_SIGN_UP,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }

    private void processJsonSignUp(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<SignUpResponse>(){}.getType();
                SignUpResponse signUpResponse = gson.fromJson(response, type);
                Log.d("signUpResponse", response);
                LoadingDialog.cancelLoading();
                if (signUpResponse.getId().equals(null)) {
                    dialogWarning(SignUpActivity.this, signUpResponse.getMessage());
                } else {
                    Toast.makeText(this, "Sign Up Successful. Please login to continue.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(SignUpActivity.this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonSignUp", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(SignUpActivity.this, "Sorry ! Can't connect to server, try later");
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_SIGN_UP) {
                    processJsonSignUp(response);
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
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_SIGN_UP) {
                    processJsonSignUp(response);
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}
