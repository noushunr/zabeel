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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.preferences.Pref;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.helpers.Global.isDataValid;
import static com.greenflames.myzebeel.helpers.Global.isValidEmail;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_EMAIL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_PASSWORD;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class LoginActivity extends AppCompatActivity implements NetworkCallbacks {

    private EditText usernameTxt, passwordTxt,etEmail;
    private TextView tvForgot;
    private Button loginBtn,btnSubmit;
    private LinearLayout goToSignUpLayout;
    private String userName, password,emailId;

    private Pref pref;
    private Gson gson = new Gson();
    private final int REQUEST_LOGIN = 1001;

    private final int REQUEST_FORGOT_PASSWORD = 2555;

    private BottomSheetBehavior behavior;
    private FrameLayout overLay;
    private ImageButton  image_close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        pref = new Pref(this);
        pref.putString(PREF_ZABEEL_USER_TOKEN, "");
        pref.putString(PREF_USER_LOGIN_STATUS, "");

        usernameTxt = (EditText) findViewById(R.id.login_user_email);
        passwordTxt = (EditText) findViewById(R.id.login_user_password);
        loginBtn = (Button) findViewById(R.id.login_button);
        tvForgot = findViewById(R.id.text_forgot_password);
        etEmail = findViewById(R.id.emailId);
        btnSubmit = findViewById(R.id.submit_btn);
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentFocus() != null) {
                    hideKeyboard(getCurrentFocus());
                } else {
                    hideKeyboard(new View(LoginActivity.this));
                }
                emailId = etEmail.getText().toString();
                if (!isValidEmail(emailId)){
                    etEmail.setError("Invalid Email");
                    etEmail.requestFocus();
                }else {

                    submitForgotPassword();
                }
            }
        });
        goToSignUpLayout = (LinearLayout) findViewById(R.id.login_go_to_sign_up_layout);
        overLay = findViewById(R.id.overlay);
        overLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        View bottomSheet = findViewById(R.id.forgot_bottom_sheet);
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getCurrentFocus() != null) {
                    hideKeyboard(getCurrentFocus());
                } else {
                    hideKeyboard(new View(LoginActivity.this));
                }

                if (isInputValid()) {
                    submitLogin();
                }

            }


//        private void userlogin() {
////
//            final ProgressDialog progressDialog = new ProgressDialog(Login_activity.this);
//            progressDialog.setCancelable(false); // set cancelable to false
//            progressDialog.setMessage("Please Wait"); // set message
//            progressDialog.show();
////
////            Api.getClient().userLogin(username.getText().toString().trim(),
////                    password.getText().toString().trim(),
////                    new Callback<LoginResponse>(){
////                        @Override
////                        public void success(LoginResponse loginResponse, Response response) {
////                            progressDialog.dismiss();
//////                                loginResponseData=loginResponse;
//////                                loginResponse= (LoginResponse) response.getBody();
////                            if (!loginResponse.getErrors()) {
////
//////                                    SharedPrefManager.getInstance(LoginActivity.this)
//////                                            .saveUser(loginResponse.getDetails());
////                                String id=loginResponse.getUsername().getId();
////                                SharedPreferences mPrefs = getSharedPreferences("id ", MODE_PRIVATE);
////                                SharedPreferences.Editor editor = mPrefs.edit();
////                                editor.putString("id", id);
//////
////                                editor.apply();
////
////
////                                Intent intent = new Intent(Login_activity.this, Home_activity.class);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                startActivity(intent);
////                                Toast.makeText(Login_activity.this,loginResponse.getMessage()+loginResponse.getDetails().getId(), Toast.LENGTH_SHORT).show();
////
////
////                            } else {
////                                Toast.makeText(Login_activity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
////                            }
////                        }
////
////                        @Override
////                        public void failure(RetrofitError error) {
////                            Toast.makeText(Login_activity.this, error.toString(), Toast.LENGTH_LONG).show();
////                            progressDialog.dismiss();
////                        }
////                    });
//
//            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
//            StringRequest stringRequest=new StringRequest(Request.Method.POST, "http://zebeel.nuevoinformatica.com/rest/en/V1/integration/customer/token", new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//
//                    Log.d("responsedata",response);
//                    String responses="";
//                    String details="";
//                    if (response!=null){
//                        try {
//                            JSONObject jsonObject=new JSONObject(response);
//                            responses=jsonObject.getString("response");
//
//                            if (responses.contains("true")){
//                                progressDialog.dismiss();
//                                Intent intent = new Intent(getApplicationContext(), Home_activity.class);
//                                startActivity(intent);
//
//                                Log.e("errorfalse",jsonObject.getString("message"));
//
//                            }else if (responses.contains("false")){
//                                progressDialog.dismiss();
//                                Log.e("errortrue",jsonObject.getString("message"));
//                                Toast.makeText(Login_activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//
////                              JSONArray object=jsonObject.getJSONArray("customer");
////                                JSONObject object1=object.getJSONObject(0);
////
////                                String id=object1.getString("id");
////                                String email=object1.getString("email");
////                                String firstname=object1.getString("firstname");
////                                String lastname=object1.getString("lastname");
////
////                                //address
////                                String addresses=object1.getString("addresses");
////                                String defaultShipping=object1.getString("defaultShipping");
////                                String defaultBilling=object1.getString("defaultBilling");
////                                String region=object1.getString("region");
////                                String regionCode=object1.getString("regionCode");
////                                String regionId=object1.getString("regionId");
////                                String postcode=object1.getString("postcode");
////                                String street=object1.getString("street");
////                                String city=object1.getString("city");
////                                String telephone=object1.getString("telephone");
////                                String countryId=object1.getString("countryId");
////                                String password=object1.getString("password");
////
////                                tinydb.putString("login_check","true");
////                                tinydb.putString("id",id);
////                                tinydb.putString("email",email);
////                                tinydb.putString("firstname",firstname);
////                                tinydb.putString("lastname",lastname);
////                                tinydb.putString("addresses",addresses);
////                                tinydb.putString("defaultShipping",defaultShipping);
////                                tinydb.putString("defaultBilling",defaultBilling);
////                                tinydb.putString("region",region);
////                                tinydb.putString("regionCode",regionCode);
////                                tinydb.putString("regionId",regionId);
////                                tinydb.putString("postcode",postcode);
////                                tinydb.putString("street",street);
////                                tinydb.putString("city",city);
////                                tinydb.putString("telephone",telephone);
////                                tinydb.putString("countryId",countryId);
////                                tinydb.putString("password",password);
//
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            progressDialog.dismiss();
//                            Toast.makeText(Login_activity.this,"Exception"+ e.toString(), Toast.LENGTH_SHORT).show();
//                            Log.e("exception",e.toString());
//                        }
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progressDialog.dismiss();
//                    Toast.makeText(Login_activity.this, "Volley_error"+error.toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("error",error.toString());
//                }
//            })
//            {
////                private static final String ACCESS_TOKEN ="irzirtam69as624mkobg20c5zvdbxypr";
//
//                protected Map<String,String> getParams() {
//                    Map<String,String> params=new HashMap<String, String>();
//                    params.put("username",username1);
//                    params.put("password",password1);
//                    return params;
//                }
////                public Map<String, String> getHeaders() throws AuthFailureError {
////                    Map<String, String> headerMap = new HashMap<String, String>();
////                    headerMap.put("Content-Type", "application/json");
////                    headerMap.put("Authorization", ACCESS_TOKEN);
////                    return headerMap;
////                }
//
//
//            };
//            requestQueue.add(stringRequest);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        }
        });


        goToSignUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToHomeActivity();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isInputValid() {
        userName = usernameTxt.getText().toString().trim();
        password = passwordTxt.getText().toString().trim();

        usernameTxt.setError(null);
        passwordTxt.setError(null);

        if (!isDataValid(userName)) {
            usernameTxt.setError("Username is required");
            usernameTxt.requestFocus();
            return false;
        } else if (!isDataValid(password)) {
            passwordTxt.setError("Password required");
            passwordTxt.requestFocus();
            return false;
        } else if (password.length() < 6) {
            passwordTxt.setError("Password should be atleast 6 character long");
            passwordTxt.requestFocus();
            return false;
        } else {
            return true;
        }

        /*if (!Patterns.EMAIL_ADDRESS.matcher(username1).matches()) {
            username.setError("Enter a valid email");
            username.requestFocus();
            return false;
        }*/

    }

    private void submitLogin() {
        JSONObject map = new JSONObject();
        try {
            map.put("username", userName);
            map.put("password", password);
            Log.d("doJSON Request", map.toString());
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
        LoadingDialog.showLoadingDialog(this,"Loading...");
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
                pref.putString(PREF_CUSTOMER_CRED_EMAIL, userName);
                pref.putString(PREF_CUSTOMER_CRED_PASSWORD, password);
                pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                goToHomeActivity();

            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(LoginActivity.this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonLogin", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(LoginActivity.this, "Sorry ! Can't connect to server, try later");
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_LOGIN) {
                    processJsonLogin(response);
                } else if (requestId == REQUEST_FORGOT_PASSWORD) {
                    processJsonForgotPassword(response);
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


    private void submitForgotPassword() {
        JSONObject map = new JSONObject();
        try {
            map.put("email", emailId);
            map.put("template",  "email_reset");
            Log.d("doJSON Request", map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkManager(this).doPutCustom(
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_USER_FORGOT_PASSWORD,
                Object.class,
                map,
                ACCESS_TOKEN,
                "TAG_FORGOT",
                REQUEST_FORGOT_PASSWORD,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }

    @Override
    public void onJsonResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_LOGIN) {
                    processJsonLogin(response);
                }
                else if (requestId == REQUEST_FORGOT_PASSWORD) {

                        processJsonForgotPassword(response);


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

    private void processJsonForgotPassword(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<Boolean>(){}.getType();
                boolean status = gson.fromJson(response, type);
                if (status){
                    LoadingDialog.cancelLoading();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(LoginActivity.this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonLogin", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }
}
