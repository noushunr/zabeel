package com.greenflames.myzebeel.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.ProductImageSlider;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.detailproducts.PriceChildren;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.cart.CartItem;
import com.greenflames.myzebeel.models.detailproducts.ConfigItemOptions;
import com.greenflames.myzebeel.models.detailproducts.ConfigProductOptions;
import com.greenflames.myzebeel.models.detailproducts.ConfigValues;
import com.greenflames.myzebeel.models.detailproducts.ConfigValuesExtensionAttributes;
import com.greenflames.myzebeel.models.detailproducts.CustomAttributes;
import com.greenflames.myzebeel.models.detailproducts.ProductDetail;
import com.greenflames.myzebeel.models.products.MediaGalleryEntries;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ru.nikartm.support.ImageBadgeView;

import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_EMAIL;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_CRED_PASSWORD;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_COUNT;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_CART_ID;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_SKU;

public class ProductDetailActivity extends AppCompatActivity implements NetworkCallbacks {
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    ImageView  back;
    RecyclerView recyclerView;
    LinearLayout linearLayout, similarpro, addWishList;
    Dialog myDialog;
    Pref pref;
    String pro_name, detail, file, valueorig, valuepack, meta_description;
    TextView pr_name, pr_price, pr_sku, pr_avail, pr_description, similartext;
    ArrayList<String> origin = new ArrayList<String>();
    ArrayList<String> packing = new ArrayList<String>();
    String description, relatedsku, linktype, linkedprosku, linkedprotype, position, price, id, status, value1, skurelated, link_type, linked_product_sku, linked_product_type, category_id;
    private static final Integer[] product_images = {R.drawable.avocado1, R.drawable.apple, R.drawable.orange};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    ArrayList product_image = new ArrayList();
    ArrayList product_name = new ArrayList();
    ArrayList product_prcie = new ArrayList();
    ArrayList availability = new ArrayList();
    ArrayList descriptionarray = new ArrayList();
    ArrayList product_sku = new ArrayList();
    ArrayList skurelatedarray = new ArrayList();
    ArrayList link_typearray = new ArrayList();
    ArrayList linkedproductskuarray = new ArrayList();
    ArrayList linkedproducttypearray = new ArrayList();
    ArrayList positionarray = new ArrayList();
    TextView decrement, increment, count;
    int qty;
    private String nameProduct = "", nameOrigin = "", namePacking = "";

    private final int REQUEST_PRODUCT_DETAIL = 1008;
    private final int REQUEST_ADD_CART_SIMPLE = 1011;
    private final int REQUEST_ADD_CART_CONFIG = 1012;
    private final int REQUEST_ADD_WISH_LIST = 1016;
    private final int REQUEST_CONFIG_DETAIL_CHILD = 1039;

    private Gson gson = new Gson();
    private static final String TAG = ProductDetailActivity.class.getName();

    ArrayList<ConfigItemOptions> originList = new ArrayList<ConfigItemOptions>();
    ArrayList<ConfigItemOptions> packingList = new ArrayList<ConfigItemOptions>();
    ArrayList<PriceChildren> productChildrenList = new ArrayList<>();
    ConfigItemOptions selectedOrigin, selectedPacking;

    private Boolean isInStock = true;

    private final int REQUEST_CART_LIST = 1015;
    private ImageBadgeView cart_btn;
    private String loginStatus;
    private ArrayList<ConfigProductOptions> configProductOptionsArrayList = new ArrayList<ConfigProductOptions>();
    private ConfigItemOptions selectedOption1, selectedOption2, selectedOption3, selectedOption4, selectedOption5;
    private ArrayList<ConfigItemOptions> itemOptions1List = new ArrayList<ConfigItemOptions>();
    private ArrayList<ConfigItemOptions> itemOptions2List = new ArrayList<ConfigItemOptions>();
    private ArrayList<ConfigItemOptions> itemOptions3List = new ArrayList<ConfigItemOptions>();
    private ArrayList<ConfigItemOptions> itemOptions4List = new ArrayList<ConfigItemOptions>();
    private ArrayList<ConfigItemOptions> itemOptions5List = new ArrayList<ConfigItemOptions>();
    private int position1 = 0, position2 = 0, position3 = 0, position4 = 0, position5 = 0;

    private final int REQUEST_LOGIN = 1001;

    private final Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable;
    private final int delay = 599*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_activity2);

        pref = new Pref(ProductDetailActivity.this);

        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        pro_name = pref.getString(PREF_ZABEEL_PRODUCT_SKU);
        Log.d("pihbbbnn", pro_name);
        detail = "http://demo.myzebeel.com/rest/en/V1/products/" + pro_name;
        myDialog = new Dialog(this);

//        description=tinyDB.getString("description");
//        relatedsku=tinyDB.getString("relatedsku");
//        linktype=tinyDB.getString("linktype");
//        linkedprosku=tinyDB.getString("linkedprosku");
//        linkedprotype=tinyDB.getString("linkedprotype");
//        position=tinyDB.getString("position");
//        product_name.add(relatedsku);
//        product_prcie.add(price);
//        product_image.add(file);
//        availability.add(status);
//        descriptionarray.add(description);

        pr_name = findViewById(R.id.txt_prod_name);
        pr_avail = findViewById(R.id.txt_availability);
        pr_sku = findViewById(R.id.txt_sku);
        pr_price = findViewById(R.id.txt_price);
        pr_description = findViewById(R.id.txt_description);

        linearLayout = findViewById(R.id.buy_now);
        similarpro = findViewById(R.id.linear_similar);
        similartext = findViewById(R.id.similar_txt);
        addWishList = findViewById(R.id.product_add_wish_list_layout);
//        linearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent=new Intent(Product_detail_activity.this, Cart_activity.class);
////                startActivity(intent);
//            showpopupcart(View v);
//            }
//        });

        recyclerView = findViewById(R.id.recyclerView_similarProducts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, linearLayoutManager.HORIZONTAL, false));


        cart_btn = findViewById(R.id.cart_detail);

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mainIntent = new Intent(ProductDetailActivity.this, CartActivity.class);
                ProductDetailActivity.this.startActivity(mainIntent);
//                if (loginStatus.equals("true")) {
//                    final Intent mainIntent = new Intent(ProductDetailActivity.this, CartActivity.class);
//                    ProductDetailActivity.this.startActivity(mainIntent);
//                } else {
//                    Toast.makeText(ProductDetailActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
//                    goToLogin();
//                }
            }
        });
        back = findViewById(R.id.back_detail);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        decrement = (TextView) findViewById(R.id.decrement2);
        increment = (TextView) findViewById(R.id.increment2);
        count = (TextView) findViewById(R.id.display_inc_dec2);

        qty = Integer.valueOf(count.getText().toString());
        pref.putString("qty", String.valueOf(1));

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (qty <= 20) {
                    qty = qty + 1;
                    count.setText(String.valueOf(qty));
                    pref.putString("qty", String.valueOf(qty));
                }

            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty > 1) {
                    qty = qty - 1;
                    count.setText(String.valueOf(qty));
                    pref.putString("qty", String.valueOf(qty));
                }
            }
        });

        //productDetail();
        //relatedproducts();

        fetchProductDetail();

        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            cart_btn.setBadgeValue(pref.getInt(PREF_ZABEEL_CART_COUNT));
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void setUpDetailPage(ProductDetail productDetail) {
        String sku = productDetail.getSku();
        String name = productDetail.getName();
        nameProduct = name;
        price = productDetail.getPrice();
        status = productDetail.getStatus();
        id = productDetail.getId();
        packing.clear();
        origin.clear();
        packingList.clear();
        originList.clear();

        if (productDetail.getType_id().equals("configurable")) {
            configProductOptionsArrayList.clear();
            configProductOptionsArrayList = productDetail.getExtension_attributes().getConfigurable_product_options();
            if (configProductOptionsArrayList != null) {

                myDialog.setContentView(R.layout.productdetail_popup);
                Button submit = myDialog.findViewById(R.id.submit_btn);

                LinearLayout main1 = myDialog.findViewById(R.id.spinner_one_main);
                LinearLayout main2 = myDialog.findViewById(R.id.spinner_two_main);
                LinearLayout main3 = myDialog.findViewById(R.id.spinner_three_main);
                LinearLayout main4 = myDialog.findViewById(R.id.spinner_four_main);
                LinearLayout main5 = myDialog.findViewById(R.id.spinner_five_main);

                TextView text1 = myDialog.findViewById(R.id.spinner_one_txt);
                TextView text2 = myDialog.findViewById(R.id.spinner_two_txt);
                TextView text3 = myDialog.findViewById(R.id.spinner_three_txt);
                TextView text4 = myDialog.findViewById(R.id.spinner_four_txt);
                TextView text5 = myDialog.findViewById(R.id.spinner_five_txt);

                Spinner spinner1 = myDialog.findViewById(R.id.spinner_one);
                Spinner spinner2 = myDialog.findViewById(R.id.spinner_two);
                Spinner spinner3 = myDialog.findViewById(R.id.spinner_three);
                Spinner spinner4 = myDialog.findViewById(R.id.spinner_four);
                Spinner spinner5 = myDialog.findViewById(R.id.spinner_five);

                TextView textView = myDialog.findViewById(R.id.close_three);

                TextView priceTextView = myDialog.findViewById(R.id.config_child_price);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(myDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                myDialog.getWindow().setAttributes(lp);

                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //myDialog.show();

                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            position1 = position;
                            selectedOption1 = itemOptions1List.get(position - 1);
                            if (configProductOptionsArrayList.size() == 2) {
                                setChildPriceTwo(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 3) {
                                setChildPriceThree(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 4) {
                                setChildPriceFour(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 5) {
                                setChildPriceFive(priceTextView);
                            }
                        } else {
                            position1 = 0;
                            selectedOption1 = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            position2 = position;
                            selectedOption2 = itemOptions2List.get(position - 1);
                            if (configProductOptionsArrayList.size() == 2) {
                                setChildPriceTwo(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 3) {
                                setChildPriceThree(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 4) {
                                setChildPriceFour(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 5) {
                                setChildPriceFive(priceTextView);
                            }
                        } else {
                            position2 = 0;
                            selectedOption2 = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            position3 = position;
                            selectedOption3 = itemOptions3List.get(position - 1);
                            if (configProductOptionsArrayList.size() == 3) {
                                setChildPriceThree(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 4) {
                                setChildPriceFour(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 5) {
                                setChildPriceFive(priceTextView);
                            }
                        } else {
                            position3 = 0;
                            selectedOption3 = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            position4 = position;
                            selectedOption4 = itemOptions4List.get(position);
                            if (configProductOptionsArrayList.size() == 4) {
                                setChildPriceFour(priceTextView);
                            } else if (configProductOptionsArrayList.size() == 5) {
                                setChildPriceFive(priceTextView);
                            }
                        } else {
                            position4 = 0;
                            selectedOption4 = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) {
                            position5 = position;
                            selectedOption5 = itemOptions5List.get(position);
                            if (configProductOptionsArrayList.size() == 5) {
                                setChildPriceFive(priceTextView);
                            }
                        } else {
                            position5 = 0;
                            selectedOption5 = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loginStatus.equals("true")) {
                            if (configProductOptionsArrayList.size() == 2 && position1 != 0 &&
                                    position2 != 0) {
                                myDialog.dismiss();
                                addToCartConfig();
                            } else if (configProductOptionsArrayList.size() == 3 && position1 != 0 &&
                                    position2 != 0 && position3 != 0) {
                                myDialog.dismiss();
                                addToCartConfig();
                            } else if (configProductOptionsArrayList.size() == 4 && position1 != 0 &&
                                    position2 != 0 && position3 != 0 && position4 != 0) {
                                myDialog.dismiss();
                                addToCartConfig();
                            } else if (configProductOptionsArrayList.size() == 5 && position1 != 0 &&
                                    position2 != 0 && position3 != 0 && position4 != 0 && position5 != 0) {
                                myDialog.dismiss();
                                addToCartConfig();
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "All options are mandatory.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                            goToLogin();
                        }
                    }
                });

                Collections.sort(configProductOptionsArrayList, new Comparator<ConfigProductOptions>() {
                    @Override
                    public int compare(ConfigProductOptions p0, ConfigProductOptions p1) {
                        return Integer.valueOf(p0.getPosition()).compareTo(Integer.valueOf(p1.getPosition()));
                    }
                });

                for (int j = 0; j < configProductOptionsArrayList.size(); j++) {
                    ConfigProductOptions configProductOptions = configProductOptionsArrayList.get(j);
                    String label = configProductOptions.getLabel();
                    ArrayList<ConfigValues> configValuesArrayList = configProductOptions.getValues();
                    ArrayList<ConfigItemOptions> itemOptionsList = new ArrayList<ConfigItemOptions>();
                    ArrayList<String> itemNameList = new ArrayList<String>();
                    itemNameList.add("Choose an option");
                    for (int j1 = 0; j1 < configValuesArrayList.size(); j1++) {
                        ConfigValues configValues = configValuesArrayList.get(j1);
                        ConfigValuesExtensionAttributes configValuesExtensionAttributes = configValues.getExtension_attributes();
                        String label1 = configValuesExtensionAttributes.getLabel();
                        ConfigItemOptions itemOptions = new ConfigItemOptions(
                                configValues.getValue_index(),
                                configProductOptions.getAttribute_id(),
                                label1
                        );
                        itemOptionsList.add(itemOptions);
                        itemNameList.add(label1);
                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(ProductDetailActivity.this,
                                    android.R.layout.simple_spinner_item, itemNameList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    if (j == 0) {
                        main1.setVisibility(View.VISIBLE);
                        text1.setText(label);
                        spinner1.setEnabled(true);
                        spinner1.setAdapter(adapter);
                        itemOptions1List.clear();
                        itemOptions1List = itemOptionsList;
                    }
                    if (j == 1) {
                        main2.setVisibility(View.VISIBLE);
                        text2.setText(label);
                        spinner2.setEnabled(true);
                        spinner2.setAdapter(adapter);
                        itemOptions2List.clear();
                        itemOptions2List = itemOptionsList;
                    }
                    if (j == 2) {
                        main3.setVisibility(View.VISIBLE);
                        text3.setText(label);
                        spinner3.setEnabled(true);
                        spinner3.setAdapter(adapter);
                        itemOptions3List.clear();
                        itemOptions3List = itemOptionsList;
                    }
                    if (j == 3) {
                        main4.setVisibility(View.VISIBLE);
                        text4.setText(label);
                        spinner4.setEnabled(true);
                        spinner4.setAdapter(adapter);
                        itemOptions4List.clear();
                        itemOptions4List = itemOptionsList;
                    }
                    if (j == 4) {
                        main5.setVisibility(View.VISIBLE);
                        text5.setText(label);
                        spinner5.setEnabled(true);
                        spinner5.setAdapter(adapter);
                        itemOptions5List.clear();
                        itemOptions5List = itemOptionsList;
                    }

                }
            }
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showCartPopUp();
                    myDialog.show();
                }
            });

            productChildren();
        } else {
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCartSimple();
//                    if (loginStatus.equals("true")) {
//                        addToCartSimple();
//                    } else {
//                        Toast.makeText(ProductDetailActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
//                        goToLogin();
//                    }
                }
            });
        }

        ArrayList<CustomAttributes> customAttributesArrayList = productDetail.getCustom_attributes();
        for (int i = 0; i < customAttributesArrayList.size(); i++) {
            CustomAttributes customAttributes = customAttributesArrayList.get(i);
            String attribute_code = customAttributes.getAttribute_code();
            if (attribute_code.equals("meta_description")) {
                meta_description = customAttributes.getValue().toString();
            }

        }

        ArrayList<MediaGalleryEntries> mediaGalleryEntriesArrayList = productDetail.getMedia_gallery_entries();
        for (int j = 0; j < mediaGalleryEntriesArrayList.size(); j++) {
            MediaGalleryEntries mediaGalleryEntries = mediaGalleryEntriesArrayList.get(j);
            if (mediaGalleryEntries.getMedia_type().equals("image")) {
                file = mediaGalleryEntries.getFile();
                if (file.contains(",")) {
                    String[] f_image = file.split(",");
                    pref.putString("product_images", Arrays.toString(f_image));
                } else {
                    pref.putString("product_images", file);
                }
                break;
            }

        }

        pr_name.setText(name);
        pr_sku.setText(sku);
        pr_price.setText(price);
        pr_description.setText(meta_description);

        if (productDetail.getExtension_attributes() != null &&
                productDetail.getExtension_attributes().getStock_item() != null
        ) {
            if (productDetail.getExtension_attributes().getStock_item().getIs_in_stock()) {
                pr_avail.setText("In stock");
            } else {
                pr_avail.setText("No stock");
            }
        } else {
            if (status.equals("0")) {
                pr_avail.setText("No stock");
            } else {
                pr_avail.setText("In stock");
            }
        }

        if (price.contains(".")) {
            pr_price.setText("KD " + price);
        } else {
            pr_price.setText("KD " + price + ".00");
        }

        //product_image_slider

        viewPager = (ViewPager) findViewById(R.id.viewPager1);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots1);

        ProductImageSlider viewPagerAdapter = new ProductImageSlider(ProductDetailActivity.this);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getApplication());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot_product));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot_product));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        addWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginStatus.equals("true")) {
                    addToWishList(productDetail.getId());
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Please login to proceed.", Toast.LENGTH_SHORT).show();
                    goToLogin();
                }
            }
        });
    }

    /*public void showCartPopUp() {

        myDialog.setContentView(R.layout.productdetail_popup);
        Button submit = myDialog.findViewById(R.id.submit_btn);

        Spinner spinner = myDialog.findViewById(R.id.spinner_one);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(ProductDetailActivity.this,
                        android.R.layout.simple_spinner_item, origin);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner2 = myDialog.findViewById(R.id.spinner_two);
        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<String>(ProductDetailActivity.this,
                        android.R.layout.simple_spinner_item, packing);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter1);

        TextView textView = myDialog.findViewById(R.id.close_three);

        TextView priceTextView = myDialog.findViewById(R.id.config_child_price);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        *//*Button button = myDialog.findViewById(R.id.submit_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, Cart_activity.class);
                startActivity(intent);

            }
        });*//*

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(myDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.gravity = Gravity.BOTTOM;

        myDialog.getWindow().setAttributes(lp);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrigin = originList.get(position);
                nameOrigin = origin.get(position);
                setChildPrice(priceTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPacking = packingList.get(position);
                namePacking = packing.get(position);
                setChildPrice(priceTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDialog.dismiss();
                addToCartConfig();
                *//*final Intent mainIntent = new Intent(ProductDetailActivity.this, Cart_activity.class);
                ProductDetailActivity.this.startActivity(mainIntent);*//*
            }
        });

    }*/

    /*private void checkChildren(String value) {
        if (productChildrenList != null && productChildrenList.size() > 0) {
            for (int index = 0; index < productChildrenList.size(); index++) {
                PriceChildren priceChildren = productChildrenList.get(index);
                ArrayList<CustomAttributes> customList = new ArrayList<CustomAttributes>();
                ArrayList<CustomAttributes> customAttributesList = priceChildren.getCustom_attributes();
                for (int i = 0; i < customAttributesList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesList.get(i);
                    String valueId = customAttributes.getValue().toString();
                    if (valueId.equals(value)) {
                        customList.add(customAttributes);
                    }
                }
                if (originId.equals(sOriginId) && packingId.equals(sPackingId)) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        newPrice = "Price : KD " + newPrice;
                    } else {
                        newPrice = "Price : KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                }
                *//*if (priceChildren.getName().equals(newChildComboName)) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        newPrice = "Price : KD " + newPrice;
                    } else {
                        newPrice = "Price : KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                }*//*
            }
        }
    }*/

    private void setChildPrice(TextView childPriceTextView) {
        String newChildComboName = nameProduct + "-" + nameOrigin + "-" + namePacking;
        boolean flag = true;
        if (productChildrenList != null && productChildrenList.size() > 0 &&
                selectedOrigin != null && selectedPacking != null) {
            String originId = selectedOrigin.getOption_value();
            String packingId = selectedPacking.getOption_value();
            String sOriginId = "";
            String sPackingId = "";
            for (int index = 0; index < productChildrenList.size(); index++) {
                PriceChildren priceChildren = productChildrenList.get(index);
                ArrayList<CustomAttributes> customAttributesList = priceChildren.getCustom_attributes();
                for (int i = 0; i < customAttributesList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesList.get(i);
                    if (customAttributes.getAttribute_code().equals("origin")) {
                        sOriginId = customAttributes.getValue().toString();
                    }
                    if (customAttributes.getAttribute_code().equals("packing")) {
                        sPackingId = customAttributes.getValue().toString();
                    }

                }
                if (originId.equals(sOriginId) && packingId.equals(sPackingId)) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        newPrice = "Price : KD " + newPrice;
                    } else {
                        newPrice = "Price : KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                }
                /*if (priceChildren.getName().equals(newChildComboName)) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        newPrice = "Price : KD " + newPrice;
                    } else {
                        newPrice = "Price : KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                }*/
            }
        }
        if (flag) {
            String newPrice = price;
            if (newPrice.contains(".")) {
                newPrice = "Price : KD " + newPrice;
            } else {
                newPrice = "Price : KD " + newPrice + ".000";
            }
            childPriceTextView.setText(newPrice);
            pr_price.setText(newPrice);
        }
    }

    private void setChildPriceTwo(TextView childPriceTextView) {
        boolean flag = true;
        if (productChildrenList != null && productChildrenList.size() > 0 &&
                selectedOption1 != null && selectedOption2 != null) {
            String option1 = selectedOption1.getOption_value();
            String option2 = selectedOption2.getOption_value();
            for (int index = 0; index < productChildrenList.size(); index++) {
                boolean flag1 = false;
                boolean flag2 = false;
                PriceChildren priceChildren = productChildrenList.get(index);
                ArrayList<CustomAttributes> customAttributesList = priceChildren.getCustom_attributes();
                for (int i = 0; i < customAttributesList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesList.get(i);
                    if (customAttributes.getValue().toString().equals(option1)) {
                        flag1 = true;
                    }
                    if (customAttributes.getValue().toString().equals(option2)) {
                        flag2 = true;
                    }

                }
                if (flag1 && flag2) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        try {
                            Float subFloat = Float.parseFloat(newPrice);
                            String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                            newPrice = "KD " + subAmt;
                        } catch (Exception e) {
                            newPrice = "KD " + newPrice;
                            e.printStackTrace();
                        }
                    } else {
                        newPrice = "KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            String newPrice = price;
            if (newPrice.contains(".")) {
                try {
                    Float subFloat = Float.parseFloat(newPrice);
                    String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                    newPrice = "KD " + subAmt;
                } catch (Exception e) {
                    newPrice = "KD " + newPrice;
                    e.printStackTrace();
                }
            } else {
                newPrice = "KD " + newPrice + ".000";
            }
            childPriceTextView.setText(newPrice);
            pr_price.setText(newPrice);
        }
    }

    private void setChildPriceThree(TextView childPriceTextView) {
        boolean flag = true;
        if (productChildrenList != null && productChildrenList.size() > 0 &&
                selectedOption1 != null && selectedOption2 != null && selectedOption3 != null) {
            String option1 = selectedOption1.getOption_value();
            String option2 = selectedOption2.getOption_value();
            String option3 = selectedOption3.getOption_value();
            for (int index = 0; index < productChildrenList.size(); index++) {
                boolean flag1 = false;
                boolean flag2 = false;
                boolean flag3 = false;
                PriceChildren priceChildren = productChildrenList.get(index);
                ArrayList<CustomAttributes> customAttributesList = priceChildren.getCustom_attributes();
                for (int i = 0; i < customAttributesList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesList.get(i);
                    if (customAttributes.getValue().toString().equals(option1)) {
                        flag1 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option2)) {
                        flag2 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option3)) {
                        flag3 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }

                }
                if (flag1 && flag2&& flag3) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        try {
                            Float subFloat = Float.parseFloat(newPrice);
                            String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                            newPrice = "KD " + subAmt;
                        } catch (Exception e) {
                            newPrice = "KD " + newPrice;
                            e.printStackTrace();
                        }
                    } else {
                        newPrice = "KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            String newPrice = price;
            if (newPrice.contains(".")) {
                try {
                    Float subFloat = Float.parseFloat(newPrice);
                    String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                    newPrice = "KD " + subAmt;
                } catch (Exception e) {
                    newPrice = "KD " + newPrice;
                    e.printStackTrace();
                }
            } else {
                newPrice = "KD " + newPrice + ".000";
            }
            childPriceTextView.setText(newPrice);
            pr_price.setText(newPrice);
        }
    }

    private void setChildPriceFour(TextView childPriceTextView) {
        boolean flag = true;
        if (productChildrenList != null && productChildrenList.size() > 0 &&
                selectedOption1 != null && selectedOption2 != null &&
                selectedOption3 != null && selectedOption4 != null) {
            String option1 = selectedOption1.getOption_value();
            String option2 = selectedOption2.getOption_value();
            String option3 = selectedOption3.getOption_value();
            String option4 = selectedOption4.getOption_value();
            for (int index = 0; index < productChildrenList.size(); index++) {
                boolean flag1 = false;
                boolean flag2 = false;
                boolean flag3 = false;
                boolean flag4 = false;
                PriceChildren priceChildren = productChildrenList.get(index);
                ArrayList<CustomAttributes> customAttributesList = priceChildren.getCustom_attributes();
                for (int i = 0; i < customAttributesList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesList.get(i);
                    if (customAttributes.getValue().toString().equals(option1)) {
                        flag1 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option2)) {
                        flag2 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option3)) {
                        flag3 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option4)) {
                        flag4 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }

                }
                if (flag1 && flag2 && flag3&& flag4) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        try {
                            Float subFloat = Float.parseFloat(newPrice);
                            String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                            newPrice = "KD " + subAmt;
                        } catch (Exception e) {
                            newPrice = "KD " + newPrice;
                            e.printStackTrace();
                        }
                    } else {
                        newPrice = "KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            String newPrice = price;
            if (newPrice.contains(".")) {
                try {
                    Float subFloat = Float.parseFloat(newPrice);
                    String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                    newPrice = "KD " + subAmt;
                } catch (Exception e) {
                    newPrice = "KD " + newPrice;
                    e.printStackTrace();
                }
            } else {
                newPrice = "KD " + newPrice + ".000";
            }
            childPriceTextView.setText(newPrice);
            pr_price.setText(newPrice);
        }
    }

    private void setChildPriceFive(TextView childPriceTextView) {
        boolean flag = true;
        if (productChildrenList != null && productChildrenList.size() > 0 &&
                selectedOption1 != null && selectedOption2 != null &&
                selectedOption3 != null && selectedOption4 != null && selectedOption5 != null) {
            String option1 = selectedOption1.getOption_value();
            String option2 = selectedOption2.getOption_value();
            String option3 = selectedOption3.getOption_value();
            String option4 = selectedOption4.getOption_value();
            String option5 = selectedOption5.getOption_value();
            for (int index = 0; index < productChildrenList.size(); index++) {
                boolean flag1 = false;
                boolean flag2 = false;
                boolean flag3 = false;
                boolean flag4 = false;
                boolean flag5 = false;
                PriceChildren priceChildren = productChildrenList.get(index);
                ArrayList<CustomAttributes> customAttributesList = priceChildren.getCustom_attributes();
                for (int i = 0; i < customAttributesList.size(); i++) {
                    CustomAttributes customAttributes = customAttributesList.get(i);
                    if (customAttributes.getValue().toString().equals(option1)) {
                        flag1 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option2)) {
                        flag2 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option3)) {
                        flag3 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option4)) {
                        flag4 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }
                    if (customAttributes.getValue().toString().equals(option5)) {
                        flag5 = true;
                        Log.e("attribute_code " + i, customAttributes.getAttribute_code());
                    }

                }
                if (flag1 && flag2 && flag3 && flag4 && flag5) {
                    String newPrice = priceChildren.getPrice();
                    if (newPrice.contains(".")) {
                        try {
                            Float subFloat = Float.parseFloat(newPrice);
                            String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                            newPrice = "KD " + subAmt;
                        } catch (Exception e) {
                            newPrice = "KD " + newPrice;
                            e.printStackTrace();
                        }
                    } else {
                        newPrice = "KD " + newPrice + ".000";
                    }
                    childPriceTextView.setText(newPrice);
                    pr_price.setText(newPrice);
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            String newPrice = price;
            if (newPrice.contains(".")) {
                try {
                    Float subFloat = Float.parseFloat(newPrice);
                    String subAmt = String.format(Locale.ENGLISH, "%.3f", subFloat);
                    newPrice = "KD " + subAmt;
                } catch (Exception e) {
                    newPrice = "KD " + newPrice;
                    e.printStackTrace();
                }
            } else {
                newPrice = "KD " + newPrice + ".000";
            }
            childPriceTextView.setText(newPrice);
            pr_price.setText(newPrice);
        }
    }

    private void fetchProductDetail() {
        String productSKU = pref.getString(PREF_ZABEEL_PRODUCT_SKU);
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String query = productSKU;
        try {
            query = URLEncoder.encode(productSKU, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_PRODUCT_DETAIL + query;

        new NetworkManager(this).doGetCustom(
                null,
                getUrl,
                ProductDetail.class,
                null,
                accessToken,
                "TAG_PRODUCT_DETAIL",
                REQUEST_PRODUCT_DETAIL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void addToCartSimple() {

        JSONObject map = new JSONObject();

        try {
            JSONObject cartItem = new JSONObject();
            cartItem.put("sku", pr_sku.getText().toString());
            cartItem.put("qty", count.getText().toString());
            if (loginStatus.equals("true"))
                cartItem.put("quote_id", pref.getString(PREF_ZABEEL_CART_ID));
            else {
                String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
                cartItem.put("quote_id", cartId);
            }
            map.put("cartItem", cartItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = "";
        if (loginStatus.equals("true")){
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_ADD_CART_SIMPLE;
        }else {
            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
            url = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/items";
        }
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        new NetworkManager(this).doPostCustom(
                url,
                JsonObject.class,
                map,
                accessToken,
                "TAG_ADD_CART_SIMPLE",
                REQUEST_ADD_CART_SIMPLE,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");

    }

    private void addToCartConfig() {

        JSONObject map = new JSONObject();

        try {
            JSONArray configItemArray = new JSONArray();

            if (selectedOption1 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption1.getOption_value());
                configItem.put("option_id",selectedOption1.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption2 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption2.getOption_value());
                configItem.put("option_id",selectedOption2.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption3 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption3.getOption_value());
                configItem.put("option_id",selectedOption3.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption4 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption4.getOption_value());
                configItem.put("option_id",selectedOption4.getOption_id());
                configItemArray.put(configItem);
            }

            if (selectedOption5 != null) {
                JSONObject configItem = new JSONObject();
                configItem.put("option_value",selectedOption5.getOption_value());
                configItem.put("option_id",selectedOption5.getOption_id());
                configItemArray.put(configItem);
            }

            JSONObject extensionAttribute = new JSONObject();

            extensionAttribute.put("configurable_item_options", configItemArray);

            JSONObject productOption = new JSONObject();

            productOption.put("extension_attributes", extensionAttribute);

            JSONObject cartItem = new JSONObject();
            cartItem.put("sku", pr_sku.getText().toString());
            cartItem.put("qty", count.getText().toString());
            cartItem.put("quote_id", pref.getString(PREF_ZABEEL_CART_ID));
            cartItem.put("product_option", productOption);
            map.put("cartItem", cartItem);

            Log.d("doJSON Request", map.toString());

            Log.d("configItemArray", configItemArray.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("error", "" + e.getMessage());
        }
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        new NetworkManager(this).doPostCustom(
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_POST_ADD_CART_CONFIG,
                JsonObject.class,
                map,
                accessToken,
                "TAG_ADD_CART_CONFIG",
                REQUEST_ADD_CART_CONFIG,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");

    }

    private void addToWishList(String productId) {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        String postUrl = Apis.API_POST_ADD_WISH_LIST + productId;

        new NetworkManager(this).doPostCustom(
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_ADD_WISH_LIST",
                REQUEST_ADD_WISH_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void productChildren() {
        String productSKU = pref.getString(PREF_ZABEEL_PRODUCT_SKU);
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String postUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CONFIG_PRODUCT_CHILD + productSKU + "/children";

        new NetworkManager(this).doGetCustom(
                null,
                postUrl,
                Object.class,
                null,
                accessToken,
                "TAG_ADD_CONFIG_DETAIL_CHILD",
                REQUEST_CONFIG_DETAIL_CHILD,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchCartList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        if (accessToken.isEmpty()){
            fetchGuestCartList();
        }else {
            new NetworkManager(this).doGetCustom(
                    null,
                    Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CART_LIST_ITEMS,
                    Object.class,
                    null,
                    accessToken,
                    "TAG_CART_LIST",
                    REQUEST_CART_LIST,
                    this
            );
        }
        //LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchGuestCartList() {
        String accessToken = pref.getString(PREF_ZABEEL_GUEST_TOKEN);

        String query = accessToken.replaceAll("\"","");
        new NetworkManager(this).doGetCustom(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_GUEST_CART_LIST_ITEMS + query+"/items",
                Object.class,
                null,
                accessToken,
                "TAG_CART_LIST",
                REQUEST_CART_LIST,
                this
        );
        //LoadingDialog.showLoadingDialog(this, "Loading...");
    }
    private void processJsonCartList(String response) {
        if (response == null || response.equals("null")) {
            Log.e(TAG, "processJson: Cant get CartList");
            LoadingDialog.cancelLoading();
            cart_btn.clearBadge();
            pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<CartItem>>() {
                }.getType();
                ArrayList<CartItem> cartList = gson.fromJson(response, type);
                if (cartList != null && cartList.size() > 0) {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, cartList.size());
                    cart_btn.setBadgeValue(cartList.size());
                } else {
                    pref.putInt(PREF_ZABEEL_CART_COUNT, 0);
                    cart_btn.clearBadge();
                }
            } catch (Exception e) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {
                    }.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonCartList", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void processJsonProductDetail(String response) {
        if (response != null && !response.equals("null")) {
            Log.d(TAG, "processJson: " + response);
            LoadingDialog.cancelLoading();
            try {
                Type type = new TypeToken<ProductDetail>() {
                }.getType();
                ProductDetail productDetail = gson.fromJson(response, type);
                setUpDetailPage(productDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonAddToCart(String response) {
        if (response != null && !response.equals("null")) {
            try {
                Type type = new TypeToken<CartItem>(){}.getType();
                CartItem cartResponse = gson.fromJson(response, type);
                Log.d("cartResponse", response);
                LoadingDialog.cancelLoading();
                if (cartResponse.getName() != null) {
                    Toast.makeText(this, cartResponse.getName() + " was added to cart.", Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    //startActivity(intent);
                    fetchCartList();
                } else {
                    dialogWarning(this, cartResponse.getMessage());
                }
            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonAddToCart", e2.getMessage());
                    e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonWishList(String response) {
        Log.d("WishList Response : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            if (wishBool) {
                Toast.makeText(this, "Added to wishlist successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Oops something went wrong.", Toast.LENGTH_SHORT).show();
            }
            LoadingDialog.cancelLoading();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>(){}.getType();
                ErrorMessageResponse cartData = gson.fromJson(response, type);
                if (cartData != null) {
                    String message = cartData.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                LoadingDialog.cancelLoading();
                e1.printStackTrace();
            }
        }
    }

    private void processJsonConfigChild(String response) {
        Log.d("ConfigChild Response : ", response);
        if (response == null || response.equals("null")) {
            LoadingDialog.cancelLoading();
            dialogWarning(this, "Sorry ! Can't connect to server, try later");
        } else {
            try {
                Type type = new TypeToken<ArrayList<PriceChildren>>() {
                }.getType();
                ArrayList<PriceChildren> childrenList = gson.fromJson(response, type);
                productChildrenList.clear();
                if (childrenList != null && childrenList.size() > 0) {
                    productChildrenList = childrenList;
                }
                LoadingDialog.cancelLoading();
            } catch (Exception e) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {
                    }.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonConfigChild", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(ProductDetailActivity.this, "Sorry ! Can't connect to server, try later");
    }

    /*private void relatedproducts() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config_urls.categoryProducts, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    Log.d("responsecategoryprd", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray items = jsonObject.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject jsonObject1 = items.getJSONObject(i);
                            String sku = jsonObject1.getString("sku");
                            String id = jsonObject1.getString("id");
                            String name = jsonObject1.getString("name");
                            String price = jsonObject1.getString("price");
                            String status = jsonObject1.getString("status");

                            if (sku.equals(pro_name)) {
                                JSONArray media_gallery_entries = jsonObject1.getJSONArray("media_gallery_entries");
                                for (int j = 0; j < media_gallery_entries.length(); j++) {
                                    JSONObject jsonObject2 = media_gallery_entries.getJSONObject(j);
                                    file = jsonObject2.getString("file");

                                }
                                JSONArray custom_attributes = jsonObject1.getJSONArray("custom_attributes");
                                for (int i1 = 0; i1 < custom_attributes.length(); i1++) {
                                    JSONObject jsonObject2 = custom_attributes.getJSONObject(i1);
                                    String attributecode = jsonObject2.getString("attribute_code");
                                    if (attributecode.equals("description")) {
                                        value1 = jsonObject2.getString("value");

                                    }
                                }
                                JSONArray product_links = jsonObject1.getJSONArray("product_links");
                                for (int i1 = 0; i1 < product_links.length(); i1++) {
                                    JSONObject jsonObject2 = product_links.getJSONObject(i1);
                                    if (product_links.length() == 0) {

                                        Log.d("errfv", product_links.toString());
                                        Log.d("errfv1", jsonObject2.toString());
                                        skurelatedarray.add("");
                                        link_typearray.add("");
                                        linkedproductskuarray.add("");
                                        linkedproducttypearray.add("");
                                        positionarray.add("");

                                        similarpro.setVisibility(View.GONE);
                                        similartext.setVisibility(View.GONE);
                                    } else {
                                        Log.d("errfv2", product_links.toString());
                                        Log.d("errfv3", jsonObject2.toString());
                                        skurelated = jsonObject2.getString("sku");
                                        Log.d("dfki", skurelated);
                                        link_type = jsonObject2.getString("link_type");
                                        linked_product_sku = jsonObject2.getString("linked_product_sku");
                                        linked_product_type = jsonObject2.getString("linked_product_type");
                                        position = jsonObject2.getString("position");


                                        product_name.add(skurelated);
                                        Log.d("ptok", product_name.toString());
                                        product_prcie.add(price);
                                        product_image.add(file);
                                        availability.add(status);
                                        descriptionarray.add(value1);

                                    }
                                }


                                link_typearray.add(link_type);
                                linkedproductskuarray.add(linked_product_sku);
                                linkedproducttypearray.add(linked_product_type);
                                positionarray.add(position);

                                Similar_products_adapter customAdapter = new Similar_products_adapter(ProductDetailActivity.this, product_name,
                                        product_prcie, product_image, availability, descriptionarray);
                                recyclerView.setAdapter(customAdapter);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("related", "not found");
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyerror", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer dnmcysxdvkc26neptgm325r8iqmzfqv4");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }*/


    private void productDetail() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, detail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("responseproduct", response);
                if (response != null) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String sku = jsonObject.getString("sku");
                        id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        price = jsonObject.getString("price");
                        status = jsonObject.getString("status");


                        JSONObject extension_attributes = jsonObject.getJSONObject("extension_attributes");
                        JSONArray configurable_product_options = extension_attributes.getJSONArray("configurable_product_options");

                        for (int j = 0; j < configurable_product_options.length(); j++) {
                            JSONObject jsonObject1 = configurable_product_options.getJSONObject(j);
                            String label = jsonObject1.getString("label");
                            if (label.equals("Packing")) {
                                JSONArray values = jsonObject1.getJSONArray("values");
                                for (int j1 = 0; j1 < values.length(); j1++) {
                                    JSONObject jsonObject2 = values.getJSONObject(j1);
                                    JSONObject extension_attributes1 = jsonObject2.getJSONObject("extension_attributes");
                                    String label1 = extension_attributes1.getString("label");
                                    packing.add(label1);
                                }
                            } else if (label.equals("Origin")) {
                                JSONArray values = jsonObject1.getJSONArray("values");
                                for (int j1 = 0; j1 < values.length(); j1++) {
                                    JSONObject jsonObject2 = values.getJSONObject(j1);
                                    JSONObject extension_attributes1 = jsonObject2.getJSONObject("extension_attributes");
                                    String label1 = extension_attributes1.getString("label");
                                    origin.add(label1);
                                }
                            }

                        }

                        JSONArray custom_attributes = jsonObject.getJSONArray("custom_attributes");
                        for (int i = 0; i < custom_attributes.length(); i++) {
                            JSONObject jsonObject1 = custom_attributes.getJSONObject(i);
                            String attribute_code = jsonObject1.getString("attribute_code");
                            if (attribute_code.equals("meta_description")) {
                                meta_description = jsonObject1.getString("value");
                            }

                        }

                        JSONArray media_gallery_entries = jsonObject.getJSONArray("media_gallery_entries");
                        for (int j = 0; j < media_gallery_entries.length(); j++) {
                            JSONObject jsonObject2 = media_gallery_entries.getJSONObject(j);
                            file = jsonObject2.getString("file");

                            Log.d("nnnnnn", file);
                            if (file.contains(",")) {
                                String f_image[] = file.split(",");
                                pref.putString("product_images", Arrays.toString(f_image));

                            } else {
                                pref.putString("product_images", file);
                            }

                            Log.d("test", file);

                            pr_name.setText(name);
                            pr_sku.setText(sku);
                            pr_price.setText(price);
                            pr_description.setText(meta_description);
                            if (status.equals("0")) {
                                pr_avail.setText("No stock");
                            } else {
                                pr_avail.setText("In stock");
                            }

                            if (price.contains(".")) {
                                pr_price.setText("KD " + price);
                            } else {
                                pr_price.setText("KD " + price + ".00");
                            }

                            //product_image_slider

                            for (int i = 0; i < product_images.length; i++)
                                ImagesArray.add(product_images[i]);
                            viewPager = (ViewPager) findViewById(R.id.viewPager1);

                            sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots1);

                            ProductImageSlider viewPagerAdapter = new ProductImageSlider(ProductDetailActivity.this);

                            viewPager.setAdapter(viewPagerAdapter);

                            dotscount = viewPagerAdapter.getCount();
                            dots = new ImageView[dotscount];

                            for (int i = 0; i < dotscount; i++) {

                                dots[i] = new ImageView(getApplication());
                                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot_product));

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                params.setMargins(8, 0, 8, 0);

                                sliderDotspanel.addView(dots[i], params);

                            }

                            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {

                                    for (int i = 0; i < dotscount; i++) {
                                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot_product));
                                    }

                                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("exception", e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volleyerror", error.toString());
                Toast.makeText(ProductDetailActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("urlllll", detail);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }


            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer dnmcysxdvkc26neptgm325r8iqmzfqv4");
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

            } catch (Exception e1) {
                Log.e("processJsonLogin", e1.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = new Pref(this);
        loginStatus = pref.getString(PREF_USER_LOGIN_STATUS);

        if (loginStatus.equals("true")) {
            fetchCartList();
            handler.postDelayed( runnable = new Runnable() {
                public void run() {
                    fetchLogin();
                    handler.postDelayed(runnable, delay);
                }
            }, 200);
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_PRODUCT_DETAIL) {
                    processJsonProductDetail(response);
                } else if (requestId == REQUEST_ADD_CART_SIMPLE) {
                    processJsonAddToCart(response);
                } else if (requestId == REQUEST_ADD_CART_CONFIG) {
                    processJsonAddToCart(response);
                } else if (requestId == REQUEST_ADD_WISH_LIST) {
                    processJsonWishList(response);
                } else if (requestId == REQUEST_CONFIG_DETAIL_CHILD) {
                    processJsonConfigChild(response);
                } else if (requestId == REQUEST_CART_LIST) {
                    processJsonCartList(response);
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

    }
}


