package com.greenflames.myzebeel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.preferences.Pref;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;

import static com.greenflames.myzebeel.network.Apis.PRODUCT_IMG_BASE_URL;

/**
 * Created by sheff on 23-09-2020.
 */

public class ProductImageSlider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    Pref tinydb;
    ArrayList<String> product_images = new ArrayList<>();
//    private Integer [] images = {R.drawable.product1,R.drawable.product1,R.drawable.product1};
    private String [] images;

    public ProductImageSlider(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        tinydb = new Pref(context);

        String image_check1= tinydb.getString("product_images");
        if(image_check1.contains(","))
        {
            String image_check = image_check1.substring(1, image_check1.length() - 1);
            image_check = image_check.trim();
            images = image_check.split(",");
            for (int i=0;i<images.length;i++)
            {
                product_images.add(images[i].trim());

            }
        }
        else
        {
            images = new String[1];
            images[0]=image_check1;
            product_images.add(image_check1.trim());
        }


    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
//        return view == object;

        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {

//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.product_image_slider, null);
//        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//        imageView.setImageResource(images[position]);

//        Glide
//                .with(context)
//                .load(Config_urls.image_url+product_images.get(position))
//                .into(imageView);

//        ViewPager vp = (ViewPager) container;
//        vp.addView(view, 0);
//        return view;


        View imageLayout = layoutInflater.inflate(R.layout.product_image_slider, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.imageView);


        Glide
                .with(context)
                .load(PRODUCT_IMG_BASE_URL + product_images.get(position))
                .into(imageView);


        view.addView(imageLayout, 0);

        return imageLayout;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

//        ViewPager vp = (ViewPager) container;
//        View view = (View) object;
//        vp.removeView(view);

        container.removeView((View) object);

    }
}
