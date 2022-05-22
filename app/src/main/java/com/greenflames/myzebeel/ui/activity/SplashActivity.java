package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Locale;

import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_EMAIL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_PASSWORD;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_LANGUAGE;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ONBOARD;

public class SplashActivity extends AppCompatActivity
        implements NetworkCallbacks {

    Pref pref;
    private final int REQUEST_ADMIN_TOKEN = 1003;
    String loginStatus;

    private PlayerView playerView;
    private MediaSource mVideoSource;
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;

    private int mResumeWindow = 0;
    private long mResumePosition = 0;
    private boolean nextActivity = false;

    private Gson gson = new Gson();
    private final int REQUEST_LOGIN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);
        //fetchAdminToken();

        String languageCode = pref.getString(PREF_LANGUAGE);
        if (languageCode.isEmpty()){
            languageCode = "en";
        }
        Global.STORE_LANGUAGE = languageCode+"/";
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        playerView = findViewById(R.id.intro_video_exoPlayerView);
        dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, getString(R.string.app_name))
        );

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginStatus.equals("true")) {
                    goToHomeActivity();
                } else {
                    String onBoard = pref.getString(PREF_ZABEEL_ONBOARD);
                    if (onBoard.equals("true")) {
                        goToLoginActivity();
                    } else {
                        goToOnBoardActivity();
                    }
                }
            }
        }, 3000);*/

    }

    private void initExoPlayer() {
        player = new SimpleExoPlayer.Builder( this)
                .build();
        playerView.setPlayer(player);
        playerView.requestFocus();
        playerView.hideController();

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        String uriMp4 = "file:///android_asset/intro/zebeel_intro.mp4";
        Uri mp4VideoUri = Uri.parse(uriMp4);

        mVideoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);

        player.prepare(mVideoSource);
        player.setPlayWhenReady(true);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            player.seekTo(mResumeWindow, mResumePosition);
        }

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    nextActivity = true;
                    fetchAdminToken();
                }
            }
        });
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToOnBoardActivity() {
        Intent intent = new Intent(SplashActivity.this, OnBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchAdminToken() {
        JSONObject map = new JSONObject();
        try {
            map.put("username", "mobileapp");
            map.put("password", "Zeb@$7127");
            Log.d("doJSON Request", map.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkManager(this).doPostCustom(
                Apis.API_POST_ADMIN_TOKEN,
                Object.class,
                map,
                ACCESS_TOKEN,
                "TAG_ADMIN_TOKEN",
                REQUEST_ADMIN_TOKEN,
                this
        );
        LoadingDialog.showLoadingDialog(this,"Loading...");
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
        LoadingDialog.showLoadingDialog(this,"Loading...");
    }

    private void processJsonAdmin(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<String>(){}.getType();
                String adminToken = gson.fromJson(response, type);
                Log.d("adminToken", adminToken);
                pref.putString(PREF_ZABEEL_ADMIN_TOKEN, adminToken);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (loginStatus.equals("true")) {
                            //goToHomeActivity();
                            fetchLogin();
                        } else {
                            String onBoard = pref.getString(PREF_ZABEEL_ONBOARD);
                            if (onBoard.equals("true")) {
                                //goToLoginActivity();
                                goToHomeActivity();
                            } else {
                                goToOnBoardActivity();
                            }
                        }
                    }
                }, 200);

                LoadingDialog.cancelLoading();

            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(SplashActivity.this, errorMessage.getMessage());
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

    private void processJsonLogin(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<String>(){}.getType();
                String userToken = gson.fromJson(response, type);
                Log.d("userToken", userToken);
                LoadingDialog.cancelLoading();
                pref.putString(PREF_ZABEEL_USER_TOKEN, userToken);
                pref.putString(PREF_USER_LOGIN_STATUS, "true");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goToHomeActivity();
                    }
                }, 200);

            } catch (Exception e1) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String onBoard = pref.getString(PREF_ZABEEL_ONBOARD);
                        if (onBoard.equals("true")) {
                            goToLoginActivity();
                        } else {
                            goToOnBoardActivity();
                        }
                    }
                }, 200);
            }
        } else {
            serverErrorDialog();
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(SplashActivity.this, "Sorry ! Can't connect to server, try later");
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_ADMIN_TOKEN) {
                    processJsonAdmin(response);
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
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!nextActivity) {
            initExoPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView != null && player != null) {
            mResumeWindow = player.getCurrentWindowIndex();
            mResumePosition = Math.max(0, player.getCurrentPosition());
            player.release();
        }
    }
}


