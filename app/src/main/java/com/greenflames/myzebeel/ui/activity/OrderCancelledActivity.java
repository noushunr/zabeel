package com.greenflames.myzebeel.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.greenflames.myzebeel.R;

public class OrderCancelledActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cancelled);

        linearLayout = findViewById(R.id.linear_continue);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderCancelledActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderCancelledActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}