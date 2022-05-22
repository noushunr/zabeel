package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.greenflames.myzebeel.R;

public class ContactUsActivity extends AppCompatActivity {

    private ImageButton back;
    private Button firstCallButton, secondCallButton, mailToButton;
    private TextView mailToTxt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        firstCallButton = findViewById(R.id.contact_first_number_btn);
        secondCallButton = findViewById(R.id.contact_second_number_btn);
        mailToTxt = findViewById(R.id.contact_mail_to_txt);
        mailToButton = findViewById(R.id.contact_mail_to_btn);

        back = findViewById(R.id.imageButton_back_contact);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        firstCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall("+96594115237");
            }
        });

        secondCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall("+96597906850");
            }
        });

        /*mailToTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });*/

        mailToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });
    }

    private void startCall(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        } catch (Exception e) {
            //
        }
    }

    public void composeEmail() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:info@myzebeel.com"));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
