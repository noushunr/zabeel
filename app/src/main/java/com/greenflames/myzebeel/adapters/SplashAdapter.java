package com.greenflames.myzebeel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.preferences.Pref;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;

public class SplashAdapter extends PagerAdapter {
    Context context;
    String txt[];
    int images[];
    LayoutInflater layoutInflater;
    ArrayList<String> checked_sub_category=new ArrayList<>();
    ArrayList<String> checked_brand=new ArrayList<>();
    Pref tinydb;




    public SplashAdapter(Context context, int images[], String txt[]) {
        this.context = context;
        this.images = images;
        this.txt = txt;
        tinydb = new Pref(context);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.splash_slider, container, false);

//        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
//        TextView text_view = (TextView) itemView.findViewById(R.id.txt_content);


//         imageView.setImageResource(images[position]);
//         text_view.setText(txt[position]);

//        Glide
//                .with(context)
//                .load(images[position])
//                .into(imageView);



        container.addView(itemView);
        return itemView;
    }






    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}