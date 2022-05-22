package com.greenflames.myzebeel.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.SplashAdapter;
import com.greenflames.myzebeel.preferences.Pref;

import java.util.Timer;

import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_LANGUAGE;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ONBOARD;

public class OnBoardActivity extends AppCompatActivity {

    ViewPager viewPager;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    int Total_images;
    SplashAdapter myCustomPagerAdapter;
    private int dotscount;
    private ImageView[] dots;
    LinearLayout sliderDotspanel;
    Button start_button;

    Pref pref;

//    public static int[] image;
//    public static String[] txt_content;

    int image[] = {R.drawable.logo, R.drawable.logo, R.drawable.logo};
    String txt_content[] = {"Eat Organic","text 2", "text 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        pref = new Pref(this);
        pref.putString(PREF_ZABEEL_ONBOARD, "true");
        pref.putString(PREF_LANGUAGE,"en");

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        start_button = (Button)findViewById(R.id.start_button);

        myCustomPagerAdapter = new SplashAdapter(OnBoardActivity.this, image,txt_content);
        viewPager.setAdapter(myCustomPagerAdapter);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Intent mainIntent = new Intent(OnBoardActivity.this, LoginActivity.class);
                final Intent mainIntent = new Intent(OnBoardActivity.this, HomeActivity.class);
                OnBoardActivity.this.startActivity(mainIntent);
                OnBoardActivity.this.finish();
            }
        });


        //to change dots of slider
        dotscount = myCustomPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getApplication());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                //Toast.makeText(Home_activity.this, ""+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageSelected(int position) {

                //to change dots of slider

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                //to change dots of slider
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });
    }
}