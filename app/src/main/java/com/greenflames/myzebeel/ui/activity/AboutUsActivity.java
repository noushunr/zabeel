package com.greenflames.myzebeel.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.greenflames.myzebeel.R;

import static com.greenflames.myzebeel.helpers.Global.AboutUsDesc;

public class AboutUsActivity extends AppCompatActivity {

    private TextView aboutUsDescTxt;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        aboutUsDescTxt = findViewById(R.id.about_us_txt);
        aboutUsDescTxt.setText(AboutUsDesc);

        back = findViewById(R.id.about_us_back_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}