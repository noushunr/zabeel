package com.greenflames.myzebeel.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.greenflames.myzebeel.R;

public class ShareAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_app_activity);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
        intent.putExtra(Intent.EXTRA_TEXT, "This is my text");
        startActivity(Intent.createChooser(intent, "choose one"));

    }
}
