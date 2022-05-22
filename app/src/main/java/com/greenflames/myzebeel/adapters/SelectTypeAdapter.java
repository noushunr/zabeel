package com.greenflames.myzebeel.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.models.detailproducts.CustomAttributes;
import com.greenflames.myzebeel.models.detailproducts.PriceChildren;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.preferences.Pref;

import java.util.ArrayList;
import java.util.List;

import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;

/**
 * Created by Adithya T Raj on 01-09-2021.
 **/

public class SelectTypeAdapter extends RecyclerView.Adapter<SelectTypeAdapter.ItemViewHolder> {
    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 2;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;

    private List<PriceChildren> mItems = new ArrayList<PriceChildren>();
    private GridLayoutManager mLayoutManager;
    private OnSelectTypeClickListener mListener;

    public SelectTypeAdapter(List<PriceChildren> items, GridLayoutManager layoutManager, OnSelectTypeClickListener listener) {
        mItems = items;
        mLayoutManager = layoutManager;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_big_select_type, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_small_select_type, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        PriceChildren item = mItems.get(position);
        holder.bind(item, mListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView nameTextView, priceTextView, qtyTextView, configTextView, decrementTextView, incrementTextView;
        Button addCartBtn;
        AppCompatImageView wishlistImageView;
        int qty = 1;

        ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_BIG) {
                nameTextView = itemView.findViewById(R.id.product_name_textView);
                priceTextView = itemView.findViewById(R.id.product_price_textView);
                qtyTextView = itemView.findViewById(R.id.display_inc_dec2);
                configTextView = itemView.findViewById(R.id.product_config_textView);
                decrementTextView = itemView.findViewById(R.id.decrement2);
                incrementTextView = itemView.findViewById(R.id.increment2);
                productImageView = itemView.findViewById(R.id.imageView_cart_product);
                addCartBtn = itemView.findViewById(R.id.add_cart);
                wishlistImageView = itemView.findViewById(R.id.add_wishlist);
            } else {
                nameTextView = itemView.findViewById(R.id.product_name_textView);
                priceTextView = itemView.findViewById(R.id.product_price_textView);
                qtyTextView = itemView.findViewById(R.id.display_inc_dec2);
                configTextView = itemView.findViewById(R.id.product_config_textView);
                decrementTextView = itemView.findViewById(R.id.decrement2);
                incrementTextView = itemView.findViewById(R.id.increment2);
                productImageView = itemView.findViewById(R.id.imageView_cart_product);
                addCartBtn = itemView.findViewById(R.id.add_cart);
                wishlistImageView = itemView.findViewById(R.id.add_wishlist);
            }
        }

        void bind(PriceChildren item, OnSelectTypeClickListener callback) {
            nameTextView.setText(item.getName());
            priceTextView.setText("KWD " + item.getPrice());

            qtyTextView.setText("" + qty);

            incrementTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty < 21) {
                        qty = qty + 1;
                        qtyTextView.setText("" + qty);
                    }
                }
            });

            decrementTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty > 1) {
                        qty = qty - 1;
                        qtyTextView.setText("" + qty);
                    }
                }
            });

            try {
                ArrayList<CustomAttributes> customAttributesArrayList = item.getCustom_attributes();
                for (int i = 0; i < customAttributesArrayList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesArrayList.get(i);
                    String attribute_code = customAttributes.getAttribute_code();
                    if (attribute_code.equals("image")) {
                        String image = customAttributes.getValue().toString();
                        Glide.with(productImageView)
                                .load(Apis.PRODUCT_IMG_BASE_URL + image)
                                .error(R.drawable.place_holder)
                                .placeholder(R.drawable.place_holder)
                                .into(productImageView);
                    }

                }
            } catch (Exception e) {

            }

            addCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onAddCartClick(item, "" + qty);
                }
            });

            wishlistImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pref pref = new Pref(itemView.getContext());
                    String loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);
                    if (loginStatus.equals("true"))
                        wishlistImageView.setImageResource(R.drawable.ic_favorite);
                    wishlistImageView.setOnClickListener(null);
                    callback.onWishListClick(item);
                }
            });
        }
    }

    public interface OnSelectTypeClickListener {
        void onAddCartClick(PriceChildren item, String qty);
        void onWishListClick(PriceChildren item);
    }

}
